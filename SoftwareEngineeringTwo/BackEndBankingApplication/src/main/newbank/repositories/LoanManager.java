package main.newbank.repositories;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import main.newbank.dtos.Customer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoanManager {

    private List<Loan> loans;

    public LoanManager() {
        this.loans = new ArrayList<>();
    }

    public void addLoan(Loan loan) {
        this.loans.add(loan); 
    }

    public List<Loan> getLoans() {
        return loans;
    }

    // Method to load loans from a JSON file
    public void loadLoansFromJson(String jsonFilePath) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(jsonFilePath);
        if (file.exists() && file.length() > 0) { // Check if file exists and is not empty
            try {
                loans = mapper.readValue(file,
                        mapper.getTypeFactory().constructCollectionType(List.class, Loan.class));
            } catch (IOException e) {
                e.printStackTrace();
                loans = new ArrayList<>(); // Initialize to empty list if reading fails
            }
        } else {
            loans = new ArrayList<>(); // Initialize to empty list if file does not exist or is empty
        }
    }

    // Method to save loans to a JSON file
    public void saveLoansToJson(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty-print the JSON

        try {
            objectMapper.writeValue(new File(filePath), loans);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptLoanOffer(Loan loan) {
        if (loans.contains(loan) && loan.getStatus().equals("pending")) {
            loan.accept();
            saveLoansToJson("loans.json");
        }
    }

    public void rejectLoanOffer(Loan loan) {
        if (loans.contains(loan) && loan.getStatus().equals("pending")) {
            loan.reject();
            saveLoansToJson("loans.json");
        }
    }

    public boolean userHasActiveLoan(String username) {
        for (Loan loan : loans) {
            if (loan.getBorrower().equals(username) && loan.getStatus().equals("pending")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPositiveCreditScore(Customer customer) {
        return customer.getCreditScore() > 0;
    }
}