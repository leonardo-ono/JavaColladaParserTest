package collada;

/**
 * Camera class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Camera extends Entity {
    
    private final String id;
    private final double fov; // in radians
    private final double aspectRatio;
    private final double zNear;
    private final double zFar;

    public Camera(String id, double fov, double aspectRatio, double zNear, double zFar) {
        this.id = id;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.zNear = zNear;
        this.zFar = zFar;
    }

    public String getId() {
        return id;
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
    
}
