public interface GameBoard {
    /**
     * Initializes the game board with its initial state.
     */
    void initializeBoard();

    /**
     * Prints the current state of the game board.
     */
    void printBoard();

    /**
     * Makes a move on the game board by placing a token in the specified column.
     *
     * @param column The column where the move is to be made.
     * @param token  The token to be placed (e.g., 'X' or 'O').
     * @return True if the move is successful, false otherwise.
     */
    boolean makeMove(int column, char token);

    /**
     * Checks if a specific column in the game board is full.
     *
     * @param column The column to check.
     * @return True if the column is full, false otherwise.
     */
    boolean isColumnFull(int column);

    /**
     * Checks if the entire game board is full.
     *
     * @return True if the board is full, false otherwise.
     */
    boolean isBoardFull();

    /**
     * Checks if a player with a specific token has won the game.
     *
     * @param token               The token to check for a win.
     * @param connectionCountForWin The number of connected tokens required for a win.
     * @return True if the player has won, false otherwise.
     */
    boolean checkWin(char token, int connectionCountForWin);
}
