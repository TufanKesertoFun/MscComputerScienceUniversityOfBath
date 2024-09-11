package main.newbank.repositories;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import main.newbank.dtos.Customer;
import main.newbank.enums.CustomerType;

import com.opencsv.CSVWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class AccountManager {

    private static final String CSV_FILE_PATH = "customers.csv";
    private List<Customer> customers;
 
    public AccountManager()  throws IOException, CsvValidationException {
        customers = new ArrayList<>();
        try {
            loadCustomersFromCSV();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    private void loadCustomersFromCSV() throws CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(CSV_FILE_PATH))) {
            // Skip the header row
            reader.readNext(); // Skip header line

            String[] line;
            while ((line = reader.readNext()) != null) {
                if (isEmptyLine(line)) {
                    continue;
                }

                String key = line[0].trim();
                String password = line[1].trim();
                boolean isAdmin = Boolean.parseBoolean(line[2].trim());
                String firstName = line[3].trim();
                String lastName = line[4].trim();
                String email = line[5].trim();
                String dateOfBirth = line[6].trim();
                String accountCreationDate = line[7].trim();
                String customerTypeStr = line[8].trim().toUpperCase();
                try{
                    CustomerType customerType = CustomerType.valueOf(customerTypeStr);
                    Customer customer = new Customer(
                        key,
                        password,
                        isAdmin,
                        firstName,
                        lastName,
                        email,
                        dateOfBirth,
                        accountCreationDate,
                        customerType
                    );
                    customers.add(customer);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid CustomerType value: " + customerTypeStr);
                }
            }
        }catch (IOException e) {
            e.printStackTrace(); // Eventually replace this with more robust logging
        }
    }

    private static boolean isEmptyLine(String[] line)  {
        for (String field : line) {
            if (!field.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isDuplicateAccount(String emailAddress) {
        return customers.stream().anyMatch(c -> c.getEmailAddress().equalsIgnoreCase(emailAddress));
    }

    public synchronized void addCustomer(Customer customer) {
        if (!isDuplicateAccount(customer.getEmailAddress())) {
            customers.add(customer);
            saveCustomerToCSV(customer);
        } else {
            throw new IllegalArgumentException("Account with this email already exists.");
        }
    }

    public void saveCustomerToCSV(Customer newCustomer) {
        File file = new File(CSV_FILE_PATH);
        try {
            // create FileWriter object with file as parameter and set append mode to true
            FileWriter outputfile = new FileWriter(file, true);

            CSVWriter writer = new CSVWriter(outputfile);

            // Prepare new customer data
            String[] newCustomerData = {
                    newCustomer.getUsername(),
                    newCustomer.getPassword(),
                    Boolean.toString(newCustomer.isAdmin()),
                    newCustomer.getFirstName(),
                    newCustomer.getLastName(),
                    newCustomer.getEmailAddress(),
                    newCustomer.getDateOfBirth(),
                    newCustomer.getAccountCreationDate(),
                    newCustomer.getCustomerType().name()
            };
            writer.writeNext(newCustomerData);

            // closing writer connection
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}