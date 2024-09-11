package main.newbank.repositories;

import main.newbank.dtos.Customer;
import main.newbank.enums.CustomerType;

public class NonProfitCustomer extends Customer {
    public NonProfitCustomer(String username, String password, boolean isAdmin, String firstName, String lastName, String emailAddress, String dateOfBirth, String accountCreationDate) {
        super(username, password, isAdmin, firstName, lastName, emailAddress, dateOfBirth, accountCreationDate, CustomerType.NON_PROFIT);
    }

    @Override
    public double getDiscount() {
        return 0.15; // 15% discount for non-profit customers
    }

    @Override
    public double getInterestRate() { 
        return 0.02; // 2% interest rate for non-profit customers
    }
}