import view.ViewFrame;

import javax.swing.SwingUtilities;

class MainClass {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ViewFrame frame = new ViewFrame();
                frame.showGUI();
            }
        });
                
    }
}
