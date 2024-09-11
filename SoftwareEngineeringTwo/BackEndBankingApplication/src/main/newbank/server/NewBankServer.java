package main.newbank.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.opencsv.exceptions.CsvValidationException;
import main.newbank.IRepositories.IAnimator;
import main.newbank.IRepositories.ICustomerHandler;
import main.newbank.repositories.Animator;
import main.newbank.repositories.CustomerHandler;
import main.newbank.repositories.NewBank;
import main.newbank.repositories.NewBankClientHandler; 
import main.newbank.repositories.PromptUser;
import main.newbank.repositories.DisplayCustomer;

public class NewBankServer extends Thread {

	private ServerSocket server;
	private IAnimator animator;

	public NewBankServer(int port, IAnimator animator) throws IOException {
		this.server = new ServerSocket(port);
		this.animator = animator;
	}

	@Override
	public void run() {
		// starts up a new client handler thread to receive incoming connections and process requests
		System.out.println("New Bank Server listening on " + server.getLocalPort());
		try {
			while (true) {
				Socket s = server.accept();
				NewBank bank = new NewBank();
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				PromptUser promptUser = new PromptUser();
				DisplayCustomer displayCustomer = new DisplayCustomer();
				ICustomerHandler customerHandler = new CustomerHandler(bank, out, promptUser, displayCustomer);
				NewBankClientHandler clientHandler = new NewBankClientHandler(bank, s, animator, customerHandler);
				clientHandler.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CsvValidationException e) {
			System.err.println("Error initializing New Bank Client Handler: " + e.getMessage());
        } finally {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		IAnimator animator = new Animator();  // Create an instance of the Animator class
		// starts a new NewBankServer thread on a specified port number
		new NewBankServer(14002, animator).start();
	}
}
