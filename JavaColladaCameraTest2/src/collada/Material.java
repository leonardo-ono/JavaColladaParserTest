package collada;

import java.awt.image.BufferedImage;

/**
 * Material class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Material {
    
    private final String id;
    private BufferedImage texture;

    public Material(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public BufferedImage getTexture() {
        return texture;
    }

    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }
    
}
