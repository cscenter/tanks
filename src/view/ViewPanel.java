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
        
        //model.addBot(2);
		//model.addBot(2);
		model.addBot(2);
		
		model.addPlayer(1);
		final int playerID = model.getPlayerID();
        
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
                    model.moveTank(playerID, Direction.UP);
                    break;
                case KeyEvent.VK_A :
                    model.moveTank(playerID, Direction.LEFT);
                    break;
                case KeyEvent.VK_S :
                    model.moveTank(playerID, Direction.DOWN);
                    break;
                case KeyEvent.VK_D :
                    model.moveTank(playerID, Direction.RIGHT);
                    break;
                case KeyEvent.VK_SPACE :
                    model.shoot(playerID);
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

        boolean visited[][] = new boolean[height][];
        for (int i = 0; i < height; ++i) {
            visited[i] = new boolean[width];
        }
        int k = 64 / 3;
        Image img = images.get(GameObjectDescription.ASPHALT).get(Direction.DOWN);
        for (int i = 0; i < height; i += 3) {
            for (int j = 0; j < width; j += 3) {
                g.drawImage(img, j * k, i * k, null);
            }
        }
        
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                if (!visited[i][j]) {
                    GameObject obj = model.getGameObject(i, j);
                    if (obj != null) {
                        img = images.get(obj.getDescription()).get(Direction.fromVector2D(obj.getOrientation()));
                        g.drawImage(img, j * k, i * k, null);
                        for (int i1 = 0; i1 < obj.getHeight() && i + i1 < height; ++i1) {
                            for (int j1 = 0; j1 < obj.getWidth() && j + j1 < width; ++j1) {
                                visited[i + i1][j + j1] = true;
                            }
                        }
                    }                    
                }
            }
        }
    }
}



















