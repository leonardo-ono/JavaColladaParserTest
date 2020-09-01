package collada;

import math.Vec4;

/**
 * Triangle class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Triangle {

    private final Vec4[] points = new Vec4[3];

    public Triangle(Vec4 a, Vec4 b, Vec4 c) {
        points[0] = a;
        points[1] = b;
        points[2] = c;
    }

    public Vec4[] getPoints() {
        return points;
    }
    
}
