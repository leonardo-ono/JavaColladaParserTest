package main;

import audio.SoundManager;
import audio.Sounds;
import collada.Animation;
import collada.Camera;
import collada.Collada;
import collada.ColladaLoader;
import collada.Node;
import collada.NodeExtender;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import renderer3d.Renderer;
import renderer3d.RendererWireframe;

/**
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class View extends Canvas implements KeyListener, NodeExtender {
    
    private BufferStrategy bs;
    
    private BufferedImage image;
    private boolean running;
    
    private Collada collada;
    private Camera camera;
    private RendererWireframe rendererWireframe; 
    private Renderer renderer;
    
    private long startTime;
    
    public View() {
        setBackground(Color.BLACK);
    }
    
    public void start() {
        try {
            // Global Orientation: +Y Forward / +Z Up
            collada = ColladaLoader.load("/res/resident_evil_door_2.dae");
            Animation animation = collada.getLibraries().getAnimations().get("door");
            animation.setExtender(this);
            
            camera = collada.getLibraries().getCameras().get("#Camera-camera");
            
            rendererWireframe = new RendererWireframe(camera, getWidth(), getHeight());
            renderer = new Renderer(camera, getWidth(), getHeight());
            
            SoundManager.getInstance().start();
            
        } catch (Exception ex) {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        
        addKeyListener(this);
        
        int width = getWidth();
        int height = getHeight();
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        createBufferStrategy(1);
        bs = getBufferStrategy();
        running = true;
        
        new Thread(() -> {
            while (running) {
                update();
                
                draw((Graphics2D) image.getGraphics());
                Graphics2D g = (Graphics2D) bs.getDrawGraphics();
                
                Graphics2D ig = (Graphics2D) image.getGraphics();

                //ig.drawImage(image, 0, 0, 400, 300, 0, 0, width, height, this);
                g.drawImage(image, 0, 0, getWidth(), getHeight(), 0, 0, 800, 600, null);
                
                g.dispose();
                bs.show();
                
                try {
                    Thread.sleep(1000 / 60);
                } catch (InterruptedException ex) {
                }
            }
        }).start();
        
        startTime = System.currentTimeMillis() + 2000;
    }

    private void update() {
        if (System.currentTimeMillis() < startTime) {
            return;
        }

        collada.getScene().updateInternal();
    }
    
    private void draw(Graphics2D g) {
        if (System.currentTimeMillis() < startTime) {
            return;
        }

        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, getWidth(), getHeight());
        renderer.getDepthBuffer().clear();
        // collada.getScene().drawWireframeInternal(g, rendererWireframe);
        collada.getScene().drawInternal(g, renderer);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View view = new View();
            view.setPreferredSize(new Dimension(800, 600));
            JFrame frame = new JFrame();
            frame.setTitle("Collada Animation Test #2");
            frame.getContentPane().add(view);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            view.requestFocus();
            view.start();
        });
    }    

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_0:
                break;
            case KeyEvent.VK_1:
                break;
            case KeyEvent.VK_2:
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private int previousFrame = 0;
    
    @Override
    public void nodeUpdate(Node node) {
        Animation animation = (Animation) node;
        animation.getFrame();
        if (previousFrame != (int) animation.getFrame()) {
            previousFrame = (int) animation.getFrame();
            if (previousFrame == 42) {
                SoundManager.getInstance().play(Sounds.DOOR_OPEN);
            }
            else if (previousFrame == 150) {
                SoundManager.getInstance().play(Sounds.DOOR_CLOSE);
            }
        }
    }
    
}
