// GameFrame.java - Updated with resizable support
import java.awt.Dimension;

import javax.swing.JFrame;

public class GameFrame extends JFrame {
    
    public GameFrame() {
        setTitle("Tic Tac Toe Ultimate");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true); // Allow resizing
        
        GamePanel gamePanel = new GamePanel();
        add(gamePanel);
        
        // Set initial size
        setSize(600, 750);
        setMinimumSize(new Dimension(400, 500)); // Prevent too small
        setLocationRelativeTo(null);
        setVisible(true);
    }
}