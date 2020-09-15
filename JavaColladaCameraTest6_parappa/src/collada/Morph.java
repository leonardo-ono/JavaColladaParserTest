package collada;

import java.awt.Graphics2D;
import renderer3d.Renderer;

/**
 * Morph class.
 * 
 * Morph Target Animation.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Morph extends Node {

    protected String animationData; 
    protected Geometry[] animations = new Geometry[0];
    protected double frame = 0;

    public Morph(String id, Node parent) {
        super(id, parent);
    }

    public void setAnimationData(int framesCount, String animationData, Collada collada) {
        this.animationData = animationData;
        this.animations = new Geometry[framesCount];
        parseAnimation(collada);
    }

    private void parseAnimation(Collada collada) {
        String parsedData[] = animationData.split(" ");
        for (int i = 0; i < animations.length; i++) {
            Geometry geometry = collada.getLibraries().getGeometries().get("#" + parsedData[i]);
            animations[i] = geometry;
        }
    }    

    public double getFrame() {
        return frame;
    }

    public void setFrame(double frame) {
        this.frame = frame;
    }

    @Override
    public void update() {
        frame += 60.0 / 30.0;
        if (frame > animations.length - 1) {
            frame = 0; //animations.length - 1;
        }        
        // System.out.println("frame: " + ((int) frame));
    }

    @Override
    public void draw(Graphics2D g, Renderer renderer) {
        Geometry geometry = animations[(int) frame];
        for (Triangle face : geometry.getFaces()) {
            renderer.draw(g, geometry, face);
        }
    }
    
}
