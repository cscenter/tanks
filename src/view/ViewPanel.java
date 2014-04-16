package view;

import io.GameModelReader;
import io.ImageGallery;
import io.MapIOException;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import model.Direction;
import model.GameModel;
import model.GameObject;

@SuppressWarnings("serial")
public class ViewPanel extends JPanel {
    private GameModel model;
    private javax.swing.Timer timer;
    
    private boolean gamePaused = false;
    private boolean gameStarted = false;

    private Keyer keyListener;
    
    private final double k = (64.0 / GameModel.DISCRETE_FACTOR);
    
    private static final int TIMER_DELAY = 10;
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
                    setGamePaused(false);
                    setGameStarted(false);
                }
            }
        });
        
        pcs.addPropertyChangeListener("gamePaused", gamePausedListener);
        pcs.addPropertyChangeListener("gameStarted", gameStartedListener);
        
        initKeyboard();
    }
    
    private class Keyer implements KeyListener {

        HashSet<Integer> pressedKeys = new HashSet<Integer>();
        
        {
            new Timer(10, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    for (Integer keyCode : pressedKeys) {
                        if (keyCode == KeyEvent.VK_P && isGameStarted()) {
                            if (isGamePaused()) {
                                unpause();
                            } else {
                                pause();
                            }
                        }
                        if (!isGameOn()) {
                            return;
                        }
                        switch (keyCode) {
                        case KeyEvent.VK_W :
                            model.movePlayer(Direction.UP);
                            break;
                        case KeyEvent.VK_A :
                            model.movePlayer(Direction.LEFT);
                            break;
                        case KeyEvent.VK_S :
                            model.movePlayer(Direction.DOWN);
                            break;
                        case KeyEvent.VK_D :
                            model.movePlayer(Direction.RIGHT);
                            break;
                        case KeyEvent.VK_SPACE :
                            model.shootPlayer();
                            break;
                        
                        case KeyEvent.VK_O :
                            model.debugprint();
                            break;
                        }
                    }
                }
            }).start();
        }
        
        @Override
        public void keyPressed(KeyEvent ovent) {
            int keyCode = ovent.getKeyCode();
            pressedKeys.add(keyCode);
        }
        @Override
        public void keyReleased(KeyEvent ovent) {
            int keyCode = ovent.getKeyCode();
            pressedKeys.remove(keyCode);
        }
        @Override
        public void keyTyped(KeyEvent ovent) {}
        
        public void reset() {
            pressedKeys.clear();
        }
    }
    
    private void initKeyboard() {
        keyListener = new Keyer();
        
        addKeyListener(keyListener);
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
        repaint();
        setGamePaused(true);
    }
    
    public Dimension getPreferredSize() {
        return new Dimension((int)(model.getWidth() * k), (int)((model.getHeight() + GameModel.DISCRETE_FACTOR)* k));
    }
    
    private boolean isGameOn() {
        return !isGamePaused() && isGameStarted();
    }
    
    public void start(String mapFilename) {
        try {
            GameModelReader.parse(model, mapFilename);
            model.start();
            timer.start();
            keyListener.reset();
            setGamePaused(false);
            setGameStarted(true);
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
                g.drawImage(img, (int)(j * k), (int)(i * k), null);
            }
        }
        
        int x;
        int y;
        for (GameObject obj : model.getGameObjects()) {
            
            img = gallery.getImage(obj);
            
            x = obj.getPosition().getX();
            y = obj.getPosition().getY();
            g.drawImage(img, (int)(y * k), (int)(x * k), null);
        }
        
        if (model.isPlayerAlive()) {
            img = gallery.getHeartImage();
            for (int i = 0; i < model.getPlayerHealth(); ++i) {
                g.drawImage(img, (int)(i * k * GameModel.DISCRETE_FACTOR), (int)(height * k), null);
            }
        }
        
        if (isGamePaused()) {
            g.drawString("PAUSED", (int)((width - 8) * k / 2), (int)(height * k / 2));
        }
        
        g.drawString("SCORE: " + Integer.toString(model.getScore()), (int)(6 * k * GameModel.DISCRETE_FACTOR), (int)((height + GameModel.DISCRETE_FACTOR / 2 + 1) * k));
    }
}
