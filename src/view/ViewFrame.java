package view;

import java.awt.BorderLayout;
import javax.swing.JFrame;


public class ViewFrame extends JFrame {
    
    private ViewPanel panel;
    
    public ViewFrame() {
        super("Tanks 1.0");
        panel = new ViewPanel();
    }
    
    public void showGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        panel.setLayout(new BorderLayout());
        panel.setFocusable(true);
        
        add(panel);
        pack();
        setVisible(true);
    }
}
