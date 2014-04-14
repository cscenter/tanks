package model;

import view.GameView;
import javax.swing.SwingUtilities;

class MainClass {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GameView.createAndShowGUI(); 
            }
        });
                
    }
}
