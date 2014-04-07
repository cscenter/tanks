package view;

import java.util.*;
import model.*;
import io.*;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ViewPanel extends JPanel {
    private GameModel model;
    
    public ViewPanel() {
        super();
        /// warning !!!
        model = new GameModel();
        GameModelReader.parse(model, "map.txt");
        
		
        model.addBot(2);
		model.addBot(2);
		model.addBot(2);
		model.addPlayer(1);
		
		int playerID = model.getPlayerID();
        
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
                
                }
                
                model.tick();
                repaint();
            }
             
        });
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);       
        
        int width = model.getWidth();
        int height = model.getHeight();
        
        g.drawRect(45, 35, 10 + 10 * width, 10 + 10 * height);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                g.drawString("" + model.getLetter(i, j), 50 + j * 10, 50 + i * 10);
            }
        }
    }
}
