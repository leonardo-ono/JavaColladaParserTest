package collada;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import math.Vec4;

/**
 * Geometry class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Geometry extends Entity {
    
    private final String id;
    private String verticesData; // = "1 1 1 1 1 -1 1 -1 1 1 -1 -1 -1 1 1 -1 1 -1 -1 -1 1 -1 -1 -1";
    private int trianglesCount;
    private String trianglesData; // = "4 0 0 2 0 1 0 0 2 2 1 3 7 1 4 3 1 5 6 2 6 5 2 7 7 2 8 1 3 9 7 3 10 5 3 11 0 4 12 3 4 13 1 4 14 4 5 15 1 5 16 5 5 17 4 0 18 6 0 19 2 0 20 2 1 21 6 1 22 7 1 23 6 2 24 4 2 25 5 2 26 1 3 27 3 3 28 7 3 29 0 4 30 2 4 31 3 4 32 4 5 33 0 5 34 1 5 35";
    private Vec4[] vertices;
    private List<Triangle> triangles = new ArrayList<>();
    private Color color;
    
    public Geometry(String id, int verticesCount, String verticesData) {
        this.id = id;
        this.verticesData = verticesData;
        this.vertices = new Vec4[verticesCount];
        parseVerticesData();
    }

    public Color getColor() {
        return color;
    }

    public String getId() {
        return id;
    }

    private void parseVerticesData() {
        int colorR = 0;
        int colorG = 0;
        int colorB = 0;
        String parsedData[] = verticesData.split(" ");
        for (int i = 0; i < vertices.length; i++) {
            Vec4 p = new Vec4();
            p.x = Double.parseDouble(parsedData[i * 3 + 2]);
            p.y = Double.parseDouble(parsedData[i * 3 + 1]);
            p.z = -Double.parseDouble(parsedData[i * 3 + 0]);
            
            //p.x = Double.parseDouble(parsedData[i * 3 + 0]);
            //p.y = Double.parseDouble(parsedData[i * 3 + 1]);
            //p.z = Double.parseDouble(parsedData[i * 3 + 2]);
            
            p.w = 1;
            vertices[i] = p;
            
            colorR += p.x * 50;
            //colorG += p.y * 50;
            colorB += p.z * 50;
        }
        colorR = colorR > 255 ? 255 : colorR < 0 ? 0 : colorR;
        colorG = colorG > 255 ? 255 : colorG < 0 ? 0 : colorG;
        colorB = colorB > 255 ? 255 : colorB < 0 ? 0 : colorB;
        color = new Color(colorR, colorG, colorB, 255);
    }

    public void addTriangles(int trianglesCount, String trianglesData) {
        this.trianglesData = trianglesData;
        this.trianglesCount = trianglesCount;
        //this.triangles = new Triangle[trianglesCount];
        parseTriangleData();
    }
    
    private void parseTriangleData() {
        String parsedData[] = trianglesData.split(" ");
        int offset = parsedData.length / (3 * trianglesCount);
        for (int i = 0; i < trianglesCount; i++) {
            Vec4 a = vertices[Integer.parseInt(parsedData[i * (offset * 3) + (0 * offset)])];
            Vec4 b = vertices[Integer.parseInt(parsedData[i * (offset * 3) + (1 * offset)])];
            Vec4 c = vertices[Integer.parseInt(parsedData[i * (offset * 3) + (2 * offset)])];
            Triangle triangle = new Triangle(a, b, c);
            triangles.add(triangle);
        }
    }
    
    public Vec4[] getPoints() {
        return vertices;
    }

    public List<Triangle> getFaces() {
        return triangles;
    }
    
}
