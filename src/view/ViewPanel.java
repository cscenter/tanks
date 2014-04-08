package view;

import model.*;
import io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ViewPanel extends JPanel {
    private GameModel model;
    private Timer timer;   

    public ViewPanel() {
        super();

        model = new GameModel();
        GameModelReader.parse(model, "map.txt");
        
        model.addBot(2);
		model.addBot(2);
		model.addBot(2);
		
		model.addPlayer(1);
		final int playerID = model.getPlayerID();
        
        timer = new Timer(100, new ActionListener() {
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
