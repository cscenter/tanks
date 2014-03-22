import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class line {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(); 
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame f = new JFrame("Mouse tracking");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        f.add(new MyPanel());
        f.pack();
        f.setVisible(true);
    } 
}

class MyPoint {
    private int x;
    private int y;
    
    public MyPoint(int x1, int y1) {
        x = x1;
        y = y1;
    }
    
    public int getX() {
        return x;
    }
    
    public void setX(int x1) {
        x = x1;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y1) {
        y = y1;
    }
}

class MyPanel extends JPanel {

    private List<MyPoint> points = new ArrayList<MyPoint>();

    public MyPanel() {

        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                clear();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                addPoint(e.getX(),e.getY());
            }
        });  
    }
    
    private void clear() {
        points.clear();
        repaint();
    }
    
    private void addPoint(int x, int y) {
        int OFFSET = 1;
        if (points.size() != 0) {
            MyPoint lastpoint = points.get(points.size() - 1);
            if ((lastpoint.getX()!=x) || (lastpoint.getY()!=y)) {
                points.add(new MyPoint(x, y));
                repaint();
            }
        } else
        {
            points.add(new MyPoint(x, y));
        }
    }
    

    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);       
        
        g.setColor(Color.RED);
        for (int i = 1; i < points.size(); ++i) {
            g.drawLine(points.get(i - 1).getX(), points.get(i - 1).getY(), points.get(i).getX(), points.get(i).getY());
        }
    }  
}
