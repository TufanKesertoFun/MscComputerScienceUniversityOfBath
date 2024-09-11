package main.newbank.repositories;

import main.newbank.IRepositories.IPromptUser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class PromptUser implements IPromptUser {
    @Override
    public String promptUser(PrintWriter out, String prompt) throws IOException {
        out.println(prompt);
        out.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }
} 
