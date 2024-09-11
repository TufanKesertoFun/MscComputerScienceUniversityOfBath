package main.newbank.repositories;

import com.opencsv.exceptions.CsvValidationException;
import main.newbank.IRepositories.INewBankClientHandler;
import main.newbank.IRepositories.IAnimator;
import main.newbank.IRepositories.ICustomerHandler;
import main.newbank.dtos.CorporateCustomer;
import main.newbank.dtos.Customer;
import main.newbank.dtos.RetailCustomer;
import main.newbank.dtos.SMECustomer;
import main.newbank.dtos.Account;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader; 
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class NewBankClientHandler extends Thread implements INewBankClientHandler {

    private static final int MAX_ATTEMPTS = 3;
    private NewBank bank;
    private BufferedReader in;
    private PrintWriter out;
    private IAnimator animator;
    private Socket socket;
    private ICustomerHandler customerHandler;
    private Map<String, Runnable> adminCommands;
    private Map<String, Runnable> userCommands;
    private Customer loggedInCustomer;


    public NewBankClientHandler(NewBank bank, Socket s, IAnimator animator, ICustomerHandler customerHandler) throws IOException, CsvValidationException {
        this.bank = bank;
        this.socket = s;
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.out = new PrintWriter(s.getOutputStream(), true);
        this.animator = animator;
        this.customerHandler = customerHandler;
        initializeCommands();
    }

    private void initializeCommands() {
        adminCommands = new HashMap<>();
        adminCommands.put("1", this::createCustomer);
        adminCommands.put("2", this::updateCustomer);
        adminCommands.put("3", this::deleteCustomer);
        adminCommands.put("4", this::readCustomer);
        adminCommands.put("5", this::displayAllCustomers);
        adminCommands.put("6", this::logout);


        userCommands = new HashMap<>();
        userCommands.put("LOGOUT", this::logout);


    }

    @Override
    public void run() {
        try {
            animator.displayGreeting(out);
            String initialCommand = in.readLine();
            initialCommand = validateInitialCommand(initialCommand);

            while (true) {
                Customer customer = handleCommand(initialCommand);

                if (customer != null) {
                    if (customer.isAdmin()) {
                        processAdminCommands();
                    } else {
                        processUserCommands(customer);
                    }
                    break;
                } else {
                    initialCommand = validateInitialCommand(in.readLine());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    private void closeResources() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String validateInitialCommand(String initialCommand) throws IOException {
        while (!"LOGIN".equals(initialCommand) && !"REGISTER".equals(initialCommand)) {
            out.println("Command not recognised. Please enter command LOGIN or REGISTER");
            out.flush();
            initialCommand = in.readLine();
        }
        return initialCommand;
    }

    private Customer handleCommand(String initialCommand) throws IOException {
        return "LOGIN".equals(initialCommand) ? login() : register();
    }

    private Customer login() throws IOException {
        Customer customer = null;
        int attempts = 0;

        while (attempts < MAX_ATTEMPTS) {
            out.println("Enter Username");
            out.flush();
            String userName = in.readLine();
            userName = sanitizeInput(userName);

            out.println("Enter Password");
            out.flush();
            String password = in.readLine();
            password = sanitizeInput(password);

            customer = validateLogin(userName, password);
            customer = processLoginResult(customer);

            if (customer != null) {
                this.loggedInCustomer = customer;
                return customer;
            } else {
                attempts++;
                if (attempts >= MAX_ATTEMPTS) {
                    out.println("Too many failed login attempts. The application will now terminate.");
                    out.flush();
                    closeResources();
                    System.exit(1);
                }
            }
        }
        return null;
    }

    private Customer validateLogin(String userName, String password) {
        out.println("Checking Details...");
        out.flush();
        return bank.checkLogInDetails(userName, password);
    }

    private Customer processLoginResult(Customer customer) {
        if (customer == null) {
            out.println("Log In Failed. Invalid Credentials, please try again.");
            out.println("Please type LOGIN if you already have an account, or REGISTER if you need to create one.");
            out.flush();
        } else {
            if (customer.isDeactivated()) {
                out.println("Log In Successful. Note: Your account is currently deactivated. You can reactivate it by contacting support.");
            } else {
                out.println("Log In Successful. What do you want to do?");
            }
            out.flush();
        }
        return customer;
    }

    private Customer register() throws IOException {
        Customer customer = null;
        int attempts = 0;
        boolean validDetails = false;

        while (attempts < MAX_ATTEMPTS && !validDetails) {
            out.println("Select the type of customer you want to be (Retail, Corporate, SME):");
            out.flush();
            String customerType = in.readLine().toUpperCase();

            if (!customerType.equals("RETAIL") && !customerType.equals("CORPORATE") && !customerType.equals("SME")) {
                out.println("Invalid customer type. Please try again.");
                out.flush();
                attempts++;
                if (attempts >= MAX_ATTEMPTS) {
                    out.println("Too many failed attempts. The application will now terminate.");
                    out.flush();
                    closeResources();
                    System.exit(1);
                }
                continue;
            }

            out.println("Please enter a valid username (6 to 15 characters, no spaces):");
            out.flush();
            String userName = in.readLine();
            userName = sanitizeInput(userName);

            out.println("Please enter a valid password (9 to 15 characters, including uppercase letters, lowercase letters, and numbers, with no spaces):");
            out.flush();
            String password = in.readLine();
            password = sanitizeInput(password);

            out.println("Please enter your email address:");
            out.flush();
            String emailAddress = in.readLine();
            emailAddress = sanitizeInput(emailAddress);

            out.println("To continue with the registration, enter your first name:");
            out.flush();
            String firstName = in.readLine();
            firstName = sanitizeInput(firstName);

            out.println("Enter your last name:");
            out.flush();
            String lastName = in.readLine();
            lastName = sanitizeInput(lastName);

            out.println("Enter your date of birth:");
            out.flush();
            String dateOfBirth = in.readLine();
            dateOfBirth = sanitizeInput(dateOfBirth);

            // Declare and initialize additional parameters
            String address = "";
            String phoneNumber = "";
            String businessName = "";
            String businessId = "";
            String smeType = "";
            int employeeCount = 0;

            if (customerType.equals("RETAIL")) {
                out.println("Please enter your address:");
                out.flush();
                address = in.readLine();
                address = sanitizeInput(address);

                out.println("Please enter your phone number:");
                out.flush();
                phoneNumber = in.readLine();
                phoneNumber = sanitizeInput(phoneNumber);

                createRetailCustomer(userName, password, firstName, lastName, emailAddress, dateOfBirth, address, phoneNumber);
            } else if (customerType.equals("CORPORATE")) {
                out.println("Please enter your business name:");
                out.flush();
                businessName = in.readLine();
                businessName = sanitizeInput(businessName);

                out.println("Please enter your business ID:");
                out.flush();
                businessId = in.readLine();
                businessId = sanitizeInput(businessId);

                createCorporateCustomer(userName, password, firstName, lastName, emailAddress, dateOfBirth, businessName, businessId);
            } else if (customerType.equals("SME")) {
                out.println("Please enter the type of SME:");
                out.flush();
                smeType = in.readLine();
                smeType = sanitizeInput(smeType);

                out.println("Please enter the number of employees:");
                out.flush();
                try {
                    employeeCount = Integer.parseInt(in.readLine());
                } catch (NumberFormatException e) {
                    out.println("Invalid number format for employee count.");
                    out.flush();
                    continue;
                }

                createSMECustomer(userName, password, firstName, lastName, emailAddress, dateOfBirth, smeType, employeeCount);
            }

            out.println("Customer created successfully. You can now log in.");
            out.flush();
            validDetails = true;
        }
        return login();
    }


    private void createRetailCustomer(String userName, String password, String firstName, String lastName,
                                      String emailAddress, String dateOfBirth, String address, String phoneNumber) {
        RetailCustomer retailCustomer = new RetailCustomer(userName, password, false, firstName, lastName,
                emailAddress, dateOfBirth, new Date().toString(), address, phoneNumber);
        bank.getAccountManager().addCustomer(retailCustomer);
        bank.getAccountManager().saveCustomerToCSV(retailCustomer);
        out.println("Retail customer created: " + retailCustomer);
        out.flush();
    }

    private void createCorporateCustomer(String userName, String password, String firstName, String lastName,
                                         String emailAddress, String dateOfBirth, String businessName, String businessId) {
        CorporateCustomer corporateCustomer = new CorporateCustomer(userName, password, false, firstName, lastName,
                emailAddress, dateOfBirth, new Date().toString(), businessName, businessId);
        bank.getAccountManager().addCustomer(corporateCustomer);
        bank.getAccountManager().saveCustomerToCSV(corporateCustomer);
        out.println("Corporate customer created: " + corporateCustomer);
        out.flush();
    }


    private void createSMECustomer(String userName, String password, String firstName, String lastName,
                                   String emailAddress, String dateOfBirth, String smeType, int employeeCount) {
        SMECustomer smeCustomer = new SMECustomer(userName, password, false, firstName, lastName,
                emailAddress, dateOfBirth, new Date().toString(), smeType, employeeCount);
        bank.getAccountManager().addCustomer(smeCustomer);
        bank.getAccountManager().saveCustomerToCSV(smeCustomer);
        out.println("SME customer created: " + smeCustomer);
        out.flush();
    }


    private void displayListOfCommands() {
        out.println("Choose an action:");
        out.println("LOGOUT - logout");
        out.println("NEWACCOUNT - register new bank account");
        out.println("DEPOSIT - deposit some money on your account of choice.");
        out.println("MARKETPLACE - enter micro loan marketplace.");
        out.println("MOVE - transfer funds between own accounts");
        out.println("SHOWMYACCOUNTS - view list of your accounts");
        out.println("VIEWACCOUNTDETAILS - view details of a chosen account");
        out.flush();
    }

    private void processUserCommands(Customer customer) throws IOException {
        if (customer != null) {

            while (true) {
              try
              {
                  String request = in.readLine();

                if (request == null) {
                    break;
                }
                Runnable command = userCommands.get(request.toUpperCase());
                if (command != null) {
                    command.run();
                } else {
                    String response = "";
                    if (request.equals("MARKETPLACE")) {
                        response = handleMarketPlaceRequest(customer);
                    } else {
                        response = bank.processRequest(customer, request);
                    }
                    out.println(response);
                    out.flush();
                    displayListOfCommands();
                }
              }
              catch(IndexOutOfBoundsException e) {
                  out.println("Invalid command format, refer to protocol.txt for command usage.");
              }
            }
            animator.displayGoodbye(out);
            out.flush();
            try {
                Thread.sleep(100); // Small delay to ensure message is sent
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void processAdminCommands() throws IOException {
        while (true) {
            out.println("Admin Panel - Choose an option:");
            out.println("1. Create Customer");
            out.println("2. Update Customer");
            out.println("3. Delete Customer");
            out.println("4. Read Customer");
            out.println("5. Display All Customers");
            out.println("6. Logout");
            out.flush();

            String adminCommand = in.readLine();

            Runnable command = adminCommands.get(adminCommand);
            if (command != null) {
                command.run();
                if ("6".equals(adminCommand)) {
                    return;
                }
            } else {
                out.println("Invalid command. Please choose a valid option.");
                out.flush();
            }
        }
    }

    private void createCustomer() {
//        try {
//            // do some admin action to create a customer with valid details
//           // customerHandler.createCustomer();
//        } catch (IOException e) {
//            out.println("Error creating customer: " + e.getMessage());
//        }
    }

    private void updateCustomer() {
        try {
            String userNameToUpdate = promptUser("Enter customer's username to update:");
            userNameToUpdate = sanitizeInput(userNameToUpdate);
            String newPassword = promptUser("Enter new password:");
            newPassword = sanitizeInput(newPassword);
            customerHandler.updateCustomer(userNameToUpdate, newPassword);
        } catch (IOException e) {
            out.println("Error updating customer: " + e.getMessage());
        }
    }

    private void deleteCustomer() {
        try {
            customerHandler.deleteCustomer();

        } catch (IOException e) {
            out.println("Error deleting customer: " + e.getMessage());
        }
    }

    private void readCustomer() {
        try {
            customerHandler.readCustomer();
        } catch (IOException e) {
            out.println("Error reading customer: " + e.getMessage());
        }
    }

    private void displayAllCustomers() {
        try {
            customerHandler.displayAllCustomers();
        } catch (IOException e) {
            out.println("Error displaying customers: " + e.getMessage());
        }
    }

    private void logout() {
        animator.displayGoodbye(out);
        out.flush();
        try {
            Thread.sleep(100); // Small delay to ensure message is sent
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void deactivateAccount() {
        Customer customer = bank.readCustomer("user8"); // Replace with the correct method to get the logged-in customer
        if (customer != null) {
            boolean success = bank.deactivateCustomer(customer.getUsername());
            if (success) {
                out.println("Your account has been deactivated successfully.");
            } else {
                out.println("Failed to deactivate your account. Please try again.");
            }
        } else {
            out.println("You are not logged in.");
        }
        out.flush();
    }


    private void processRequest(String request, PrintWriter out) {
        try {
            if (request.equalsIgnoreCase("DELETE_ACCOUNT")) {
                out.println("Are you sure you want to delete your account? This action cannot be undone. Type 'CONFIRM' to proceed.");
                String confirmation = in.readLine();
                if (confirmation.equalsIgnoreCase("CONFIRM")) {
                    out.println("Enter your username to confirm deletion:");
                    String userName = in.readLine();
                    boolean success = customerHandler.deleteAccount(userName);
                    if (success) {
                        out.println("Your account has been successfully deleted.");
                    } else {
                        out.println("Failed to delete your account. Please try again.");
                    }
                } else {
                    out.println("Account deletion canceled.");
                }
            } else {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String promptUser(String prompt) throws IOException {
        out.println(prompt);
        out.flush();
        return in.readLine();
    }

    private String sanitizeInput(String input) {
        // Allow only alphanumeric characters (ASCII chars)
        // Remove any potentially harmful characters from injection attacks.
        return input.replaceAll("[^a-zA-Z0-9@._-]", "");
    }

    private String handleMarketPlaceRequest(Customer customer) {
        out.println("Would you like to (1) Request a new loan or (2) Accept/Reject an existing loan offer?");
        Scanner scanner = new Scanner(in);
        int choice = scanner.nextInt();
        String response = "";
        if (choice == 1) {
            response = requestNewLoan(customer);
        } else if (choice == 2) {
            response = evaluateLoanOffer(customer);
        } else {
            out.println("Invalid choice. Please try again.");
        }
        return response;
    }

    private String requestNewLoan(Customer currentUser) {
        Scanner scanner = new Scanner(in);

        out.println("Enter the amount you want to loan:");
        double amount = scanner.nextDouble();

        out.println("Enter the interest rate (in percentage):");
        double interestRate = scanner.nextDouble();

        String request = "REQUEST_LOAN " + amount + " " + interestRate;
        String response = bank.processRequest(currentUser, request);

        return response;
    }

    private String evaluateLoanOffer(Customer currentUser) {
        out.println("Welcome to the Micro Loan Marketplace. Available loan offers:\n");
        List<String> offers = bank.viewOffers();

        if (offers.isEmpty()) {
            out.println("No loan offers available.");
            return "";
        } else {
            for (String offer : offers) {
                out.println(offer);
            }
        }

        Scanner scanner = new Scanner(in);

        out.println("Enter the username of the borrower whose loan you want to accept or reject:\n");
        String borrowerUsername = scanner.next();

        if (Objects.equals(currentUser.getUsername(), borrowerUsername)) {
            out.println("You cannot accept or reject your own requests.");
            return "";
        }
        double amount = 0.0;

        for (Loan offer : bank.getOffers())
        {
            if (offer.getBorrower().equals(borrowerUsername)) {
                amount = offer.getAmount();
            }
        }

        out.println("Do you want to accept or reject this request?");
        String action = scanner.next();

        String response = "";
        if (Objects.equals(action, "accept")) {
            String request = "ACCEPT_LOAN " + borrowerUsername + " " + amount;
            response = bank.processRequest(currentUser, request);
        } else if (Objects.equals(action, "reject")) {
            String request = "REJECT_LOAN " + borrowerUsername;
            response = bank.processRequest(currentUser, request);
        } else {
            out.println("Choose a valid action.");
        }
        return response;
    }

    private Customer getLoggedInCustomer() {
        return this.loggedInCustomer;
    }

    private void showMyAccounts() {
        Customer customer = getLoggedInCustomer();
        if (customer == null) {
            out.println("You need to log in to view your accounts.");
            return;
        }

        List<Account> accounts = customer.getAccounts();
        if (accounts.isEmpty()) {
            out.println("You have no accounts associated with your profile.");
        } else {
            out.println("Your Accounts:");
            for (Account account : accounts) {
                out.println(account.toString());
            }
        }
        out.flush();
    }

    private void viewAccount() {
        try {
            out.println("Enter the account name:");
            out.flush();
            String accountName = in.readLine().toUpperCase();

            Customer customer = getLoggedInCustomer();
            if (customer == null) {
                out.println("You need to log in to view account details.");
                return;
            }

            Account account = customer.getAccountByType(accountName);
            if (account == null) {
                out.println("Account not found or does not belong to you.");
            } else {
                out.println("Account Details:");
                out.println(account.toString());
            }
        } catch (IOException e) {
            out.println("An error occurred while trying to fetch account details.");
        }
        out.flush();
    }

    private void viewTransactions() {
        try {
            out.println("Enter the account name:");
            out.flush();
            String accountName = in.readLine().toUpperCase();

            Customer customer = getLoggedInCustomer(); // Implement this method to get the logged-in customer
            if (customer == null) {
                out.println("You need to log in to view transaction history.");
                return;
            }

            Account account = customer.getAccountByType(accountName);
            if (account == null) {
                out.println("Account not found or does not belong to you.");
            } else {
                out.println("Transaction History for " + accountName + ":");
                List<String> transactions = account.getTransactionHistory();
                if (transactions.isEmpty()) {
                    out.println("No transactions found for this account.");
                } else {
                    for (String transaction : transactions) {
                        out.println(transaction);
                    }
                }
            }
        } catch (IOException e) {
            out.println("An error occurred while trying to fetch transaction history.");
        }
        out.flush();
    }

    private void viewAccountDetails() {
        Customer customer = getLoggedInCustomer();
        if (customer == null) {
            out.println("You need to log in to view account details.");
            return;
        }

        try {
            out.println("Would you like to view (1) All accounts or (2) A single account?");
            out.flush();
            String choice = in.readLine().trim();

            if ("1".equals(choice)) {
                showAllAccounts(customer);
            } else if ("2".equals(choice)) {
                viewSingleAccount(customer);
            } else {
                out.println("Invalid choice. Please enter 1 or 2.");
            }
        } catch (IOException e) {
            out.println("An error occurred while fetching account details.");
        }
        out.flush();
    }

    private void showAllAccounts(Customer customer) {
        List<Account> accounts = customer.getAccounts();
        if (accounts.isEmpty()) {
            out.println("You have no accounts associated with your profile.");
        } else {
            out.println("Your Accounts:");
            for (Account account : accounts) {
                out.println(account.toString());
            }
        }
    }

    private void viewSingleAccount(Customer customer) {
        try {
            out.println("Enter the account name:");
            out.flush();
            String accountName = in.readLine().toUpperCase();

            Account account = customer.getAccountByType(accountName);
            if (account == null) {
                out.println("Account not found or does not belong to you.");
            } else {
                out.println("Account Details:");
                out.println(account.toString());
            }
        } catch (IOException e) {
            out.println("An error occurred while trying to fetch account details.");
        }
        out.flush();
    }

}
