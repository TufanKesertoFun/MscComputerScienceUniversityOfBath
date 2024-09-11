package main.newbank.dtos;

import main.newbank.enums.CustomerType;
 
public class CorporateCustomer extends Customer {
    private String businessName;
    private String businessId;

    public CorporateCustomer(String username, String password, boolean isAdmin, String firstName, String lastName,
                             String emailAddress, String dateOfBirth, String accountCreationDate,
                             String businessName, String businessId) {
        super(username, password, isAdmin, firstName, lastName, emailAddress, dateOfBirth, accountCreationDate, CustomerType.CORPORATE);
        this.businessName = businessName;
        this.businessId = businessId;
    }

    @Override
    public String toString() {
        return "CorporateCustomer{" +
                "username='" + getUsername() + '\'' +
                ", businessName='" + businessName + '\'' +
                ", businessId='" + businessId + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", emailAddress='" + getEmailAddress() + '\'' +
                ", dateOfBirth='" + getDateOfBirth() + '\'' +
                ", accountCreationDate='" + getAccountCreationDate() + '\'' +
                '}';
    }
}
