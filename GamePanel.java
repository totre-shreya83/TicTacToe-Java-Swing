// GamePanel.java - Completely resizable version with responsive layout
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener, ComponentListener {
    
    private JButton[][] buttons = new JButton[3][3];
    
    // Color scheme
    private final Color X_COLOR = new Color(41, 128, 185);
    private final Color O_COLOR = new Color(231, 76, 60);
    private final Color WIN_COLOR = new Color(46, 204, 113);
    private final Color HOVER_COLOR = new Color(236, 240, 241);
    private final Color DRAW_COLOR = new Color(241, 196, 15);
    private final Color BACKGROUND_COLOR = new Color(52, 73, 94);
    
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    
    private JButton restartButton;
    private JButton resetScoreButton;
    private JButton aiToggleButton;
    private JButton difficultyButton;
    
    private ScoreBoard board = new ScoreBoard();
    private Timer timer;
    private int seconds = 0;
    private boolean timerRunning = false;
    
    private boolean xTurn = true;
    private int moves = 0;
    private boolean gameOver = false;
    private boolean isAIEnabled = false;
    private boolean isAITurn = false;
    private AIDifficulty difficulty = AIDifficulty.MEDIUM;
    
    // Layout components
    private JPanel gridPanel;
    private JPanel titlePanel;
    private JPanel bottomPanel;
    
    private enum AIDifficulty {
        EASY, MEDIUM, HARD
    }
    
    public GamePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initializeComponents();
        setupTimer();
        
        // Add component listener for resize events
        addComponentListener(this);
    }
    
    private void initializeComponents() {
        // Title Panel
        titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("TIC TAC TOE");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);
        
        // Grid Panel
        gridPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255,255,255,50), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Create buttons with dynamic sizing
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col] = new JButton("");
                buttons[row][col].setFocusPainted(false);
                buttons[row][col].setBackground(Color.WHITE);
                buttons[row][col].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199), 3),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                buttons[row][col].addActionListener(this);
                
                // Set initial font size
                updateButtonFont(buttons[row][col]);
                
                buttons[row][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        JButton b = (JButton) e.getSource();
                        if (b.getText().equals("") && !gameOver) {
                            b.setBackground(HOVER_COLOR);
                            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        }
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        JButton b = (JButton) e.getSource();
                        if (b.getText().equals("")) {
                            b.setBackground(Color.BLUE);
                        }
                    }
                });
                
                gridPanel.add(buttons[row][col]);
            }
        }
        
        add(gridPanel, BorderLayout.CENTER);
        
        // Bottom Panel
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Status Label
        statusLabel = new JLabel("Player X's Turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(statusLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Timer Label
        timerLabel = new JLabel(" 00:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        timerLabel.setForeground(new Color(241, 196, 15));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(timerLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Score Panel
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        scorePanel.setOpaque(false);
        
        scoreLabel = new JLabel("X: 0  |  O: 0  |  Draws: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreLabel.setForeground(Color.WHITE);
        scorePanel.add(scoreLabel);
        bottomPanel.add(scorePanel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
      // Button Panel
JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
buttonPanel.setOpaque(false);
buttonPanel.setMaximumSize(new Dimension(600, 40));

restartButton = createStyledButton("New Game", new Color(52, 152, 219));
resetScoreButton = createStyledButton(" Reset Score", new Color(231, 76, 60));
aiToggleButton = createStyledButton(" AI: OFF", new Color(155, 89, 182));
difficultyButton = createStyledButton(" Medium", new Color(46, 204, 113));

// Add action listeners with color changes
restartButton.addActionListener(e -> {
    // Flash effect on click
    restartButton.setBackground(new Color(41, 128, 185));
    newGame();
    // Reset color after 300ms
    Timer timer = new Timer(300, ev -> restartButton.setBackground(new Color(52, 152, 219)));
    timer.setRepeats(false);
    timer.start();
});

resetScoreButton.addActionListener(e -> {
    // Flash effect on click
    resetScoreButton.setBackground(new Color(192, 57, 43));
    resetScore();
    Timer timer = new Timer(300, ev -> resetScoreButton.setBackground(new Color(231, 76, 60)));
    timer.setRepeats(false);
    timer.start();
});

aiToggleButton.addActionListener(e -> {
    toggleAI();
    // Change color based on AI state
    if (isAIEnabled) {
        aiToggleButton.setBackground(new Color(46, 204, 113)); // Green when ON
        aiToggleButton.setText(" AI: ON");
    } else {
        aiToggleButton.setBackground(new Color(155, 89, 182)); // Purple when OFF
        aiToggleButton.setText(" AI: OFF");
    }
});

difficultyButton.addActionListener(e -> {
    changeDifficulty();
    // Change color based on difficulty level
    switch (difficulty) {
        case EASY:
            difficultyButton.setBackground(new Color(52, 152, 219)); // Blue for Easy
            difficultyButton.setText(" Easy");
            break;
        case MEDIUM:
            difficultyButton.setBackground(new Color(46, 204, 113)); // Green for Medium
            difficultyButton.setText(" Medium");
            break;
        case HARD:
            difficultyButton.setBackground(new Color(231, 76, 60)); // Red for Hard
            difficultyButton.setText(" Hard");
            break;
    }
});

buttonPanel.add(restartButton);
buttonPanel.add(resetScoreButton);
buttonPanel.add(aiToggleButton);
buttonPanel.add(difficultyButton);
bottomPanel.add(buttonPanel);

add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void updateButtonFont(JButton button) {
        // Calculate font size based on panel size
        int size = Math.min(gridPanel.getWidth(), gridPanel.getHeight()) / 6;
        size = Math.max(20, Math.min(80, size)); // Clamp between 20 and 80
        button.setFont(new Font("Segoe UI", Font.BOLD, size));
    }
    
    private void updateAllButtonFonts() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                updateButtonFont(buttons[i][j]);
            }
        }
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void setupTimer() {
        timer = new Timer(1000, e -> {
            seconds++;
            updateTimerDisplay();
        });
    }
    
    private void updateTimerDisplay() {
        int mins = seconds / 60;
        int secs = seconds % 60;
        timerLabel.setText(String.format(" %02d:%02d", mins, secs));
    }
    
    private void startTimer() {
        if (!timerRunning) {
            timerRunning = true;
            timer.start();
        }
    }
    
    private void stopTimer() {
        if (timerRunning) {
            timerRunning = false;
            timer.stop();
        }
    }
    
    private void resetTimer() {
        stopTimer();
        seconds = 0;
        updateTimerDisplay();
    }
    
    private void toggleAI() {
        isAIEnabled = !isAIEnabled;
        aiToggleButton.setText(isAIEnabled ? " AI: ON" : " AI: OFF");
        aiToggleButton.setBackground(isAIEnabled ? new Color(46, 204, 113) : new Color(155, 89, 182));
        
        if (isAIEnabled && !gameOver && !xTurn) {
            makeAIMove();
        }
        
        newGame();
    }
    
    private void changeDifficulty() {
        switch (difficulty) {
            case EASY:
                difficulty = AIDifficulty.MEDIUM;
                difficultyButton.setText(" Medium");
                difficultyButton.setBackground(new Color(46, 204, 113));
                break;
            case MEDIUM:
                difficulty = AIDifficulty.HARD;
                difficultyButton.setText(" Hard");
                difficultyButton.setBackground(new Color(231, 76, 60));
                break;
            case HARD:
                difficulty = AIDifficulty.EASY;
                difficultyButton.setText(" Easy");
                difficultyButton.setBackground(new Color(52, 152, 219));
                break;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver || isAITurn) return;
        
        JButton button = (JButton) e.getSource();
        if (!button.getText().equals("")) return;
        
        startTimer();
        makeMove(button);
    }
    
    private void makeMove(JButton button) {
        if (xTurn) {
            button.setText("X");
            button.setForeground(X_COLOR);
        } else {
            button.setText("O");
            button.setForeground(O_COLOR);
        }
        
        button.setBackground(Color.WHITE);
        moves++;
        
        // Update font to ensure proper sizing
        updateButtonFont(button);
        
        if (checkWinner()) {
            handleWin();
            return;
        }
        
        if (moves == 9) {
            handleDraw();
            return;
        }
        
        xTurn = !xTurn;
        updateStatus();
        
        if (isAIEnabled && !xTurn && !gameOver) {
            isAITurn = true;
            Timer delayTimer = new Timer(300, e -> {
                makeAIMove();
                isAITurn = false;
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        }
    }
    
    private void makeAIMove() {
        if (gameOver || xTurn || moves == 9) return;
        
        int[] bestMove = findBestMove();
        if (bestMove != null) {
            JButton aiButton = buttons[bestMove[0]][bestMove[1]];
            makeMove(aiButton);
        }
    }
    
    private int[] findBestMove() {
        switch (difficulty) {
            case EASY:
                return getRandomMove();
            case MEDIUM:
                return getMediumMove();
            case HARD:
                return getBestMove();
            default:
                return getRandomMove();
        }
    }
    
    private int[] getRandomMove() {
        Random rand = new Random();
        int attempts = 0;
        while (attempts < 100) {
            int row = rand.nextInt(3);
            int col = rand.nextInt(3);
            if (buttons[row][col].getText().equals("")) {
                return new int[]{row, col};
            }
            attempts++;
        }
        return null;
    }
    
    private int[] getMediumMove() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) {
                    buttons[i][j].setText("O");
                    if (checkWinner()) {
                        buttons[i][j].setText("");
                        return new int[]{i, j};
                    }
                    buttons[i][j].setText("");
                    
                    buttons[i][j].setText("X");
                    if (checkWinner()) {
                        buttons[i][j].setText("");
                        return new int[]{i, j};
                    }
                    buttons[i][j].setText("");
                }
            }
        }
        return getRandomMove();
    }
    
    private int[] getBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) {
                    buttons[i][j].setText("O");
                    int score = minimax(false, 0);
                    buttons[i][j].setText("");
                    
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{i, j};
                    }
                }
            }
        }
        return bestMove;
    }
    
    private int minimax(boolean isMaximizing, int depth) {
        if (checkWinner()) {
            return isMaximizing ? -10 + depth : 10 - depth;
        }
        
        if (moves == 9) {
            return 0;
        }
        
        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (buttons[i][j].getText().equals("")) {
                        buttons[i][j].setText("O");
                        moves++;
                        int score = minimax(false, depth + 1);
                        buttons[i][j].setText("");
                        moves--;
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (buttons[i][j].getText().equals("")) {
                        buttons[i][j].setText("X");
                        moves++;
                        int score = minimax(true, depth + 1);
                        buttons[i][j].setText("");
                        moves--;
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }
    
    private boolean checkWinner() {
        // Rows
        for (int i = 0; i < 3; i++) {
            if (!buttons[i][0].getText().equals("") &&
                buttons[i][0].getText().equals(buttons[i][1].getText()) &&
                buttons[i][1].getText().equals(buttons[i][2].getText())) {
                
                highlightWin(i, 0, i, 1, i, 2);
                return true;
            }
        }
        
        // Columns
        for (int i = 0; i < 3; i++) {
            if (!buttons[0][i].getText().equals("") &&
                buttons[0][i].getText().equals(buttons[1][i].getText()) &&
                buttons[1][i].getText().equals(buttons[2][i].getText())) {
                
                highlightWin(0, i, 1, i, 2, i);
                return true;
            }
        }
        
        // Main diagonal
        if (!buttons[0][0].getText().equals("") &&
            buttons[0][0].getText().equals(buttons[1][1].getText()) &&
            buttons[1][1].getText().equals(buttons[2][2].getText())) {
            
            highlightWin(0, 0, 1, 1, 2, 2);
            return true;
        }
        
        // Other diagonal
        if (!buttons[0][2].getText().equals("") &&
            buttons[0][2].getText().equals(buttons[1][1].getText()) &&
            buttons[1][1].getText().equals(buttons[2][0].getText())) {
            
            highlightWin(0, 2, 1, 1, 2, 0);
            return true;
        }
        
        return false;
    }
    
    private void highlightWin(int r1, int c1, int r2, int c2, int r3, int c3) {
        buttons[r1][c1].setBackground(WIN_COLOR);
        buttons[r2][c2].setBackground(WIN_COLOR);
        buttons[r3][c3].setBackground(WIN_COLOR);
    }
    
    private void handleWin() {
        gameOver = true;
        disableBoard();
        stopTimer();
        
        if (xTurn) {
            board.xWin();
            showWinMessage("X");
        } else {
            board.oWin();
            showWinMessage("O");
        }
        
        updateScore();
    }
    
    private void handleDraw() {
        gameOver = true;
        stopTimer();
        board.draw();
        updateScore();
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setBackground(DRAW_COLOR);
            }
        }
        
        statusLabel.setText("Match Draw!");
        statusLabel.setForeground(DRAW_COLOR);
    }
    
    private void showWinMessage(String player) {
        statusLabel.setText(player + " Wins! 🎉");
        statusLabel.setForeground(WIN_COLOR);
        
        int option = JOptionPane.showOptionDialog(this,
            player + " wins the game! 🎉\nTime: " + timerLabel.getText(),
            "Game Over",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new Object[]{"Play Again", "Close"},
            "Play Again");
            
        if (option == 1) {
            System.exit(0);
        } else {
            newGame();
        }
    }
    
    private void disableBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }
    
    private void updateStatus() {
        if (!gameOver) {
            statusLabel.setText(xTurn ? "Player X's Turn" : 
                (isAIEnabled ? "AI (O) is thinking..." : "Player O's Turn"));
            statusLabel.setForeground(Color.WHITE);
        }
    }
    
    private void newGame() {
        stopTimer();
        resetTimer();
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setBackground(Color.WHITE);
                buttons[i][j].setEnabled(true);
                updateButtonFont(buttons[i][j]);
            }
        }
        
        moves = 0;
        xTurn = true;
        gameOver = false;
        isAITurn = false;
        
        statusLabel.setText("Player X's Turn");
        statusLabel.setForeground(Color.WHITE);
        startTimer();
    }
    
    private void resetScore() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reset the score?",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            board.reset();
            updateScore();
        }
    }
    
    private void updateScore() {
        scoreLabel.setText(String.format("X: %d  |  O: %d  |  Draws: %d",
            board.getXWins(), board.getOWins(), board.getDraws()));
    }
    
    // ComponentListener methods for resize handling
    @Override
    public void componentResized(ComponentEvent e) {
        // Update all button fonts when window is resized
        updateAllButtonFonts();
    }
    
    @Override
    public void componentMoved(ComponentEvent e) {}
    
    @Override
    public void componentShown(ComponentEvent e) {
        // Initial font sizing after component is shown
        SwingUtilities.invokeLater(() -> updateAllButtonFonts());
    }
    
    @Override
    public void componentHidden(ComponentEvent e) {}
}