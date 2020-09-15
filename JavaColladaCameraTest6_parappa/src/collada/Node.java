package collada;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import math.Mat4;
import renderer3d.Renderer;
import renderer3d.RendererWireframe;

/**
 * (VisualScene) Node class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Node {
    
    protected final String id;
    protected Node parent;
    protected final List<Node> children = new ArrayList<>();
    protected final Mat4 localTransform = new Mat4();
    protected final Mat4 worldTransform = new Mat4();
    protected NodeExtender extender;
    
    public Node(String id, Node parent) {
        this.id = id;
        this.parent = parent;
        localTransform.setIdentity();
        worldTransform.setIdentity();
    }

    public String getId() {
        return id;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node node) {
        node.setParent(this);
        children.add(node);
    }

    public Mat4 getLocalTransform() {
        return localTransform;
    }

    public void setLocalTransform(String data, boolean invertMatrix) {
        String parsedData[] = data.split(" ");
        localTransform.m00 = Double.parseDouble(parsedData[0]);
        localTransform.m01 = Double.parseDouble(parsedData[1]);
        localTransform.m02 = Double.parseDouble(parsedData[2]);
        localTransform.m03 = Double.parseDouble(parsedData[3]);
        localTransform.m10 = Double.parseDouble(parsedData[4]);
        localTransform.m11 = Double.parseDouble(parsedData[5]);
        localTransform.m12 = Double.parseDouble(parsedData[6]);
        localTransform.m13 = Double.parseDouble(parsedData[7]);
        localTransform.m20 = Double.parseDouble(parsedData[8]);
        localTransform.m21 = Double.parseDouble(parsedData[9]);
        localTransform.m22 = Double.parseDouble(parsedData[10]);
        localTransform.m23 = Double.parseDouble(parsedData[11]);
        localTransform.m30 = Double.parseDouble(parsedData[12]);
        localTransform.m31 = Double.parseDouble(parsedData[13]);
        localTransform.m32 = Double.parseDouble(parsedData[14]);
        localTransform.m33 = Double.parseDouble(parsedData[15]);
        if (invertMatrix) {
            localTransform.invert();
        }
    }    

    public Mat4 getWorldTransform() {
        return worldTransform;
    }

    public NodeExtender getExtender() {
        return extender;
    }

    public void setExtender(NodeExtender extender) {
        this.extender = extender;
    }
    
    public void updateInternal() {
        update();
        if (extender != null) {
            extender.nodeUpdate(this);
        }
        if (parent != null) {
            worldTransform.set(parent.getWorldTransform());
            worldTransform.multiply(localTransform);
        }
        else {
            worldTransform.set(localTransform);
        }
        children.forEach(node -> node.updateInternal());
    }
    
    public void update() {
    }

    public void drawWireframeInternal(Graphics2D g, RendererWireframe renderer) {
        drawWireframe(g, renderer);
        children.forEach(node -> node.drawWireframeInternal(g, renderer));
    }
    
    public void drawWireframe(Graphics2D g, RendererWireframe renderer) {
    }

    public void drawInternal(Graphics2D g, Renderer renderer) {
        draw(g, renderer);
        children.forEach(node -> node.drawInternal(g, renderer));
    }
    
    public void draw(Graphics2D g, Renderer renderer) {
    }
    
}
