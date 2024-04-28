/**
 * The class Game for one human vs two computer implements connect4 game play
 */ 
public class GameForOneHumanVsTwoComputer implements Connect4GamePlay {
    private final GameBoard gameBoard;
    private final Player humanPlayer;
    private final Player computerPlayer1;
    private final Player computerPlayer2;

    private final int connectionCountForWin;


/** 
 *
 * Game for one human vs two computer
 *
 * @param gameBoard  the game board. 
 * @param humanPlayer  the human player. 
 * @param computerPlayer1  the computer player1. 
 * @param computerPlayer2  the computer player2. 
 * @param connectionCountForWin  the connection count for win. 
 * @return public
 */
    public GameForOneHumanVsTwoComputer(GameBoard gameBoard, Player humanPlayer, Player computerPlayer1, Player computerPlayer2, int connectionCountForWin) { 

        this.gameBoard = gameBoard;
        this.humanPlayer = humanPlayer;
        this.computerPlayer1 = computerPlayer1;
        this.computerPlayer2 = computerPlayer2;
        this.connectionCountForWin = connectionCountForWin;
    }

/** 
 *
 * Play game
 *
 */
   @Override
    public void playGame() { 

        Player[] players = {humanPlayer, computerPlayer1, computerPlayer2};
        int currentPlayerIndex = 0;

        while (true) {
            gameBoard.printBoard();

            int move;
            char currentPlayerToken;

            move = players[currentPlayerIndex].makeMove();
            currentPlayerToken = (currentPlayerIndex == 0) ? 'X' : (currentPlayerIndex == 1) ? 'Z' : 'O';

            if (gameBoard.makeMove(move, currentPlayerToken)) {
                if (gameBoard.checkWin(currentPlayerToken,connectionCountForWin)) {
                    gameBoard.printBoard();
                    System.out.println((currentPlayerToken == 'X' ? "Human Player" : "Computer Player " + (currentPlayerIndex - 1)) + " wins!");
                    break;
                }

                if (gameBoard.isBoardFull()) {
                    gameBoard.printBoard();
                    System.out.println("It's a draw!");
                    break;
                }

                currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
            } else {
                System.out.println("Column is full. Try again.");
            }
        }

        if (humanPlayer instanceof HumanPlayer) {
            ((HumanPlayer) humanPlayer).closeScanner();
        }
    }
}