import java.util.Scanner;

/**
 * The class Human player implements player, custom exception interface
 */
public class HumanPlayer implements Player, CustomExceptionInterface {
  private Scanner scanner;

  /**
   *
   * Human player
   *
   * @return public
   */
  public HumanPlayer() {

    scanner = new Scanner(System.in);
  }



  /**
   *
   * Make move
   *
   * @return int
   */
  @Override
  public int makeMove() {

    try {
      int column;
      System.out.print("Enter your move human Player (1-7): ");
      column = scanner.nextInt() - 1;

      if (column < 0 || column >= Connect4Board.COLS) {
        throw new RuntimeException("Invalid column number. Please enter a number between 1 and 7.");
      }

      return column;

    } catch (Throwable e) {
      handleException(e);
      return makeMove(); // Retry input
    }
  }

  /**
   *
   * Handle exception
   *
   * @param e the e.
   */
  private void handleException(Throwable e) {

    // Handle exception and log it
    System.out.println(getErrorMessage());
    showException(e.getMessage());
    scanner.nextLine(); // Consume the invalid input
  }

  /**
   *
   * Gets the error message
   *
   * @return the error message
   */
  @Override
  public String getErrorMessage() {

    return "Invalid input. Please enter a valid number.";
  }

  /**
   *
   * Show exception
   *
   * @param message the message.
   */
  @Override
  public void showException(String message) {

    // Implement logging logic here
    System.out.println("Exception logged: " + message);
  }

  /**
   *
   * Close scanner
   *
   */
  public void closeScanner() {

    scanner.close();
  }
}
