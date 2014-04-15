package view;

import model.*;
import io.GameModelReader;
import io.ImageGallery;
import io.MapIOException;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@SuppressWarnings("serial")
public class ViewPanel extends JPanel {
    private GameModel model;
    private javax.swing.Timer timer;
    
    private boolean isOver = false;
    public boolean isOver() {
		return isOver;
	}

	public void setOver(boolean isOver) {
		boolean oldValue = this.isOver; 
		this.isOver = isOver;
		pcs.firePropertyChange("isOver", oldValue, isOver);
	}

	private int k = 64 / GameModel.DISCRETE_FACTOR;
    
    private static final int TIMER_DELAY = 50;
    private ImageGallery gallery;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
	public ViewPanel(PropertyChangeListener gameStateListener) {
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
                    isOver = true;
                    // TODO send message to Frame about game ending
                }
            }
        });
        
        pcs.addPropertyChangeListener("isOver", gameStateListener);
    }
    
    public void unpause() {
        timer.start();
        setOver(false);
    }
    
    public void pause() {
    	timer.stop();
    	setOver(true);
    }
	
    public Dimension getPreferredSize() {
        return new Dimension(model.getWidth() * k, (model.getHeight() + GameModel.DISCRETE_FACTOR)* k);
    }
    
    public void start() {
        try {
            GameModelReader.parse(model, "map.txt");
            model.start();
            timer.start();
            isOver = false;
            
            addKeyListener(new KeyAdapter() {
     
                public void keyPressed(KeyEvent e) {               
                    switch (e.getKeyCode()) {
                    case KeyEvent.VK_W :
                        if (!isOver()) {
                        	model.movePlayer(Direction.UP);
                        }
                        break;
                    case KeyEvent.VK_A :
                    	if (!isOver()) {
                    		model.movePlayer(Direction.LEFT);
                    	}
                        break;
                    case KeyEvent.VK_S :
                    	if (!isOver()) {
                    		model.movePlayer(Direction.DOWN);
                    	}
                        break;
                    case KeyEvent.VK_D :
                    	if (!isOver()) {
                    		model.movePlayer(Direction.RIGHT);
                    	}
                        break;
                    case KeyEvent.VK_SPACE :
                    	if (!isOver()) {
                    		model.shootPlayer();
                    	}
                        break;
                    case KeyEvent.VK_P :
                        if (isOver()) {
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
