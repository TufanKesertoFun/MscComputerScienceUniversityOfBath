package main.newbank.repositories;

import main.newbank.IRepositories.ICustomerHandler;
import main.newbank.IRepositories.INewBank;
import main.newbank.IRepositories.IPromptUser;
import main.newbank.IRepositories.IDisplayCustomer;
import main.newbank.dtos.Customer;
import main.newbank.enums.CustomerType;

import java.io.IOException;
import java.io.PrintWriter; 
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerHandler implements ICustomerHandler {
    private INewBank bank;
    private PrintWriter out;
    private IPromptUser promptUser;
    private IDisplayCustomer displayCustomer;
    private static final Logger logger = Logger.getLogger( CustomerHandler.class.getName() );

    public CustomerHandler(INewBank bank, PrintWriter out, IPromptUser promptUser, IDisplayCustomer displayCustomer) {
        this.bank = bank;
        this.out = out;
        this.promptUser = promptUser;
        this.displayCustomer = displayCustomer;
    }

    @Override
    public void updateCustomer(String userName, String newPassword) throws IOException {
        try {
        boolean success = bank.updateCustomer(userName, newPassword);
        if (success) {
            out.println("Customer updated successfully.");
        } else {
            out.println("Failed to update customer. Please try again.");
        }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating customer", e);
            out.println("An error occurred. Please try again.");
        } finally {
        out.flush();
        }
    }
    

    @Override
    public void deleteCustomer() throws IOException {
        try {
        String userName = promptUser.promptUser(out, "Enter customer's username to delete:");
        boolean success = bank.deleteCustomer(userName);
        if (success) {
            out.println("Customer deleted successfully.");
        } else {
            out.println("Failed to delete customer. Please try again.");
        }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error deleting customer", e);
            out.println("An error occurred. Please try again.");
        } finally {
        out.flush();
        }
    }

    @Override
    public boolean deleteAccount(String userName) throws IOException {
        return bank.deleteCustomerPermanently(userName);
    }


    @Override
    public void readCustomer() throws IOException {
        try {
        String userName = promptUser.promptUser(out, "Enter customer's username to read:");
        Customer customer = bank.readCustomer(userName);
        if (customer != null) {
            displayCustomer.displayCustomerDetails(out, customer);
        } else {
            out.println("Customer not found. Please try again.");
        }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading customer", e);
            out.println("An error occurred. Please try again.");
        } finally {
        out.flush();
        }
    }

    @Override
    public void createCustomer(String userName, String password, String firstName, String lastName, String emailAddress, String dateOfBirth, CustomerType customerType) throws IOException {
       try{
        Customer newCustomer = bank.registerCustomer(userName, password, firstName, lastName, emailAddress, dateOfBirth, customerType);
        if (newCustomer != null) {
            out.println("Customer created successfully.");
        } else {
            out.println("Failed to create customer. Please try again.");
        }
    } catch (Exception e) {
        logger.log(Level.SEVERE, "Error creating customer", e);
        out.println("An error occurred. Please try again.");
    } finally {
           out.flush();
    }
    }

    @Override
    public void displayAllCustomers() throws IOException {
        try {
        List<Customer> customers = bank.getAllCustomers();
        out.println(String.format("%-15s%-15s%-15s%-25s%-15s%-25s",
                "Customer ID", "First Name", "Last Name", "Email Address",
                "Date of Birth", "Account Creation Date"));
        out.println(String.format("%-15s%-15s%-15s%-25s%-15s%-25s",
                "------------", "----------", "---------", "-------------", "------------", "-------------------"));
        for (Customer customer : customers) {
            out.println(String.format("%-15s%-15s%-15s%-25s%-15s%-25s",
                    customer.getUsername() != null ? customer.getUsername() : "",
                    customer.getFirstName() != null ? customer.getFirstName() : "",
                    customer.getLastName() != null ? customer.getLastName() : "",
                    customer.getEmailAddress() != null ? customer.getEmailAddress() : "",
                    customer.getDateOfBirth() != null ? customer.getDateOfBirth() : "",
                    customer.getAccountCreationDate() != null ? customer.getAccountCreationDate() : ""));
        }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error displaying all customers", e);
            out.println("An error occurred. Please try again.");
        }
        out.flush();
    }

}
