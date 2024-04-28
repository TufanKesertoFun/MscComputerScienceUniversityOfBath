import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        final String RED = "\u001B[31m";
        final String RESET = "\u001B[0m";

        System.out.println("Welcome to Connect 4!");

        Connect4GamePlay game = null;

        while (game == null) {
            // Display game mode options
            System.out.println("Choose the game mode:");
            System.out.println("1. One versus One (Human vs Computer) Press 1 and Enter");
            System.out.println("2. One versus Two (Human vs Two Computers) ConnectN Game Press 2 and Enter");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();

                if (choice == 1) {
                    // One versus One (Human vs Computer)
                    game = new GameForOneHumanVsOneComputer(new Connect4Board(), new HumanPlayer(), new ComputerPlayer());
                } else if (choice == 2) {
                    // One versus Two (Human vs Two Computers) ConnectN Game
                    int connectionCountForWin;
                    while (true) {
                        System.out.println("Please Choose Winning Value Condition for ConnectN Game between 2<N<7. If you choose 3, you need to connect three X to win as a human player");
                        connectionCountForWin = scanner.nextInt();

                        if (2 < connectionCountForWin && connectionCountForWin < 7) {
                            break;
                        } else {
                            System.out.println(RED + "Invalid input. Please enter a number between 3 and 6." + RESET);
                            scanner.nextLine(); // Consume the newline character
                        }
                    }

                    game = new GameForOneHumanVsTwoComputer(new Connect4Board(), new HumanPlayer(), new ComputerPlayer(), new ComputerPlayer(), connectionCountForWin);
                } else {
                    System.out.println(RED + "Invalid choice." + RESET);
                }
            } catch (InputMismatchException e) {
                System.out.println(RED + "Invalid input. Please enter a number." + RESET);
                scanner.nextLine(); // Clear the input buffer
            } catch (Exception e) {
                System.out.println(RED + "An error occurred: " + e.getMessage() + RESET);
            }
        }

        game.playGame();

        scanner.close();
    }
}
