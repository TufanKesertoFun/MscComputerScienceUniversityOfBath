/**
 * The class Game for one human vs one computer implements connect4 game play
 */ 
public class GameForOneHumanVsOneComputer implements Connect4GamePlay {
    private final GameBoard gameBoard;
    private final Player player;
    private final Player computer;

/** 
 *
 * Game for one human vs one computer
 *
 * @param gameBoard  the game board. 
 * @param player  the player. 
 * @param computer  the computer. 
 * @return public
 */
    public GameForOneHumanVsOneComputer(GameBoard gameBoard, Player player, Player computer) { 

        this.gameBoard = gameBoard;
        this.player = player;
        this.computer = computer;
    }

/** 
 *
 * Play game
 *
 */
    @Override
    public void playGame() { 

        boolean playerTurn = true;

        while (true) {
            gameBoard.printBoard();

            int move;
            if (playerTurn) {
                move = player.makeMove();
            } else {
                move = computer.makeMove();
            }

            if (gameBoard.makeMove(move, playerTurn ? 'X' : 'O')) {
                if (gameBoard.checkWin(playerTurn ? 'X' : 'O',4)) {
                    gameBoard.printBoard();
                    System.out.println((playerTurn ? "Player" : "Computer") + " wins!");
                    break;
                }

                if (gameBoard.isBoardFull()) {
                    gameBoard.printBoard();
                    System.out.println("It's a draw!");
                    break;
                }

                playerTurn = !playerTurn;
            } else {
                System.out.println("Column is full. Try again.");
            }
        }

        if (player instanceof HumanPlayer) {
            ((HumanPlayer) player).closeScanner();
        }
    }


}
