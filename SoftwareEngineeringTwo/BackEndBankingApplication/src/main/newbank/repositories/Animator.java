package main.newbank.repositories;

import main.newbank.IRepositories.IAnimator;
import main.newbank.enums.Color;

import java.io.PrintWriter;
import java.io.IOException;
 
public class Animator implements IAnimator {

    @Override
    public void displayGreeting(PrintWriter out) throws IOException {
        String[] lines = {
                Color.RED + "///////////////////////////////////////////////////" + Color.RESET,
                Color.GREEN + "/                                               /" + Color.RESET,
                Color.YELLOW + "/   Experience the Future of Banking with Us!   /" + Color.RESET,
                Color.BLUE + "/                  New Bank                     /" + Color.RESET,
                Color.GREEN + "/                                               /" + Color.RESET,
                Color.RED + "///////////////////////////////////////////////////" + Color.RESET,
                Color.RED + "                ________________                " + Color.RESET,
                Color.RED + "               |                |               " + Color.RESET,
                Color.RED + "               |  _ _ _ _ _ _ _ |               " + Color.RESET,
                Color.RED + "               | | | | | | | |  |               " + Color.RESET,
                Color.RED + "               | | | | | | | |  |               " + Color.RESET,
                Color.RED + "               | |_|_|_|_|_|_|_ |               " + Color.RESET,
                Color.RED + "               |  _ _ _ _ _ _ _ |               " + Color.RESET,
                Color.RED + "               | | | | | | | |  |               " + Color.RESET,
                Color.RED + "               | | | | | | | |  |               " + Color.RESET,
                Color.RED + "               | |_|_|_|_|_|_|_ |               " + Color.RESET,
                Color.RED + "               |________________|               " + Color.RESET
        };

        for (String line : lines) {
            out.println(line);
            try {
                Thread.sleep(50); // Pause for 500 milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        out.println("Please type LOGIN if you already have an account, or REGISTER if you need to create one.");
    }

    @Override
    public void displayGoodbye(PrintWriter out) {
        String[] lines = {
                Color.RED + "///////////////////////////////////////////////////" + Color.RESET,
                Color.GREEN + "/                                               /" + Color.RESET,
                Color.YELLOW + "/        Thank You for Banking with Us!         /" + Color.RESET,
                Color.BLUE + "/             Have a Great Day!                 /" + Color.RESET,
                Color.GREEN + "/                                               /" + Color.RESET,
                Color.RED + "///////////////////////////////////////////////////" + Color.RESET
        };

        for (String line : lines) {
            out.println(line);
            try {
                Thread.sleep(50); // Pause for 500 milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
