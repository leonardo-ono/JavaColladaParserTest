package collada;

/**
 * Collada class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Collada {
    
    private final Libraries libraries = new Libraries();
    private VisualScene scene;
    
    public Collada() {
    }

    public Libraries getLibraries() {
        return libraries;
    }

    public VisualScene getScene() {
        return scene;
    }

    public void setScene(VisualScene scene) {
        this.scene = scene;
    }

}
