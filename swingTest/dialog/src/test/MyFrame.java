package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class MyFrame extends JFrame {

    JPanel panel;

    MyFrame( String title ) {
        super( title );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.setPreferredSize(new Dimension(400, 400));

        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onClick();                
                }
            }
        });
        
        add(panel);
        pack();
        setVisible(true);
    }
    
    public void onClick() {
        MyDialog dialog = new MyDialog(this, "The title", true);
        dialog.setVisible(true);
    }
}
