package main.newbank.IRepositories;

import main.newbank.dtos.Customer;
import java.io.PrintWriter;

public interface IDisplayCustomer {
    void displayCustomerDetails(PrintWriter out, Customer customer);
}
 