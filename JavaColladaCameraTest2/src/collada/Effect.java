package collada;

import java.awt.image.BufferedImage;

/**
 * Effect class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Effect {

    private final String id;
    private String imageId;
    
    public Effect(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
    
}
