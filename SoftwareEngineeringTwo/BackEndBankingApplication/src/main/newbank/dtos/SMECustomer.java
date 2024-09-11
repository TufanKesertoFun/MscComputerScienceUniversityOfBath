package main.newbank.dtos;

import main.newbank.enums.CustomerType;

public class SMECustomer extends Customer { 
    private String smeType;
    private int employeeCount;

    public SMECustomer(String username, String password, boolean isAdmin, String firstName, String lastName,
                       String emailAddress, String dateOfBirth, String accountCreationDate,
                       String smeType, int employeeCount) {
        super(username, password, isAdmin, firstName, lastName, emailAddress, dateOfBirth, accountCreationDate, CustomerType.NON_PROFIT);
        this.smeType = smeType;
        this.employeeCount = employeeCount;
    }

    @Override
    public String toString() {
        return "SMECustomer{" +
                "username='" + getUsername() + '\'' +
                ", smeType='" + smeType + '\'' +
                ", employeeCount=" + employeeCount +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", emailAddress='" + getEmailAddress() + '\'' +
                ", dateOfBirth='" + getDateOfBirth() + '\'' +
                ", accountCreationDate='" + getAccountCreationDate() + '\'' +
                '}';
    }
}
