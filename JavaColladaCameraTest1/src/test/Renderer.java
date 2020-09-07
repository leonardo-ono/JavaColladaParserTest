package test;

import collada.Triangle;
import math.Vec4;
import math.Mat4;
import collada.Camera;
import collada.Geometry;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author admin
 */
public class Renderer {

    private final Camera camera;
    private final Mat4 projection = new Mat4();
    private final Mat4 model = new Mat4();
    private final Mat4 mvp = new Mat4();
    private final Vec4 pa = new Vec4();
    private final Vec4 pb = new Vec4();
    private final Vec4 pc = new Vec4();
    
    public Renderer(Camera camera) {
        this.camera = camera;
        double fov = Math.toRadians(39.59775);
        //projection.setPerspectiveProjection(fov, 800);
        double aspectRatio = 800.0 / 600.0;
        //projection.setFrustum(-0.25, 0.25, -aspectRatio * 0.25, aspectRatio * 0.25, 0.1, 100);
        projection.setPerspective(fov, aspectRatio, 0.1, 100.0);
    }
    
    public void draw(Graphics2D g, Geometry geometry, Triangle face) {
        //model.setTranslation(0, 0, -10);
        if (geometry.getCurrent() != null) {
            Mat4 modelRotation = new Mat4();
            modelRotation.setRotationY(Math.toDegrees(-90));
            model.setIdentity();
            model.multiply(geometry.getCurrent());
            model.multiply(modelRotation);
        }
        else {
            model.setIdentity();
        }
        //model.setScale(0.25, 0.25, 0.25);
                
        mvp.set(projection);
        mvp.multiply(camera.getCurrent());
        mvp.multiply(model);
        
        //mvp.set(camera.getCurrent());
        //mvp.multiply(projection);
        
        pa.set(face.getPoints()[0]);
        pb.set(face.getPoints()[1]);
        pc.set(face.getPoints()[2]);
        mvp.multiply(pa);
        mvp.multiply(pb);
        mvp.multiply(pc);
        
        if (pa.z < 0.1 || pb.z < 0.1 || pc.z < 0.1) {
            return;
        }
        
        pa.doPerspectiveDivision();
        pb.doPerspectiveDivision();
        pc.doPerspectiveDivision();
        
        double width = 8;
        double height = 6;
               
        pa.x = width * pa.x + 400;
        pa.y = 300 - height * pa.y;
        pb.x = width * pb.x + 400;
        pb.y = 300 - height * pb.y;
        pc.x = width * pc.x + 400;
        pc.y = 300 - height * pc.y;
        
        g.setColor(geometry.getColor());
        g.drawLine((int) pa.x, (int) pa.y, (int) pb.x, (int) pb.y);
        g.drawLine((int) pb.x, (int) pb.y, (int) pc.x, (int) pc.y);
        g.drawLine((int) pc.x, (int) pc.y, (int) pa.x, (int) pa.y);
    }
    
    public void update() {
        //camera.update();
    }
    
    
}
