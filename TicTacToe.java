import java.util.Scanner;

public class TicTacToe {
    static char[][] board = {
        {'1', '2', '3'},
        {'4', '5', '6'},
        {'7', '8', '9'}
    };

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        char currentPlayer = 'X';
        int moves = 0;

        while (true) {
            printBoard();

            System.out.println("Player " + currentPlayer + ", enter position (1-9): ");
            int position = sc.nextInt();

            if (!placeMove(position, currentPlayer)) {
                System.out.println("Invalid move! Try again.");
                continue;
            }

            moves++;

            if (checkWinner(currentPlayer)) {
                printBoard();
                System.out.println(" Player " + currentPlayer + " wins!");
                break;
            }

            if (moves == 9) {
                printBoard();
                System.out.println("It's a Draw!");
                break;
            }

            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        }
    }

    static void printBoard() {
        System.out.println();
        for (int i = 0; i < 3; i++) {
            System.out.println(" " + board[i][0] + " | " + board[i][1] + " | " + board[i][2]);
            if (i < 2)
                System.out.println("---+---+---");
        }
        System.out.println();
    }

    static boolean placeMove(int position, char player) {
        int row = (position - 1) / 3;
        int col = (position - 1) % 3;

        if (position < 1 || position > 9)
            return false;

        if (board[row][col] == 'X' || board[row][col] == 'O')
            return false;

        board[row][col] = player;
        return true;
    }

    static boolean checkWinner(char player) {

        // Rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player &&
                board[i][1] == player &&
                board[i][2] == player)
                return true;
        }

        // Columns
        for (int i = 0; i < 3; i++) {
            if (board[0][i] == player &&
                board[1][i] == player &&
                board[2][i] == player)
                return true;
        }

        // Diagonals
        if (board[0][0] == player &&
            board[1][1] == player &&
            board[2][2] == player)
            return true;

        if (board[0][2] == player &&
            board[1][1] == player &&
            board[2][0] == player)
            return true;

        return false;
    }
}