package collada;

import java.util.HashMap;
import java.util.Map;

/**
 * Libraries class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Libraries {
   
    private final Map<String, Entity> entities = new HashMap<>();
    private final Map<String, Camera> cameras = new HashMap<>();
    private final Map<String, Geometry> geometries = new HashMap<>();

    public Libraries() {
    }

    public Map<String, Entity> getEntities() {
        return entities;
    }

    public Map<String, Camera> getCameras() {
        return cameras;
    }

    public void addCamera(Camera camera) {
        cameras.put(camera.getId(), camera);
        entities.put(camera.getId(), camera);
    }
    
    public Map<String, Geometry> getGeometries() {
        return geometries;
    }

    public void addGeometry(Geometry geometry) {
        geometries.put(geometry.getId(), geometry);
        entities.put(geometry.getId(), geometry);
    }
    
}
