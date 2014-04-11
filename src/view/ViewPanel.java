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
    private Image backgroundImage;
    private Image waterImage;
    private Image stoneImage;
    private Image heartImage;
    private Image treeImage;
    private EnumMap<Direction, Image> greenTankImage;
    private EnumMap<Direction, Image> redTankImage;
    private EnumMap<Direction, Image> projectileImage;
    
    private boolean isOver = false;
    private int k = 64 / GameModel.DISCRETE_FACTOR;
    
    private static final int TIMER_DELAY = 50;
    
    private void initImages() {
        try {
            backgroundImage = ImageIO.read(new File("sprites//ground//ground.png"));
            waterImage = ImageIO.read(new File("sprites//water//water.png"));
            stoneImage = ImageIO.read(new File("sprites//stone//stone.png"));
            treeImage = ImageIO.read(new File("sprites//tree//tree.png"));
            heartImage = ImageIO.read(new File("sprites//health//heart.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        greenTankImage = new EnumMap<Direction, Image>(Direction.class);
        redTankImage = new EnumMap<Direction, Image>(Direction.class);
        projectileImage = new EnumMap<Direction, Image>(Direction.class);
        for (Direction d : Direction.values()) {            
                try {
                    String filename;
                    filename = "sprites//tank//red//tank" + d.toString() + ".png";
                    redTankImage.put(d, ImageIO.read(new File(filename)));
                    filename = "sprites//tank//green//tank" + d.toString() + ".png";
                    greenTankImage.put(d, ImageIO.read(new File(filename)));
                    filename = "sprites//projectile//projectile" + d.toString() + ".png";
                    projectileImage.put(d, ImageIO.read(new File(filename)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }        
    }
        
    public ViewPanel() {
        super();
        
        initImages();
        
        model = new GameModel();
        GameModelReader.parse(model, "map.txt");
        
        model.start();
        
        timer = new javax.swing.Timer(TIMER_DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                model.tick();
                repaint();
                if (!model.isPlayerAlive()) {
                    JOptionPane.showMessageDialog(ViewPanel.this,
                        "Oops, you have been killed...\n Your score: " + model.getScore(),
                        "Game over",
                        JOptionPane.PLAIN_MESSAGE);
                    timer.stop();
                    isOver = true;
                }
            }
        });
        
        timer.start();
        
        addKeyListener(new KeyAdapter() {
 
            public void keyPressed(KeyEvent e) {               
                if (isOver) {
                    return;
                }
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
        return new Dimension(model.getWidth() * k, (model.getHeight() + GameModel.DISCRETE_FACTOR)* k);
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);       
        
        int width = model.getWidth();
        int height = model.getHeight();

        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
        
        for (int i = 0; i < height; i += GameModel.DISCRETE_FACTOR) {
            for (int j = 0; j < width; j += GameModel.DISCRETE_FACTOR) {
                g.drawImage(backgroundImage, j * k, i * k, null);
            }
        }
        
        int x;
        int y;
        Image img = null;
        for (GameObject obj : model.getGameObjects()) {
            
            switch (obj.getDescription()) {
            case WATER:
                img = waterImage;
                break;
            case STONE:
                img = stoneImage;
                break;
            case TREE:
                img = treeImage;
                break;
            case TANK:
                Tank t = (Tank) obj;
                if (t.getTeam() == 1) {
                    img = greenTankImage.get(Direction.fromVector2D(t.getOrientation()));
                } else {
                    img = redTankImage.get(Direction.fromVector2D(t.getOrientation()));
                }
                break;
            case PROJECTILE:
                img = projectileImage.get(Direction.fromVector2D(((Projectile)obj).getOrientation()));
                break;
            }
            x = obj.getPosition().getX();
            y = obj.getPosition().getY();
            g.drawImage(img, y * k, x * k, null);
        }
        
        if (model.isPlayerAlive()) {
            for (int i = 0; i < model.getPlayerHealth(); ++i) {
                g.drawImage(heartImage, i * k * GameModel.DISCRETE_FACTOR, height * k, null);
            }
        }
        
        g.drawString("SCORE: " + Integer.toString(model.getScore()), 6 * k * GameModel.DISCRETE_FACTOR, (height + GameModel.DISCRETE_FACTOR / 2 + 1) * k);
    }
}



















