package collada;

import math.Vec3;
import math.Vec4;

/**
 * Vertex class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Vertex {

    private final Vec4 point;
    private final Vec3 st;
    private final Vec4 vn;

    public Vertex(Vec4 point, Vec3 st, Vec4 vn) {
        this.point = point;
        this.st = st;
        this.vn = vn;
    }

    public Vec4 getPoint() {
        return point;
    }

    public Vec3 getSt() {
        return st;
    }

    public Vec4 getVn() {
        return vn;
    }

    @Override
    public String toString() {
        return "Vertex{" + "point=" + point + ", st=" + st + ", vn=" + vn + '}';
    }
    
}
