package main.newbank.repositories;

import main.newbank.IRepositories.IDisplayCustomer;
import main.newbank.dtos.Customer;
import java.io.PrintWriter;

public class DisplayCustomer implements IDisplayCustomer {
    @Override
    public void displayCustomerDetails(PrintWriter out, Customer customer) {
        out.println("Customer details: username: " + customer.getUsername());
        out.println("First Name: " + customer.getFirstName());
        out.println("Last Name: " + customer.getLastName());
        out.println("Email Address: " + customer.getEmailAddress());
        out.println("Date of Birth: " + customer.getDateOfBirth());
        out.println("Account Creation Date: " + customer.getAccountCreationDate());
        out.flush();
    } 
}
