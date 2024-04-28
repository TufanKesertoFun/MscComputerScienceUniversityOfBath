/**
 * The Connect4Board class represents the game board for Connect 4.
 * It implements the GameBoard interface.
 */
public class Connect4Board implements GameBoard {

   // Constants for the number of rows and columns on the game board
    public static final int ROWS = 6;
    public static final int COLS = 7;

   // Number of connected tokens required for a win
    private final int connectionCountForWin = 4;

    // The game board represented as a 2D array
    private char[][] board;
  
    // Player tokens for different players
    private char[][] playerTokens = {{'X', 'O', 'Z'}};

    // ANSI color codes for console output
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";


    /**
     * Default constructor for the Connect4Board class.
     * Initializes the game board.
     */
    public Connect4Board() {
        initializeBoard();
    }

    /**
     * Initializes the game board with empty spaces.
     */

    @Override
    public void initializeBoard() {
        board = new char[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = ' ';
            }
        }
    }

    /**
     * Prints the current state of the game board to the console.
     */
    @Override
    public void printBoard() {
        for (int i = 0; i < ROWS; i++) {
            System.out.print(ROWS - i);
            for (int j = 0; j < COLS; j++) {
                char token = board[i][j];
                System.out.print("| ");
                if (token == playerTokens[0][0]) {
                    System.out.print(RED + token + RESET + " ");
                } else if (token == playerTokens[0][1]) {
                    System.out.print(YELLOW + token + RESET + " ");
                } else if (token == playerTokens[0][2]) {
                    System.out.print(BLUE + token + RESET + " ");
                } else {
                    System.out.print(token + " ");
                }
            }
            System.out.println("|");
        }
        System.out.println("   1   2   3   4   5   6   7");
        System.out.println("------------------------------");
    }

    /**
     * Attempts to make a move by placing a token in the specified column.
     *
     * @param column      The column where the move is to be made.
     * @param playerToken The token to be placed ('X', 'O', 'Z', etc.).
     * @return True if the move is successful, false otherwise.
     */
    @Override
    public boolean makeMove(int column, char playerToken) {
        for (int i = ROWS - 1; i >= 0; i--) {
            if (board[i][column] == ' ') {
                board[i][column] = playerToken;
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a specific column on the game board is full.
     *
     * @param col The column to check.
     * @return True if the column is full, false otherwise.
     */
    @Override
    public boolean isColumnFull(int col) {
        return board[0][col] != ' ';
    }

    /**
     * Checks if the entire game board is full.
     *
     * @return True if the board is full, false otherwise.
     */
    @Override
    public boolean isBoardFull() {
        for (int i = 0; i < COLS; i++) {
            if (board[0][i] == ' ') {
                return false;
            }
        }
        return true;
    }

  /**
   * Checks if a player with a specific token has won the game.
   *
   * @param playerToken           The token to check for a win.
   * @param connectionCountForWin The number of connected tokens required for a win.
   * @return True if the player has won, false otherwise.
   */
    @Override
    public boolean checkWin(char playerToken, int connectionCountForWin) {
        return checkHorizontalWin(playerToken, connectionCountForWin) || checkVerticalWin(playerToken,connectionCountForWin) || checkDiagonalWin(playerToken,connectionCountForWin);
    }


  // Private helper methods for checking win conditions in different directions
    private boolean checkHorizontalWin(char playerToken,int connectionCountForWin) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j <= COLS - connectionCountForWin; j++) {
                if (checkLine(playerToken, i, j, 0, 1,connectionCountForWin)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkVerticalWin(char playerToken,int connectionCountForWin) {
        for (int i = 0; i <= ROWS - connectionCountForWin; i++) {
            for (int j = 0; j < COLS; j++) {
                if (checkLine(playerToken, i, j, 1, 0,connectionCountForWin)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkDiagonalWin(char playerToken,int connectionCountForWin) {
        return checkTopLeftToBottomRight(playerToken,connectionCountForWin) || checkBottomLeftToTopRight(playerToken,connectionCountForWin);
    }

    private boolean checkTopLeftToBottomRight(char playerToken,int connectionCountForWin) {
        for (int i = 0; i <= ROWS - connectionCountForWin; i++) {
            for (int j = 0; j <= COLS - connectionCountForWin; j++) {
                if (checkLine(playerToken, i, j, 1, 1,connectionCountForWin)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkBottomLeftToTopRight(char playerToken,int connectionCountForWin) {
        for (int i = 3; i < ROWS; i++) {
            for (int j = 0; j <= COLS - connectionCountForWin; j++) {
                if (checkLine(playerToken, i, j, -1, 1,connectionCountForWin)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkLine(char playerToken, int row, int col, int rowIncrement, int colIncrement,int connectionCountForWin) {
        for (int i = 0; i < connectionCountForWin; i++) {
            if (board[row][col] != playerToken) {
                return false;
            }
            row += rowIncrement;
            col += colIncrement;
        }
        return true;
    }
}
