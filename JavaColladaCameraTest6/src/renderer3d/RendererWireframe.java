package renderer3d;

import collada.Triangle;
import math.Vec4;
import math.Mat4;
import collada.Camera;
import collada.Geometry;
import java.awt.Graphics2D;

/**
 * RendererWireframe class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class RendererWireframe {

    private final Camera camera;
    private final Mat4 viewport = new Mat4();
    private final Mat4 projection = new Mat4();
    private final Mat4 model = new Mat4();
    private final Mat4 mvp = new Mat4();
    private final Vec4 pa = new Vec4();
    private final Vec4 pb = new Vec4();
    private final Vec4 pc = new Vec4();
    
    public RendererWireframe(Camera camera, int width, int height) {
        this.camera = camera;
        double aspectRatio = width / (double) height;
        projection.setPerspective(camera.getFov(), aspectRatio, camera.getzNear(), camera.getzFar());
        viewport.setViewportTransformation(0, 0, width, height);
    }
    
    public void draw(Graphics2D g, Geometry geometry, Triangle face) {
        model.setIdentity();
        model.multiply(geometry.getWorldTransform());
                
        mvp.set(projection);
        mvp.multiply(camera.getWorldTransform());
        mvp.multiply(model);
        
        pa.set(face.getPoints()[0].getPoint());
        pb.set(face.getPoints()[1].getPoint());
        pc.set(face.getPoints()[2].getPoint());
        
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
        
        g.setColor(geometry.getColor());
        g.drawLine((int) pa.x, (int) pa.y, (int) pb.x, (int) pb.y);
        g.drawLine((int) pb.x, (int) pb.y, (int) pc.x, (int) pc.y);
        g.drawLine((int) pc.x, (int) pc.y, (int) pa.x, (int) pa.y);
    }
    
}
