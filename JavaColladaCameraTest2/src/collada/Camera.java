package collada;

/**
 * Camera class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Camera extends Animation {
    
    private final double fov; // in degrees
    private final double aspectRatio;
    private final double zNear;
    private final double zFar;

    public Camera(String id, Node parent, double fov, double aspectRatio, double zNear, double zFar) {
        super(id, parent);
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.zNear = zNear;
        this.zFar = zFar;
    }

    public double getFov() {
        return fov;
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public double getzNear() {
        return zNear;
    }

    public double getzFar() {
        return zFar;
    }

    @Override
    public void updateInternal() {
        super.updateInternal(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
