package tanks.model.view;

import java.util.*;
import tanks.model.*;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ViewPanel extends JPanel {
    private GameModel model;
    
    public ViewPanel() {
        super();
        /// warning !!!
        model = new GameModel();
        GameModelReader.parse(model, "map.txt");
        model.addTank("Player");
        
        addKeyListener(new KeyAdapter() {
 
            public void keyPressed(KeyEvent e) {               
                switch (e.getKeyCode()) {
                case KeyEvent.VK_W :
                    model.moveTank("Player", Direction.UP);
                    break;
                case KeyEvent.VK_A :
                    model.moveTank("Player", Direction.LEFT);
                    break;
                case KeyEvent.VK_S :
                    model.moveTank("Player", Direction.DOWN);
                    break;
                case KeyEvent.VK_D :
                    model.moveTank("Player", Direction.RIGHT);
                    break;
                case KeyEvent.VK_SPACE :
                    model.shoot("Player");
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
