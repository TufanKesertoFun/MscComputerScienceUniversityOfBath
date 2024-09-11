package main.newbank.IRepositories;



import main.newbank.dtos.Customer;
import main.newbank.enums.CustomerType;

import java.util.List;

public interface INewBank {
    Customer registerCustomer(String userName, String password, String firstName, String lastName, String emailAddress, String DateOfBirth, CustomerType customerType);
    boolean updateCustomer(String userName, String newPassword);
    boolean deleteCustomer(String userName);
    Customer readCustomer(String userName);
    List<Customer> getAllCustomers(); 
    boolean deactivateCustomer(String userName);
    boolean deleteCustomerPermanently(String userName);
}
