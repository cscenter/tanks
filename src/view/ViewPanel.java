package view;

import model.*;
import io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ViewPanel extends JPanel {
    private GameModel model;
    private javax.swing.Timer timer;
    
    private boolean isOver = false;
    private int k = 64 / GameModel.DISCRETE_FACTOR;
    
    private static final int TIMER_DELAY = 50;
    private ImageGallery gallery;
        
    public ViewPanel() {
        super();
        
        model = new GameModel();
        GameModelReader.parse(model, "map.txt");
        
        gallery = new ImageGallery("sprites");
        
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
        
        Image img = gallery.getBackgroundImage();
        
        for (int i = 0; i < height; i += GameModel.DISCRETE_FACTOR) {
            for (int j = 0; j < width; j += GameModel.DISCRETE_FACTOR) {
                g.drawImage(img, j * k, i * k, null);
            }
        }
        
        int x;
        int y;
        for (GameObject obj : model.getGameObjects()) {
            
            img = gallery.getImage(obj);
            
            x = obj.getPosition().getX();
            y = obj.getPosition().getY();
            g.drawImage(img, y * k, x * k, null);
        }
        
        if (model.isPlayerAlive()) {
            img = gallery.getHeartImage();
            for (int i = 0; i < model.getPlayerHealth(); ++i) {
                g.drawImage(img, i * k * GameModel.DISCRETE_FACTOR, height * k, null);
            }
        }
        
        g.drawString("SCORE: " + Integer.toString(model.getScore()), 6 * k * GameModel.DISCRETE_FACTOR, (height + GameModel.DISCRETE_FACTOR / 2 + 1) * k);
    }
}



















