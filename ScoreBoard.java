// ScoreBoard.java - Unchanged from previous version
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ScoreBoard {
    private int xWins;
    private int oWins;
    private int draws;
    private static final String SAVE_FILE = "tictactoe_scores.txt";
    
    public ScoreBoard() {
        loadFromFile();
    }
    
    public void xWin() {
        xWins++;
        saveToFile();
    }
    
    public void oWin() {
        oWins++;
        saveToFile();
    }
    
    public void draw() {
        draws++;
        saveToFile();
    }
    
    public int getXWins() { return xWins; }
    public int getOWins() { return oWins; }
    public int getDraws() { return draws; }
    
    public void reset() {
        xWins = 0;
        oWins = 0;
        draws = 0;
        saveToFile();
    }
    
    private void saveToFile() {
        try {
            String data = xWins + "," + oWins + "," + draws;
            Files.write(Paths.get(SAVE_FILE), data.getBytes());
        } catch (IOException e) {
            // Silent fail
        }
    }
    
    private void loadFromFile() {
        try {
            String data = new String(Files.readAllBytes(Paths.get(SAVE_FILE)));
            String[] parts = data.split(",");
            if (parts.length == 3) {
                xWins = Integer.parseInt(parts[0]);
                oWins = Integer.parseInt(parts[1]);
                draws = Integer.parseInt(parts[2]);
            }
        } catch (IOException | NumberFormatException e) {
            xWins = 0;
            oWins = 0;
            draws = 0;
        }
    }
}