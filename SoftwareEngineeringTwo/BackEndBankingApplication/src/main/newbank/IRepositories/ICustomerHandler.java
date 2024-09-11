package main.newbank.IRepositories;
import main.newbank.enums.CustomerType;

import java.io.IOException;

public interface ICustomerHandler {
    void createCustomer(String userName, String password, String firstName, String lastName, String emailAddress, String dateOfBirth, CustomerType customerType) throws IOException;
    void updateCustomer(String userName, String newPassword) throws IOException;
    void deleteCustomer() throws IOException;

    boolean deleteAccount(String userName) throws IOException;

    void readCustomer() throws IOException;
    void displayAllCustomers() throws IOException;

}
 