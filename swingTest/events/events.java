import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

import static java.lang.System.out;

public class events {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(); 
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame f = new JFrame("Mouse events");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        f.add(new MyPanel());
        f.pack();
        f.setVisible(true);
    } 
}

class MyPanel extends JPanel {

    private List<Rectangle> rects = new ArrayList<Rectangle>();
    private Random generator = new Random();
    
    public MyPanel() {

        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // out.println(e.getClickCount());
                if (e.getClickCount() == 2) {
                    changeBackground();
                } else {
                    addRect();
                }
            }
        });
    }
    
    private void changeBackground() {
        setBackground(new Color(generator.nextInt(256),generator.nextInt(256),generator.nextInt(256),generator.nextInt(256)));
        repaint();
    }
    
    private void addRect() {
        int x = Math.abs(generator.nextInt()) % getWidth();
        int y = Math.abs(generator.nextInt()) % getHeight();
        int width = Math.abs(generator.nextInt()) % (getWidth() - x);
        int height = Math.abs(generator.nextInt()) % (getHeight() - y);

        rects.add(new Rectangle(x, y, width, height));
        repaint();
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);       
                
        Graphics2D g2 = (Graphics2D) g;
        for (Rectangle rect : rects) {
            g2.setColor(new Color(generator.nextInt(256),generator.nextInt(256),generator.nextInt(256),generator.nextInt(256)));
            g2.fill(rect);
        }
    }  
}
