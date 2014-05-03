package view;

import io.GameModelReader;
import io.ImageGallery;
import io.MapIOException;

import java.awt.Color;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import model.Direction;
import model.GameModel;
import model.GameObject;
import model.GameObjectDescription;
import model.InfiniteGameModel;
import model.ModelException;
import model.Tank;

@SuppressWarnings("serial")
public class ViewPanel extends JPanel {
    private GameModel model = null;
    private javax.swing.Timer timer;
    
    private boolean gamePaused = false;
    private boolean gameStarted = false;

    private Keyer keyListener;
    
    private final double k = (64.0 / GameModel.DISCRETE_FACTOR);
    private final int cellImageSize = 64;
    
    private static final int TIMER_DELAY = 1;
    private ImageGallery gallery;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private int ticksCounter;
    private static final int REPAINT_DELAY = 8;
    
    public ViewPanel(PropertyChangeListener gameStartedListener, PropertyChangeListener gamePausedListener) {
        super();

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
                try {
					model.tick();
					++ticksCounter;
				} catch (ModelException e) {
					JOptionPane.showMessageDialog(ViewPanel.this,
		                    e.getMessage(),
		                    "Error",
		                    JOptionPane.ERROR_MESSAGE);
				}
                if ((ticksCounter & REPAINT_DELAY) == 0) {
                	repaint();
                }
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
        
        public Keyer() {
            super();
            
            new Timer(TIMER_DELAY, new ActionListener() {
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
    
    //public Dimension getPreferredSize() {
    //    return new Dimension((int)(model.getWidth() * k), (int)((model.getHeight() + GameModel.DISCRETE_FACTOR)* k));
    //}
    
    private boolean isGameOn() {
        return !isGamePaused() && isGameStarted();
    }
    
    public void start(String mapFilename, int botsCount) {
        try {
            model = new InfiniteGameModel(botsCount);
            GameModelReader.parse(model, mapFilename);
            model.start();
            timer.start();
            ticksCounter = 0;
            keyListener.reset();
            setGamePaused(false);
            setGameStarted(true);
        } catch (MapIOException e) {
            JOptionPane.showMessageDialog(ViewPanel.this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (ModelException e) {
        	JOptionPane.showMessageDialog(ViewPanel.this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
		}
    }
    
    private int modelCenterX;
    private int modelCenterY;
    private int screenWidth;
    private int screenHeight;
    
    /*
    private boolean isValidCoordinates(int x, int y) {
        return (x > 0) && (y > 0) && (x + k * GameModel.DISCRETE_FACTOR < screenWidth) && (y + k * GameModel.DISCRETE_FACTOR < screenHeight);
    }
    */
    
    private boolean isValidCoordinates(double x, double y) {
        return (x + cellImageSize > 0) && (y  + cellImageSize > 0) && (x < screenWidth) && (y < screenHeight);
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (model == null) {
            return;
        }
        
        Dimension d = getParent().getSize();
        screenWidth = d.width;
        screenHeight = d.height;

        Tank playersTank = model.getPlayerTank();
        if (playersTank != null) {
            modelCenterX = (int)(playersTank.getPosition().getX() * k);
            modelCenterY = (int)(playersTank.getPosition().getY() * k);
        }
        
        int screenCenterX = screenWidth / 2;
        int screenCenterY = screenHeight / 2;

        int moveX = screenCenterX - (int)(modelCenterY);
        int moveY = screenCenterY - (int)(modelCenterX);
        if (isGameOn())
            g.setColor(Color.BLUE);
        g.fillRect(0, 0, screenWidth, screenHeight);
        
        Image img = null;

        int x;
        int y;
        Collection<GameObject> toDrawFirst = new ArrayList<>();
        Collection<GameObject> toDrawSecond = new ArrayList<>();
        Collection<GameObject> toDrawThird = new ArrayList<>();
        
        for (GameObject obj : model.getGameObjects()) {
            
            x = obj.getPosition().getX();
            y = obj.getPosition().getY();

            if (isValidCoordinates((y * k) + moveX, (x * k) + moveY)) {
                switch (obj.getDescription()) {
                case GROUND:
                	toDrawSecond.add(obj);
                	break;
                case WATER:
                	toDrawFirst.add(obj);
                	break;
            	default:
            		toDrawThird.add(obj);
                }
            }
        }
        
        for (GameObject obj : toDrawFirst) {
        	
        	img = gallery.getImage(obj);
            
            x = obj.getPosition().getX();
            y = obj.getPosition().getY();
            g.drawImage(img, (int)(y * k) + moveX, (int)(x * k) + moveY, null);
        }
        
        for (GameObject obj : toDrawSecond) {
        	
        	img = gallery.getImage(obj);
            
            x = obj.getPosition().getX();
            y = obj.getPosition().getY();
            
            g.drawImage(img, (int)(y * k) + moveX - 8, (int)(x * k) + moveY - 8, null);
        }
        
        for (GameObject obj : toDrawThird) {
        	
        	img = gallery.getImage(obj);
            
            x = obj.getPosition().getX();
            y = obj.getPosition().getY();
            
            g.drawImage(img, (int)(y * k) + moveX, (int)(x * k) + moveY, null);
        }
        
        if (model.isPlayerAlive()) {
            img = gallery.getHeartImage();
            for (int i = 0; i < model.getPlayerHealth(); ++i) {
                g.drawImage(img, i * cellImageSize, 0, null);
            }
        }
        
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
        g.setColor(Color.BLACK);
        
        if (isGamePaused()) {
            g.drawString("PAUSED", (int)((screenWidth - 8) * k / 2), (int)(screenHeight * k / 2));
        }
        if (isGameOn()) {
            g.drawString("SCORE: " + Integer.toString(model.getScore()), 0, (int)(1.5 * cellImageSize));
        }
    }
}
