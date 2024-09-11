package main.newbank.repositories;

import com.opencsv.exceptions.CsvValidationException;
import main.newbank.IRepositories.INewBank;
import main.newbank.dtos.Customer;
import main.newbank.enums.CustomerType;
import main.newbank.dtos.Account;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
 
public class NewBank implements INewBank {

    private List<Customer> customers;
    private final AccountManager accountManager;
    private final String[] allowedAccountTypes = {"CURRENT", "SAVINGS"};
    private final LoanManager loanManager;


    public NewBank() throws CsvValidationException, IOException {
            this.accountManager = new AccountManager();
            displayAdminCredentials();
            customers = accountManager.getCustomers();
            this.loanManager = new LoanManager();
            loanManager.loadLoansFromJson("loans.json");
        }


    private void displayAdminCredentials() {
        System.out.println("Admin Username: admin");
        System.out.println("Admin Password: password");
    }

    public NewBank getBank() {
        return this;
    }

    @Override
    public synchronized Customer registerCustomer(String userName, String password, String firstName, String lastName, String emailAddress, String dateOfBirth, CustomerType customerType) {
        Customer newCustomer;
        String accountCreationDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        switch (customerType) {
            case CORPORATE:
                newCustomer = new CorporateCustomer(userName, password, false, firstName, lastName, emailAddress, dateOfBirth, accountCreationDate);
                break;
            case NON_PROFIT:
                newCustomer = new NonProfitCustomer(userName, password, false, firstName, lastName, emailAddress, dateOfBirth, accountCreationDate);
                break;
            case INDIVIDUAL:
            default:
                newCustomer = new IndividualCustomer(userName, password, false, firstName, lastName, emailAddress, dateOfBirth, accountCreationDate);
                break;
        }
        accountManager.addCustomer(newCustomer);
        customers = accountManager.getCustomers(); // Update the customers list
        return newCustomer;
    }


