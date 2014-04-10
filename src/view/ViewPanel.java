package view;

import model.*;
import io.*;
import javax.swing.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class ViewPanel extends JPanel {
    private GameModel model;
    private javax.swing.Timer timer;   
    private EnumMap<GameObjectDescription, EnumMap<Direction, Image> > images;
    
    public ViewPanel() {
        super();
        
        images = new EnumMap<GameObjectDescription, EnumMap<Direction, Image>>(GameObjectDescription.class);
        
        for (GameObjectDescription desc : GameObjectDescription.values()) {
            images.put(desc, new EnumMap<Direction, Image>(Direction.class));
            for (Direction d : Direction.values()) {
                String filename = desc.toString().toLowerCase();
                filename = "sprites//" + desc.toString().toLowerCase() + "//" + desc.toString().toLowerCase() + d.toString() + ".png";
                try {
                    images.get(desc).put(d, ImageIO.read(new File(filename)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        model = new GameModel();
        GameModelReader.parse(model, "map.txt");
        
        model.start();
        
        timer = new javax.swing.Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                model.tick();
                repaint();
            }
        });
        
        timer.start();
        
        addKeyListener(new KeyAdapter() {
 
            public void keyPressed(KeyEvent e) {               
                switch (e.getKeyCode()) {
                case KeyEvent.VK_W :
                    model.movePlayer(Direction.UP);
                    break;
                case KeyEvent.VK_A :
                    model.movePlayer(Direction.LEFT);
                    break;
                case KeyEvent.VK_S :
                    model.movePlayer(Direction.DOWN);
                    break;
                case KeyEvent.VK_D :
                    model.movePlayer(Direction.RIGHT);
                    break;
                case KeyEvent.VK_SPACE :
                    model.shootPlayer();
                    break;
                
                
                case KeyEvent.VK_P :
                    model.debugprint();
                    break;
                    
                }    
            }
        });
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(800, 800);
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);       
        
        int width = model.getWidth();
        int height = model.getHeight();

        int k = 64 / 3;
        Image img = images.get(GameObjectDescription.ASPHALT).get(Direction.DOWN);
        for (int i = 0; i < height; i += 3) {
            for (int j = 0; j < width; j += 3) {
                g.drawImage(img, j * k, i * k, null);
            }
        }
        
        int x;
        int y;
        for (GameObject obj : model.getGameObjects()) {
            img = images.get(obj.getDescription()).get(Direction.fromVector2D(obj.getOrientation()));
            x = obj.getPosition().getX();
            y = obj.getPosition().getY();
            g.drawImage(img, y * k, x * k, null);
        }
    }
}



















