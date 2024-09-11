package main.newbank.repositories;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;


public class Loan {

    @JsonProperty("borrower")
    private String borrower;
    @JsonProperty("amount")
    private double amount;
    @JsonProperty("interest_rate")
    private double interestRate;
    @JsonProperty("status") 
    private String status;

    @JsonCreator
    public Loan(
            @JsonProperty("borrower") String borrower,
            @JsonProperty("amount") double amount,
            @JsonProperty("interest_rate") double interestRate,
            @JsonProperty ("status") String status)
    {
        this.borrower = borrower;
        this.amount = amount;
        this.interestRate = interestRate;
        this.status = status;
    }

    public String getBorrower() {
        return borrower;
    }

    public double getAmount() {
        return amount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public String getStatus() {
        return status;
    }

    public void accept() {
        this.status = "accepted";
    }

    public void reject() {
        this.status = "rejected";
    }
}