    @Override
    public synchronized boolean updateCustomer(String userName, String newPassword) {
        for (Customer customer : customers) {
            if (customer.getUsername().equals(userName)) {
                customer.setPassword(newPassword);
                // Save changes back to the account manager or data storage if necessary
                accountManager.saveCustomerToCSV(customer);
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean deleteCustomer(String userName) {
        for (Customer customer : customers) {
            if (customer.getUsername().equals(userName)) {
                customers.remove(customer);
                accountManager.saveCustomerToCSV(customer);
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized Customer readCustomer(String userName) {
        for (Customer customer : customers) {
            if (customer.getUsername().equals(userName)) {
                return customer;
            }
        }
        return null;
    }

    @Override
    public synchronized List<Customer> getAllCustomers() {
        return accountManager.getCustomers();
    }

    public synchronized Customer checkLogInDetails(String userName, String password) {
       customers = accountManager.getCustomers();
        for (Customer customer : customers) {
            if (customer.getUsername().equals(userName)) {
                if (Objects.equals(password, customer.getPassword())) {
                    return customer;
                }
            }
        }
        return null;
    }

    @Override
    public synchronized boolean deactivateCustomer(String userName) {
        Customer customer = customers.get(Integer.parseInt(userName));
        if (customer != null) {
            customer.setDeactivated(true);
            return true;
        }
        return false;
    }
    @Override
    public synchronized boolean deleteCustomerPermanently(String userName) {
        customers.remove(userName);
        return true;
    }

    public synchronized String processRequest(Customer customer, String request) {
        String[] requestParams = request.split("\\s+");
        String command = requestParams[0];
        String outcome;
        if (customer != null && customers.contains(customer)) {
            switch (command.toUpperCase()) {
                case "NEWACCOUNT":
                    outcome = createBankAccount(customer, requestParams[1]);
                    return outcome;
                case "MOVE":
                    outcome = moveFundsBetweenAccounts(customer, requestParams[1], requestParams[2], requestParams[3]);
                    return outcome;
                case "DEPOSIT":
                    return depositOntoAccount(customer, requestParams[1], requestParams[2]);
                case "LOGOUT":
                    if (requestParams.length != 1) {
                        return "Invalid command. Usage: LOGOUT";
                    }
                    return "END";

                default:
                    return "Invalid command. Please try again.";
            }
        }
        return "Customer not found or not logged in.";
    }

    public boolean isPasswordValid(String password) {
        String[] badPasswords = {"PASSWORD", "password", "123456789"};
        int upperCharLimit = 15;
        int lowerCharLimit = 9;

        for (String x : badPasswords) {
            if (x.equals(password)) {
                return false;
            }
        }

        Pattern pattern = Pattern.compile("(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(^\\S+$)");
        Matcher matcher = pattern.matcher(password);
        boolean match = matcher.find();
        int strLen = password.length();

        return match && strLen >= lowerCharLimit && strLen <= upperCharLimit;
    }

    public boolean isUserNameValid(String userName) {
        boolean duplicate = customers.stream().anyMatch(c -> c.getUsername().equals(userName));
        int upperCharLimit = 15;
        int lowerCharLimit = 6;
        int strLen = userName.length();

        return !duplicate && strLen >= lowerCharLimit && strLen <= upperCharLimit && !userName.contains(" ");
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }


    /**
     * The function creates a new bank account for the logged in user. Upon calling the command the user needs
     * to specify what type of account they want to open.
     *
     * @param customer Customer object of the logged in user.
     * @param accName The type of account the user wants to open.
     * @return String representation of the output message - whether the command was successful or not.
     */
    private String createBankAccount(Customer customer, String accName){
        // Validate if request account type is supported
        boolean validAccountType = Arrays.asList(allowedAccountTypes).contains(accName.toUpperCase());
        if (!validAccountType){
            return "Supported account types are Current and Savings. Please try again.";
        }

        int startAccountCount = customer.getAccountsCount();
        Account newAccount = new Account(accName.toUpperCase());
        customer.addBankAccount(newAccount);
        int endAccountCount = customer.getAccountsCount();
        return endAccountCount > startAccountCount ? "Congratulations! Account successfully created." : "Failed to create new account. Please try again!";
    }


    public String requestLoan(Customer currentUser, String request) {
        // Split the request string by spaces
        String[] params = request.split("\\s+");

        // Ensure the request contains the expected number of parameters
        if (params.length < 3) {
            return "Invalid request. Please provide the amount and interest rate.";
        }

        try {
            // Parse the amount and interest rate from the request parameters
            double amount = Double.parseDouble(params[1]);
            double interestRate = Double.parseDouble(params[2]);

            // Create a new loan and add it to the marketplace
            if (!loanManager.userHasActiveLoan(currentUser.getUsername())) {
                Loan newLoan = new Loan(currentUser.getUsername(), amount, interestRate, "pending");
                loanManager.addLoan(newLoan);
                loanManager.saveLoansToJson("loans.json");
            }
            else {
                return "You currently have a pending loan request already.";
            }
        } catch (NumberFormatException e) {
           return "Invalid amount or interest rate. Please provide valid numbers.";
        }
        return "Your loan offer has been added to the marketplace.\n";
    }

    public List<String> viewOffers()
    {
        List<String> loanOffers = new ArrayList<>();
        for (Loan loan : loanManager.getLoans()) {
            if(Objects.equals(loan.getStatus(), "pending")) {
                String offer = "Loan request from " + loan.getBorrower() + " for $" + loan.getAmount() + " at " + loan.getInterestRate() + "% interest.";
                loanOffers.add(offer);
            }
        }
        return loanOffers;
    }

    private String acceptLoan(Customer customer, String request)
    {
        // Split the request string by spaces
        String[] params = request.split("\\s+");

        // Ensure the request contains the expected number of parameters
        if (params.length < 2) {
            return "Invalid request.Please provide the username of the borrower.";
        }

        try {

            String borrowerUsername = params[1];
            double amount = Double.parseDouble(params[2]);

            if (!customer.getAccountByType("CURRENT").areFundsAvailable(amount))
            {
                return "You have insufficient funds in your main account to grant this loan request.";
            }

            Loan selectedLoan = null;
            for (Loan loan : loanManager.getLoans()) {
                if (loan.getBorrower().equals(borrowerUsername) && loan.getStatus().equals("pending")) {
                    selectedLoan = loan;
                    break;
                }
            }

            if (selectedLoan != null) {
                Customer borrower = null;

                for (Customer b : customers) {
                    if (b.getUsername().equals(borrowerUsername)) {
                        borrower = b;
                    }
                }
                if(borrower != null) {
                    if (loanManager.hasPositiveCreditScore(borrower)) {
                        loanManager.acceptLoanOffer(selectedLoan);
                        return "You have accepted the loan offer.\n" +
                                "Notifying " + selectedLoan.getBorrower() + "...";
                    } else {
                        return "Offer cannot be accepted. Borrower has a negative credit score.";
                    }
                }
                else {
                    return "Borrower does not exist.";
                }

            } else {
                return "No matching loan offer found or the loan offer is no longer available.";
            }

        }
        catch (NullPointerException e) {
            return "You need to have a default account with funds.";
        }
        catch (Exception e) {
            return "Invalid parameters";
        }

    }

    List<Loan> getOffers()
    {
        return loanManager.getLoans();
    }

    private String rejectLoan(Customer customer, String request)
    {
        String[] params = request.split("\\s+");

        if (params.length < 2) {
            return "Invalid request.Please provide the username of the offeror.";
        }

        try {
            String borrowerUsername = params[1];
            Loan selectedLoan = null;
            for (Loan loan : loanManager.getLoans()) {
                if (loan.getBorrower().equals(borrowerUsername) && loan.getStatus().equals("pending")) {
                    selectedLoan = loan;
                    break;
                }
            }

            if (selectedLoan != null) {
                loanManager.rejectLoanOffer(selectedLoan);
                return "You have rejected the loan offer.\n" +
                        "Notifying " + selectedLoan.getBorrower() + "...";
            } else {
                return "No matching loan offer found or the loan offer is no longer available.";
            }
        }
        catch (Exception e) {
            return "Invalid parameters";
        }

    }
  
    /**
     * This function allows the user to move funds between their own accounts. The function verifies that the requested
     * accounts to transfer money from and to exist for the customer. There is an additional check to verify the user
     * has sufficient funds to execute the operation.
     *
     * @param customer Customer object of the logged in user.
     * @param amountToTransfer The amount requested to be transfered, this is passed in string and converted to double.
     * @param sourceAccName The account name from which the funds are sourced.
     * @param destAccName The account name to which the funds are deposited.
     * @return String representation of the output message - whether the command was successful or not.
     */
  private String moveFundsBetweenAccounts(Customer customer, String amountToTransfer, String sourceAccName, String destAccName){
        double amountToTransferDouble = Double.parseDouble(amountToTransfer);
        Account sourceAccount = customer.getAccountByType(sourceAccName);
        Account destAccount = customer.getAccountByType(destAccName);
        if(sourceAccount == null){
            return String.format("Account '%s' does not exist!", sourceAccName);
        }
        else if(destAccount == null){
            return String.format("Account '%s' does not exist!", destAccName);
        }
        else if(!sourceAccount.areFundsAvailable(amountToTransferDouble)){
            return String.format("Account '%s' does not have sufficient funds!", sourceAccName);
        }
        else{
            sourceAccount.withdrawFunds(amountToTransferDouble);
            destAccount.depositFunds(amountToTransferDouble);
        }

        return String.format("Successfully transferred %.2f from %s to %s.", amountToTransferDouble, sourceAccName, destAccName);
    }

    private String depositOntoAccount(Customer customer, String amountToTransfer, String AccountName)
    {
        double amountToTransferDouble = Double.parseDouble(amountToTransfer);
        Account account = customer.getAccountByType(AccountName);
        if(account == null){
            return String.format("Account '%s' does not exist!", AccountName);
        }
        account.depositFunds(amountToTransferDouble);
        return String.format("Successfully transferred %.2f to %s.", amountToTransferDouble, AccountName);
    }
}
