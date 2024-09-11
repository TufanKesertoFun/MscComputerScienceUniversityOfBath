package main.newbank.dtos;

import main.newbank.enums.CustomerType;

public class RetailCustomer extends Customer {
    private String address;
    private String phoneNumber; 

    public RetailCustomer(String username, String password, boolean isAdmin, String firstName, String lastName,
                          String emailAddress, String dateOfBirth, String accountCreationDate,
                          String address, String phoneNumber) {
        super(username, password, isAdmin, firstName, lastName, emailAddress, dateOfBirth, accountCreationDate, CustomerType.INDIVIDUAL);
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "RetailCustomer{" +
                "username='" + getUsername() + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", emailAddress='" + getEmailAddress() + '\'' +
                ", dateOfBirth='" + getDateOfBirth() + '\'' +
                ", accountCreationDate='" + getAccountCreationDate() + '\'' +
                '}';
    }
}
