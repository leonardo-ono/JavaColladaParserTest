package collada;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import math.Vec3;
import math.Vec4;
import renderer3d.Renderer;
import renderer3d.RendererWireframe;

/**
 * Geometry class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Geometry extends Node {
    
    private final String verticesData; 
    private final String stsData; 
    private final String normalsData;
    
    private final int verticesCount;
    private final int stsCount;
    private final int normalsCount;
    
    private int trianglesCount;
    private String trianglesData; 
    
    private final Vec4[] vertices;
    private final Vec3[] sts;
    private final Vec4[] normals;
    
    private final List<Triangle> triangles = new ArrayList<>();
    private Color color;
    
    public Geometry(String id, Node parent
            , int verticesCount, String verticesData
            , int stsCount, String stsData
            , int normalsCount, String normalsData) {
        
        super(id, parent);
        
        this.verticesData = verticesData;
        this.stsData = stsData;
        this.normalsData = normalsData;
        
        this.verticesCount = verticesCount;
        this.stsCount = stsCount;
        this.normalsCount = normalsCount;
        
        this.vertices = new Vec4[verticesCount];
        this.sts = new Vec3[stsCount];
        this.normals = new Vec4[normalsCount];
        
        parseVerticesData();
        parseStsData();
        parseNormalsData();
    }

    public Color getColor() {
        return color;
    }

    private void parseVerticesData() {
        int colorR = 0;
        int colorG = 0;
        int colorB = 0;
        String parsedData[] = verticesData.split(" ");
        for (int i = 0; i < vertices.length; i++) {
            Vec4 p = new Vec4();
            
            p.x = Double.parseDouble(parsedData[i * 3 + 0]);
            p.y = Double.parseDouble(parsedData[i * 3 + 1]);
            p.z = Double.parseDouble(parsedData[i * 3 + 2]);
            
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

    private void parseStsData() {
        String parsedData[] = stsData.split(" ");
        
        if (parsedData.length <= 0) {
            return;
        }
        
        for (int i = 0; i < sts.length; i++) {
            Vec3 p = new Vec3();
            
            p.x = Double.parseDouble(parsedData[i * 2 + 0]);
            p.y = Double.parseDouble(parsedData[i * 2 + 1]);
            sts[i] = p;
        }
    }

    private void parseNormalsData() {
        String parsedData[] = normalsData.split(" ");
        
        if (parsedData.length <= 0) {
            return;
        }
        
        for (int i = 0; i < normals.length; i++) {
            Vec4 p = new Vec4();
            
            p.x = Double.parseDouble(parsedData[i * 2 + 0]);
            p.y = Double.parseDouble(parsedData[i * 2 + 1]);
            p.z = Double.parseDouble(parsedData[i * 2 + 2]);
            p.w = 1;
            normals[i] = p;
        }
    }
    
    public void addTriangles(int trianglesCount, String trianglesData, Material material) {
        this.trianglesData = trianglesData;
        this.trianglesCount = trianglesCount;
        parseTriangleData(material);
    }
    
    private void parseTriangleData(Material material) {
        String parsedData[] = trianglesData.split(" ");
        int offset = parsedData.length / (3 * trianglesCount);
        for (int i = 0; i < trianglesCount; i++) {
            Vec4 pa = vertices[Integer.parseInt(parsedData[0 + i * (offset * 3) + (0 * offset)])];
            Vec4 pb = vertices[Integer.parseInt(parsedData[0 + i * (offset * 3) + (1 * offset)])];
            Vec4 pc = vertices[Integer.parseInt(parsedData[0 + i * (offset * 3) + (2 * offset)])];

            Vec4 na = normals[Integer.parseInt(parsedData[1 + i * (offset * 3) + (0 * offset)])];
            Vec4 nb = normals[Integer.parseInt(parsedData[1 + i * (offset * 3) + (1 * offset)])];
            Vec4 nc = normals[Integer.parseInt(parsedData[1 + i * (offset * 3) + (2 * offset)])];
            
            Vec3 sta = null;
            Vec3 stb = null;
            Vec3 stc = null;
            if (offset == 3) {
                sta = sts[Integer.parseInt(parsedData[2 + i * (offset * 3) + (0 * offset)])];
                stb = sts[Integer.parseInt(parsedData[2 + i * (offset * 3) + (1 * offset)])];
                stc = sts[Integer.parseInt(parsedData[2 + i * (offset * 3) + (2 * offset)])];
            }
            Vertex va = new Vertex(pa, sta, na);
            Vertex vb = new Vertex(pb, stb, nb);
            Vertex vc = new Vertex(pc, stc, nc);
            Triangle triangle = new Triangle(va, vb, vc);
            triangle.setMaterial(material);
            triangles.add(triangle);
        }
    }
    
    public Vec4[] getPoints() {
        return vertices;
    }

    public List<Triangle> getFaces() {
        return triangles;
    }

    @Override
    public void drawWireframe(Graphics2D g, RendererWireframe renderer) {
        for (Triangle face : getFaces()) {
            renderer.draw(g, this, face);
        }
    }

    @Override
    public void draw(Graphics2D g, Renderer renderer) {
        for (Triangle face : getFaces()) {
            renderer.draw(g, this, face);
        }
    }
 
    
}
