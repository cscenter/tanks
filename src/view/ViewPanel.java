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
        model.addTank("Player", 1);
        
        model.addTank("Semen", 2);
        
        final String[] name = {"Player", "Semen"};
        int i = 0;
        
        addKeyListener(new KeyAdapter() {
 
            public void keyPressed(KeyEvent e) {               
                switch (e.getKeyCode()) {
                case KeyEvent.VK_W :
                    model.moveTank(name[0], Direction.UP);
                    break;
                case KeyEvent.VK_A :
                    model.moveTank(name[0], Direction.LEFT);
                    break;
                case KeyEvent.VK_S :
                    model.moveTank(name[0], Direction.DOWN);
                    break;
                case KeyEvent.VK_D :
                    model.moveTank(name[0], Direction.RIGHT);
                    break;
                case KeyEvent.VK_SPACE :
                    model.shoot(name[0]);
                    break;
                
                }
                String tmp = name[1];
                name[1] = name[0];
                name[0] = tmp;
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
