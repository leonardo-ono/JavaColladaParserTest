package test;

import collada.Camera;
import collada.Collada;
import collada.ColladaLoader;
import collada.Geometry;
import collada.Triangle;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class View extends Canvas implements KeyListener {
    
    private BufferStrategy bs;
    
    private BufferedImage image;
    private boolean running;
    
    private Collada collada;
    private Camera camera;
    private Renderer renderer; // = new Renderer(null);
    
    public View() {
    }
    
    public void start() {
        try {
            // TODO: Global Orientation: +Y Forward / +Z Up
            collada = ColladaLoader.load("/res/star_fox_4.dae");
            
            camera = collada.getLibraries().getCameras().get("Camera");
            renderer = new Renderer(camera);
        } catch (Exception ex) {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        
        addKeyListener(this);
        
        int width = 800;
        int height = 600;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        createBufferStrategy(2);
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
    }

    private void update() {
        collada.getLibraries().getEntities().values().forEach((geometry) -> {
            geometry.update();
        });
    }
    
    private void draw(Graphics2D g) {
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, getWidth(), getHeight());
        
        collada.getLibraries().getGeometries().values().forEach((geometry) -> {
            for (Triangle face : geometry.getFaces()) {
                renderer.draw(g, geometry, face);
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View view = new View();
            view.setPreferredSize(new Dimension(800, 600));
            JFrame frame = new JFrame();
            frame.setTitle("Collada Animation Test #1");
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
    
}
