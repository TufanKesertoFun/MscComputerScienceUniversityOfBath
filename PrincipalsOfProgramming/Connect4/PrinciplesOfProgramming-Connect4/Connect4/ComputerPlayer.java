import java.util.Random;

/**
 * The ComputerPlayer class represents a computer player in the Connect 4 game.
 * It implements the Player interface, providing a method to make a random move.
 */
public class ComputerPlayer implements Player {

  /**
   * Generates a random move for the computer player.
   *
   * @return An integer representing the column where the computer makes a move.
   */
  @Override
  public int makeMove() {
    Random random = new Random();
    return random.nextInt(Connect4Board.COLS);
  }
}
