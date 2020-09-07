package collada;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Libraries class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Libraries {
   
    private final Map<String, Camera> cameras = new HashMap<>();
    private final Map<String, BufferedImage> images = new HashMap<>();
    private final Map<String, Effect> effects = new HashMap<>();
    private final Map<String, Material> materials = new HashMap<>();
    private final Map<String, Geometry> geometries = new HashMap<>();
    private final Map<String, Animation> animations = new HashMap<>();
    private final Map<String, VisualScene> visualScenes = new HashMap<>();

    public Libraries() {
    }

    public Map<String, Camera> getCameras() {
        return cameras;
    }

    public void addCamera(Camera camera) {
        cameras.put(camera.getId(), camera);
    }

    public Map<String, BufferedImage> getImages() {
        return images;
    }

    public void addImage(String imageId, String imageRes) {
        try {
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/res/image/" + imageRes));
            images.put(imageId, image);
        } catch (IOException ex) {
            Logger.getLogger(Libraries.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }    

    public Map<String, Effect> getEffecs() {
        return effects;
    }

    public void addEffect(Effect effect) {
        effects.put(effect.getId(), effect);
    }

    public Map<String, Material> getMaterials() {
        return materials;
    }

    public void addMaterial(Material material) {
        materials.put(material.getId(), material);
    }
    
    public Map<String, Animation> getAnimations() {
        return animations;
    }
    
    public void addAnimation(Animation animation) {
        animations.put(animation.getId(), animation);
    }
    
    
    public Map<String, Geometry> getGeometries() {
        return geometries;
    }

    public void addGeometry(Geometry geometry) {
        geometries.put(geometry.getId(), geometry);
    }

    public Map<String, VisualScene> getVisualScenes() {
        return visualScenes;
    }
    
    public void addVisualScene(VisualScene visualScene) {
        visualScenes.put(visualScene.getId(), visualScene);
                
    }
    
}
