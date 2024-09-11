package main.newbank.repositories;

import main.newbank.dtos.Customer;
import main.newbank.enums.CustomerType;

public class IndividualCustomer extends Customer {
    public IndividualCustomer(String username, String password, boolean isAdmin, String firstName, String lastName, String emailAddress, String dateOfBirth, String accountCreationDate) {
        super(username, password, isAdmin, firstName, lastName, emailAddress, dateOfBirth, accountCreationDate, CustomerType.INDIVIDUAL);
    }

    @Override
    public double getDiscount() {
        return 0.05; // 5% discount for individual customers
    }

    @Override
    public double getInterestRate() {
        return 0.03; // 3% interest rate for individual customers
    }
} 