package main.newbank.dtos;

import main.newbank.enums.CustomerType;
import main.newbank.IRepositories.ICustomer;
import java.util.ArrayList;
import java.util.List;

public class Customer implements ICustomer { 
    private String username;
    private String password;
    private boolean isAdmin;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String dateOfBirth;
    private String accountCreationDate;
    private CustomerType customerType;
    private ArrayList<Account> accounts;
    private int creditScore;
    private boolean isDeactivated = false;

    public Customer(String username, String password, boolean isAdmin, String firstName, String lastName, String emailAddress
            , String dateOfBirth, String accountCreationDate, CustomerType customerType) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.dateOfBirth = dateOfBirth;
        this.accountCreationDate = accountCreationDate;
        this.customerType = customerType;
        this.creditScore = 750;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getEmailAddress() {
        return emailAddress;
    }

    @Override
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    @Override
    public String getAccountCreationDate() {
        return accountCreationDate;
    }

    @Override
    public String getCustomerName() {
        return firstName + " " + lastName;
    }

    @Override
    public CustomerType getCustomerType() {
        return customerType;
    }

    @Override
    public double getDiscount() {
        return 0.0;
    }

    @Override
    public double getInterestRate() {
        return 0.0;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    @Override
    public int getAccountsCount() {
        return accounts.size();
    }

    @Override
    public void addBankAccount(Account account) {
        accounts.add(account);
    }

    @Override
    public Account getAccountByType(String accountName) {
        return accounts.stream()
                .filter(acc -> acc.getAccountName().equalsIgnoreCase(accountName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean isDeactivated() {
        return isDeactivated;
    }

    @Override
    public void setDeactivated(boolean deactivated) {
        isDeactivated = deactivated;
    }
}
