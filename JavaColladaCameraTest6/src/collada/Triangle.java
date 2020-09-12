package collada;

/**
 * Triangle class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Triangle {

    private final Vertex[] points = new Vertex[3];
    private Material material;
    
    public Triangle(Vertex a, Vertex b, Vertex c) {
        points[0] = a;
        points[1] = b;
        points[2] = c;
    }

    public Vertex[] getPoints() {
        return points;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
    
}
