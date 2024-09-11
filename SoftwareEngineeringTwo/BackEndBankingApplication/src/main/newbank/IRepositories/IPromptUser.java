package main.newbank.IRepositories;

import java.io.IOException;
import java.io.PrintWriter;

public interface IPromptUser {
    String promptUser(PrintWriter out, String prompt) throws IOException;
}
 