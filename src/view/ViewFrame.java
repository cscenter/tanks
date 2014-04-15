package view;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;


@SuppressWarnings("serial")
public class ViewFrame extends JFrame {
    
    private ViewPanel panel;
    
    public ViewFrame() {
        super("Tanks 1.0");
        
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;
        final JMenuItem pauseMenuItem = new JMenuItem("Pause");
        final JMenuItem resumeMenuItem = new JMenuItem("Resume");
        
        menuBar = new JMenuBar();
        menu = new JMenu("File");
        
        menuItem = new JMenuItem("New Game");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.start();
                pack();
                repaint();
                pauseMenuItem.setEnabled(true);
                resumeMenuItem.setEnabled(false);
            }
        });
        menu.add(menuItem);
        
        pauseMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.pause();
                //pauseMenuItem.setEnabled(false);
                //resumeMenuItem.setEnabled(true);
            }
        });
        menu.add(pauseMenuItem);
        
        resumeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.unpause();
                //pauseMenuItem.setEnabled(true);
                //resumeMenuItem.setEnabled(false);
            }
        });
        menu.add(resumeMenuItem);
        
        menu.add(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                for (Frame frame : Frame.getFrames())
                {
                    if (frame.isActive())
                    {
                        WindowEvent windowClosing = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
                        frame.dispatchEvent(windowClosing);
                    }
                }
            }
        });
        
        menuBar.add(menu);
        
        menu = new JMenu("Help");
        menu.add(new JMenuItem(new AbstractAction("Show controls") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(ViewFrame.this,
                        "W - move upward\nA - move left\n" + 
                        "S - move downward\nD - move right\n" + 
                        "SPACE - shoot\nP - pause/unpause",
                        "Controls",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }));
        menu.add(new JMenuItem(new AbstractAction("About") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(ViewFrame.this,
                        "Welcome to TANKS. It is a game with the only one aim:\n" + 
                        "Survive & kill bad red guys as much as you can. I hope" +
                        " you will enjoy it=)\n\n\nYou can contact me by email: mordbergak@gmail.com",
                        "About",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }));
        
        menuBar.add(menu);
        
        setJMenuBar(menuBar);
        
        PropertyChangeListener gamePausedListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent arg0) {
                if (Boolean.TRUE.equals(arg0.getNewValue())) {
                    pauseMenuItem.setEnabled(false);
                    resumeMenuItem.setEnabled(true);
                } else {
                    pauseMenuItem.setEnabled(true);
                    resumeMenuItem.setEnabled(false);
                }                    
            }
        };
        
        PropertyChangeListener gameStaertedListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (Boolean.TRUE.equals(evt.getNewValue())) {
                    pauseMenuItem.setEnabled(true);
                    resumeMenuItem.setEnabled(false);
                } else {
                    pauseMenuItem.setEnabled(false);
                    resumeMenuItem.setEnabled(false);
                }
            }
        };
        
        panel = new ViewPanel(gameStaertedListener, gamePausedListener);
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
