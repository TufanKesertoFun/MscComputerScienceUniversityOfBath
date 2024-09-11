package main.newbank.repositories;

import main.newbank.dtos.Customer;
import main.newbank.enums.CustomerType;

public class CorporateCustomer extends Customer {
    public CorporateCustomer(String username, String password, boolean isAdmin, String firstName, String lastName, String emailAddress, String dateOfBirth, String accountCreationDate) {
        super(username, password, isAdmin, firstName, lastName, emailAddress, dateOfBirth, accountCreationDate, CustomerType.CORPORATE);
    }
 
    @Override
    public double getDiscount() {
        return 0.10; // 10% discount for corporate customers
    }

    @Override
    public double getInterestRate() {
        return 0.05; // 5% interest rate for corporate customers
    }
}