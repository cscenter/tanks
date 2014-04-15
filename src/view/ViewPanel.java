package view;

import model.*;
import io.GameModelReader;
import io.ImageGallery;
import io.MapIOException;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@SuppressWarnings("serial")
public class ViewPanel extends JPanel {
    private GameModel model;
    private javax.swing.Timer timer;
    
    private boolean gamePaused = false;
    private boolean gameStarted = false;
    

    private int k = 64 / GameModel.DISCRETE_FACTOR;
    
    private static final int TIMER_DELAY = 50;
    private ImageGallery gallery;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    public ViewPanel(PropertyChangeListener gameStartedListener, PropertyChangeListener gamePausedListener) {
        super();
        
        model = new GameModel();
        try {
            gallery = new ImageGallery("sprites");
        } catch (MapIOException e) {
            JOptionPane.showMessageDialog(ViewPanel.this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        timer = new javax.swing.Timer(TIMER_DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                model.tick();
                repaint();
                if (!model.isPlayerAlive()) {
                    JOptionPane.showMessageDialog(ViewPanel.this,
                        "Oops, you have been killed...\n Your score: " + model.getScore(),
                        "Game over",
                        JOptionPane.PLAIN_MESSAGE);
                    timer.stop();
                    setGameStarted(false);
                    // TODO send message to Frame about game ending
                }
            }
        });
        
        pcs.addPropertyChangeListener("gamePaused", gamePausedListener);
        pcs.addPropertyChangeListener("gameStarted", gameStartedListener);
    }
    
    public boolean isGamePaused() {
        return gamePaused;
    }

    public void setGamePaused(boolean gamePaused) {
        boolean oldValue = this.gamePaused; 
        this.gamePaused = gamePaused;
        pcs.firePropertyChange("gamePaused", oldValue, gamePaused);
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        boolean oldValue = this.gameStarted; 
        this.gameStarted = gameStarted;
        pcs.firePropertyChange("gameStarted", oldValue, gameStarted);
    }
    
    public void unpause() {
        timer.start();
        setGamePaused(false);
    }
    
    public void pause() {
        timer.stop();
        setGamePaused(true);
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(model.getWidth() * k, (model.getHeight() + GameModel.DISCRETE_FACTOR)* k);
    }
    
    private boolean isGameOn() {
        return !isGamePaused() && isGameStarted();
    }
    
    public void start() {
        try {
            GameModelReader.parse(model, "map.txt");
            model.start();
            timer.start();
            setGamePaused(false);
            setGameStarted(true);
            addKeyListener(new KeyAdapter() {
     
                public void keyPressed(KeyEvent e) {               
                    switch (e.getKeyCode()) {
                    case KeyEvent.VK_W :
                        if (isGameOn()) {
                            model.movePlayer(Direction.UP);
                        }
                        break;
                    case KeyEvent.VK_A :
                        if (isGameOn()) {
                            model.movePlayer(Direction.LEFT);
                        }
                        break;
                    case KeyEvent.VK_S :
                        if (isGameOn()) {
                            model.movePlayer(Direction.DOWN);
                        }
                        break;
                    case KeyEvent.VK_D :
                        if (isGameOn()) {
                            model.movePlayer(Direction.RIGHT);
                        }
                        break;
                    case KeyEvent.VK_SPACE :
                        if (isGameOn()) {
                            model.shootPlayer();
                        }
                        break;
                    case KeyEvent.VK_P :
                        if (isGamePaused()) {
                            unpause();
                        } else {
                            pause();
                        }
                        break;
                    
                    case KeyEvent.VK_O :
                        model.debugprint();
                        break;
                    }    
                }
            });
        } catch (MapIOException e) {
            JOptionPane.showMessageDialog(ViewPanel.this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);       
        
        int width = model.getWidth();
        int height = model.getHeight();

        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
        
        Image img = gallery.getBackgroundImage();
        
        for (int i = 0; i < height; i += GameModel.DISCRETE_FACTOR) {
            for (int j = 0; j < width; j += GameModel.DISCRETE_FACTOR) {
                g.drawImage(img, j * k, i * k, null);
            }
        }
        
        int x;
        int y;
        for (GameObject obj : model.getGameObjects()) {
            
            img = gallery.getImage(obj);
            
            x = obj.getPosition().getX();
            y = obj.getPosition().getY();
            g.drawImage(img, y * k, x * k, null);
        }
        
        if (model.isPlayerAlive()) {
            img = gallery.getHeartImage();
            for (int i = 0; i < model.getPlayerHealth(); ++i) {
                g.drawImage(img, i * k * GameModel.DISCRETE_FACTOR, height * k, null);
            }
        }
        
        g.drawString("SCORE: " + Integer.toString(model.getScore()), 6 * k * GameModel.DISCRETE_FACTOR, (height + GameModel.DISCRETE_FACTOR / 2 + 1) * k);
    }
}
