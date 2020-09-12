package collada;

import static collada.Texture.Extrapolation.REPEAT;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

/**
 *
 * @author admin
 */
public class Texture {

    public static enum Extrapolation { REPEAT, EXTEND, CLIP }
    
    private final String id;
    private BufferedImage image;
    private Raster textureRaster;
    private Extrapolation extrapolation = REPEAT;
    
    public Texture(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        textureRaster = image.getRaster();
    }

    public Extrapolation getExtrapolation() {
        return extrapolation;
    }

    public void setExtrapolation(Extrapolation extrapolation) {
        this.extrapolation = extrapolation;
    }
    
    public void getPixel(double s, double t, int[] pxDst) {
        switch (extrapolation) {
            case CLIP:
                // TODO
                break;
            case EXTEND:
                // TODO
                break;
            case REPEAT:
                getPixelRepeat(s, t, pxDst);
                break;
        }
    }
    
    public void getPixelRepeat(double s, double t, int[] pxDst) {
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
        int tx = (int) (s * (image.getWidth() - 1));
        int ty = (int) ((1 - t) * (image.getHeight() - 1));
        textureRaster.getPixel(tx, ty, pxDst);
    }
    
}
