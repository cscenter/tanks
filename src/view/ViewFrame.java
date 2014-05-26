package view;

import io.GameModelGenerator;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import model.GameModel;
import model.ModelException;


@SuppressWarnings("serial")
public class ViewFrame extends JFrame {
    
    private ViewPanel panel;
    
    private static final int RANDOM_MAP_SIZE = 100;
    private static final int RANDOM_MAP_BOTS_COUNT = 40;
    public static final String SCORES_TABLE_FILE = "highscores.txt";
    private static final int MAX_SCORES_TO_PRINT = 10;
    
    private static final int COLOMNS_IN_SCORE_TABLE = 3;
    
    
    private String getHighScores() {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(SCORES_TABLE_FILE)))
        {
            String[] row;
            String line = br.readLine();
            while (line != null) {
                row = line.split(" ");
                rows.add(row);
                line = br.readLine();
            }

            /// name mape score
            Comparator<String[]> cmp = new Comparator<String[]>() {

                @Override
                public int compare(String[] o1, String[] o2) {
                    int score1 = Integer.parseInt(o1[2]);
                    int score2 = Integer.parseInt(o2[2]);
                    return score2 - score1;
                }
                
            };
            Collections.sort(rows, cmp);
            String result = "";
            for (int i = 0; i < Math.min(rows.size(), MAX_SCORES_TO_PRINT); ++i) {
                result += String.valueOf(i + 1);
                for (int j = 0; j < COLOMNS_IN_SCORE_TABLE; ++j) {
                    result += "\t" + rows.get(i)[j];
                }
                result += "\n";
            }
            return result;
        } catch (IOException e) {
            return "Cannot load results ='(";
        }
    }
    
    public static void createMap() {
        try {
            GameModelGenerator.createMap(RANDOM_MAP_SIZE, RANDOM_MAP_SIZE, "randomMap.txt", RANDOM_MAP_BOTS_COUNT);
        } catch (ModelException e1) {
            System.out.println("Error while map generation occuried.");
        }
    }
    
    public ViewFrame() {
        super("Tanks 1.0");
        
        JMenuBar menuBar;
        JMenu menu;
        JMenu newGameSubmenu;
        JMenuItem menuItem;
        final JMenuItem pauseMenuItem = new JMenuItem("Pause");
        final JMenuItem resumeMenuItem = new JMenuItem("Resume");
        
        menuBar = new JMenuBar();
        menu = new JMenu("File");
        
        newGameSubmenu = new JMenu("New Game");
        menuItem = new JMenuItem("Campaign");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	panel.start(GameModel.ModelType.CAMPAIGN, "maps");
                repaint();
                pauseMenuItem.setEnabled(true);
                resumeMenuItem.setEnabled(false);
            }
        });
        newGameSubmenu.add(menuItem);
        
        menuItem = new JMenuItem("Infinite");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createMap();
                panel.start(GameModel.ModelType.INFINITE, "randomMap.txt");
                repaint();
                pauseMenuItem.setEnabled(true);
                resumeMenuItem.setEnabled(false);
            }
        });
        newGameSubmenu.add(menuItem);
        
        menu.add(newGameSubmenu);
        
        pauseMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.pause();
            }
        });
        menu.add(pauseMenuItem);
        
        resumeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.unpause();
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
        
        menu = new JMenu("High Scores");
        menu.add(menu.add(new JMenuItem(new AbstractAction("Show table") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(ViewFrame.this,
                        getHighScores(),
                        "High Scores",
                        JOptionPane.PLAIN_MESSAGE);
            }
        })));
        
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
        
        pauseMenuItem.setEnabled(false);
        resumeMenuItem.setEnabled(false);

        panel = new ViewPanel(gameStaertedListener, gamePausedListener);
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    public void showGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        panel.setLayout(new BorderLayout());
        panel.setFocusable(true);
        
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        
        pack();
        setVisible(true);
    }
}
