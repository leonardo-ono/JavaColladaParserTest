package collada;

import math.Mat4;

/**
 * Animation class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Animation extends Node {

    protected String animationData; 
    protected Mat4[] animations = new Mat4[0];
    protected double frame = 0;
    
    public Animation(String id, Node parent) {
        super(id, parent);
    }
    
    public void setAnimationData(int framesCount, String animationData, boolean invertMatrix) {
        this.animationData = animationData;
        this.animations = new Mat4[framesCount];
        parseAnimation(invertMatrix);
    }

    private void parseAnimation(boolean invertMatrix) {
        String parsedData[] = animationData.split(" ");
        for (int i = 0; i < animations.length; i++) {
            Mat4 animation = new Mat4();
            animation.m00 = Double.parseDouble(parsedData[i * 16 + 0]);
            animation.m01 = Double.parseDouble(parsedData[i * 16 + 1]);
            animation.m02 = Double.parseDouble(parsedData[i * 16 + 2]);
            animation.m03 = Double.parseDouble(parsedData[i * 16 + 3]);
            animation.m10 = Double.parseDouble(parsedData[i * 16 + 4]);
            animation.m11 = Double.parseDouble(parsedData[i * 16 + 5]);
            animation.m12 = Double.parseDouble(parsedData[i * 16 + 6]);
            animation.m13 = Double.parseDouble(parsedData[i * 16 + 7]);
            animation.m20 = Double.parseDouble(parsedData[i * 16 + 8]);
            animation.m21 = Double.parseDouble(parsedData[i * 16 + 9]);
            animation.m22 = Double.parseDouble(parsedData[i * 16 + 10]);
            animation.m23 = Double.parseDouble(parsedData[i * 16 + 11]);
            animation.m30 = Double.parseDouble(parsedData[i * 16 + 12]);
            animation.m31 = Double.parseDouble(parsedData[i * 16 + 13]);
            animation.m32 = Double.parseDouble(parsedData[i * 16 + 14]);
            animation.m33 = Double.parseDouble(parsedData[i * 16 + 15]);
            if (invertMatrix) {
                animation.invert();
            }
            animations[i] = animation;
        }
    }    

    public double getFrame() {
        return frame;
    }

    public void setFrame(double frame) {
        this.frame = frame;
    }

    public void updateLocalTransform() {
        if (animations.length == 0) {
            return;
        }
        localTransform.set(animations[(int) frame]);
    }

    @Override
    public void update() {
        frame += 60.0 / 60.0;
        if (frame > animations.length - 1) {
            frame = 0; //animations.length - 1;
        }        
        updateLocalTransform();
    }
    
}
