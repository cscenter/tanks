package tanks.model.view;

import java.util.*;
import tanks.model.*;
import java.awt.Dimension;
import java.awt.BorderLayout;
 
import javax.swing.JFrame;


public class ViewFrame extends JFrame {
    
    private ViewPanel panel;
    
    public ViewFrame(String title) {
        super(title);
        panel = new ViewPanel();
    }
    
    public void showGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        panel.setLayout(new BorderLayout());
        panel.setFocusable(true);
        
        panel.setPreferredSize(new Dimension(400, 400));
        add(panel);
        pack();
        setVisible(true);
    }
}
