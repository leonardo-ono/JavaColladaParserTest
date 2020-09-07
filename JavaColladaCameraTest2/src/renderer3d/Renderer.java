package renderer3d;

import collada.Camera;
import collada.Geometry;
import collada.Material;
import collada.Triangle;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import math.Mat4;
import math.Vec3;
import math.Vec4;

/**
 * Renderer class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Renderer {

    private final Camera camera;
    private final DepthBuffer depthBuffer;
            
    private final Mat4 viewport = new Mat4();
    private final Mat4 projection = new Mat4();
    private final Mat4 model = new Mat4();
    private final Mat4 mvp = new Mat4();
    private final Vec4 pa = new Vec4();
    private final Vec4 pb = new Vec4();
    private final Vec4 pc = new Vec4();
    
    private Triangle currentTriangle;
    //private final Vec3 normal = new Vec3();
    //private final List<Vertex> vertices = new ArrayList<>();
    private Material material;
    private Raster textureRaster;
    
    private final TriangleRasterizerComposite triangleComposite 
        = new TriangleRasterizerComposite();
    
    public Renderer(Camera camera, int width, int height) {
        this.camera = camera;
        depthBuffer = new DepthBuffer(width, height);
        
        double aspectRatio = width / (double) height;
        projection.setPerspective(camera.getFov(), aspectRatio, camera.getzNear(), camera.getzFar());
        viewport.setViewportTransformation(0, 0, width, height);
    }

    private final Polygon polygonTmp = new Polygon();
    private final Vec3[] screenPoints = { new Vec3(), new Vec3(), new Vec3() };
    private final Vec3 screenPoint = new Vec3();

    Vec4 copyA = new Vec4();
    Vec4 copyB = new Vec4();
    Vec4 copyC = new Vec4();
    Vec4[] copyVs = { copyA, copyB, copyC };

    public DepthBuffer getDepthBuffer() {
        return depthBuffer;
    }
    
    public void draw(Graphics2D g, Geometry geometry, Triangle triangle) {
        
        this.currentTriangle = triangle;
        
        this.material = triangle.getMaterial();
        this.textureRaster = material.getTexture().getRaster();
        
        model.setIdentity();
        model.multiply(geometry.getWorldTransform());
                
        mvp.set(projection);
        mvp.multiply(camera.getWorldTransform());
        mvp.multiply(model);
        
        pa.set(triangle.getPoints()[0].getPoint());
        pb.set(triangle.getPoints()[1].getPoint());
        pc.set(triangle.getPoints()[2].getPoint());
        
        mvp.multiply(pa);
        mvp.multiply(pb);
        mvp.multiply(pc);
        
        if (pa.z < 0.1 || pb.z < 0.1 || pc.z < 0.1) {
            return;
        }
        
        // TODO: frustrum clipping
        
        pa.doPerspectiveDivision();
        pb.doPerspectiveDivision();
        pc.doPerspectiveDivision();
        
        viewport.multiply(pa);
        viewport.multiply(pb);
        viewport.multiply(pc);
        
//        g.setColor(geometry.getColor());
//        g.drawLine((int) pa.x, (int) pa.y, (int) pb.x, (int) pb.y);
//        g.drawLine((int) pb.x, (int) pb.y, (int) pc.x, (int) pc.y);
//        g.drawLine((int) pc.x, (int) pc.y, (int) pa.x, (int) pa.y);
        
        copyVs[0].x = pa.x;
        copyVs[0].y = pa.y;
        copyVs[0].z = -pa.z;
        copyVs[1].x = pb.x;
        copyVs[1].y = pb.y;
        copyVs[1].z = -pb.z;
        copyVs[2].x = pc.x;
        copyVs[2].y = pc.y;
        copyVs[2].z = -pc.z;
        
        polygonTmp.reset();
        for (int index = 0; index < 3; index++) {
            int x = (int) (copyVs[index].x);
            int y = (int) (copyVs[index].y);
            polygonTmp.addPoint(x, y);
            screenPoints[index].set(x, 0, y);
        }

        calculateTotalWeight(screenPoints[0], screenPoints[1], screenPoints[2]);
        
        // back-face culling
        if (wtotal > 0) {
            return;
        }
        
        triangleComposite.set(depthBuffer);
        
        Composite originalComposite = g.getComposite();
        
        g.setComposite(triangleComposite);
        g.fill(polygonTmp);
        
        g.setComposite(originalComposite);
    }
    
    private class TriangleRasterizerComposite implements Composite {
        
        private final TriangleRasterizerCompositeContext context 
            = new TriangleRasterizerCompositeContext();

        public void set(DepthBuffer depthBuffer) {
            context.setDepthBuffer(depthBuffer);
        }
        
        @Override
        public CompositeContext createContext(ColorModel srcColorModel
                , ColorModel dstColorModel, RenderingHints hints) {
            
            return context;
        }
        
    }
    
    private class TriangleRasterizerCompositeContext 
            implements CompositeContext {

        private DepthBuffer depthBuffer;
        private final int[] pxDst = new int[4];

        public void setDepthBuffer(DepthBuffer depthBuffer) {
            this.depthBuffer = depthBuffer;
        }

        @Override
        public void dispose() {
        }

        @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            for (int mx = 0; mx < dstOut.getWidth(); mx++) {
                for (int my = 0; my < dstOut.getHeight(); my++) {
                    int x = mx - dstOut.getSampleModelTranslateX();
                    int y = my - dstOut.getSampleModelTranslateY();

                    screenPoint.set(x, 0, y);
                    calculateWeights(screenPoint
                        , screenPoints[0], screenPoints[1], screenPoints[2]);

                    double z = w0 * copyA.z + w1 * copyB.z + w2 * copyC.z;
                    if (!depthBuffer.update(x, y, z)) {
                        continue;
                    }

                    Vec3 st0 = currentTriangle.getPoints()[0].getSt();
                    Vec3 st1 = currentTriangle.getPoints()[1].getSt();
                    Vec3 st2 = currentTriangle.getPoints()[2].getSt();
                    
                    double s = w0 * st0.x + w1 * st1.x + w2 * st2.x;
                    double t = w0 * st0.y + w1 * st1.y + w2 * st2.y;

                    s = s % 1;
                    t = t % 1;
                    if (s < 0) {
                        int si = (int) s;
                        s -= si - 1;
                    }
                    if (t < 0) {
                        int ti = (int) t;
                        t -= ti - 1;
                    }

                    int tx = (int) (s * (material.getTexture().getWidth() - 1));
                    int ty = (int) ((1 - t) 
                        * (material.getTexture().getHeight() - 1));

                    textureRaster.getPixel(tx, ty, pxDst);

                    dstOut.setPixel(mx, my, pxDst);
                }
            }
        }
        
    }

    private double wtotal;
    private double w0;
    private double w1;
    private double w2;
    private final Vec3 v0 = new Vec3();
    private final Vec3 v1 = new Vec3();
    private final Vec3 v2 = new Vec3();
    
    public void calculateTotalWeight(Vec3 p0, Vec3 p1, Vec3 p2) {
        v0.set(p1);
        v0.sub(p0);
        v1.set(p2);
        v1.sub(p0);
        wtotal = v0.cross2D(v1);
        wtotal = 1 / wtotal;
    }
    
    public void calculateWeights(Vec3 p, Vec3 p0, Vec3 p1, Vec3 p2) {
        v0.set(p0, p);
        v1.set(p1, p);
        v2.set(p2, p);
        w0 = v1.cross2D(v2) * wtotal;
        w1 = v2.cross2D(v0) * wtotal;
        w2 = v0.cross2D(v1) * wtotal;
    }
    
}
