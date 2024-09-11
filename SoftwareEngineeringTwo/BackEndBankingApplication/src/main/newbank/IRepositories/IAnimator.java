package main.newbank.IRepositories;

import java.io.IOException;
import java.io.PrintWriter;

public interface IAnimator { 
    void displayGreeting(PrintWriter out) throws IOException; 
    void displayGoodbye(PrintWriter out);
}
  