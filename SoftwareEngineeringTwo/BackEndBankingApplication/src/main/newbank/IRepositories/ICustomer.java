package main.newbank.IRepositories;
import main.newbank.enums.CustomerType;


import main.newbank.dtos.Account;

import java.io.IOException;

public interface ICustomer {
    String getPassword();
    void setPassword(String password);
    String getUsername();
    String getFirstName();
    String getLastName();
    String getEmailAddress();
    String getDateOfBirth();
    String getAccountCrea tionDate();
    boolean isAdmin();// Include this method
    String getCustomerName();
    CustomerType getCustomerType();
    double getDiscount();
    double getInterestRate();
    void addBankAccount(Account account);
    int getAccountsCount();
    Account getAccountByType(String accountName);
    boolean isDeactivated();
    void setDeactivated(boolean deactivated);
}
