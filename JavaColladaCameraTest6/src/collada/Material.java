package collada;

/**
 * Material class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Material {
    
    private final String id;
    private Texture texture;

    public Material(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
    
}
