// TicTacToe.java - Main class
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

public class Tictacktoe {
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        showSplashScreen();
        
        SwingUtilities.invokeLater(() -> {
            new GameFrame();
        });
    }
    
    private static void showSplashScreen() {
        JWindow splash = new JWindow();
        splash.setLayout(new BorderLayout());
        
        JLabel label = new JLabel("TIC TAC TOE", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 48));
        label.setForeground(new Color(41, 128, 185));
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 5));
        
        splash.add(label);
        splash.setSize(400, 200);
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);
        
        Timer timer = new Timer(1500, e -> splash.dispose());
        timer.setRepeats(false);
        timer.start();
    }
}