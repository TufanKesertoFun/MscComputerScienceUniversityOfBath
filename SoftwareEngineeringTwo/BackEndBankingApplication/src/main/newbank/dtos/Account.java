package main.newbank.dtos;
import java.util.ArrayList;
import java.util.List;

public class Account {
	
	private String accountName; 
	private double balance;
	private List<String> transactions;


	public Account(String accountName) {
		this.accountName = accountName;
	}

	public Account(String accountName, double balance) {
		this.accountName = accountName;
		this.balance = 0.0;
		this.transactions = new ArrayList<>();
	}
	
	public String toString() {
		return (accountName + ": " + balance);

	}


	public String getAccountName() {
		return accountName;
	}

	public void withdrawFunds(double amountToWithdraw){
		balance -= amountToWithdraw;
	}

	public void depositFunds(double amountToDeposit){
		balance += amountToDeposit;
	}

	public boolean areFundsAvailable(double amountToCheck) {
		return balance - amountToCheck >= 0;
	}

	public List<String> getTransactionHistory() {
		return transactions;
	}

	public void addTransaction(String transaction) {
		transactions.add(transaction);
	}

	public double getBalance() {
		return balance;
	}




}
