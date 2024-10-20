package customer_manage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CustomerManage {
    private Map<String, Customer> customers = new HashMap<>();

    private static final String FILE_NAME = "customers.txt";

    public CustomerManage() {
        loadCustomersFromFile();
    }

    public void addCustomer(Customer customer) {
        if (customer.getName() == null || customer.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (!isValidEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (!isValidPhoneNumber(customer.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number format. It must be 10 digits.");
        }
        if (customers.containsKey(customer.getPhone())) {
            throw new IllegalArgumentException("Phone number already exists.");
        } else {
            customers.put(customer.getPhone(), customer);
            saveCustomersToFile();
        }
    }

    public void showCustomers(){
        if(customers.isEmpty()){
            System.out.println("No customers found");
        }else {
            customers.values().forEach(System.out::println);
        }
    }

    public Customer searchCustomerByPhone(String phone){
        return customers.get(phone);
    }

    public void deleteCustomer(String phoneNumber) {
        if (customers.remove(phoneNumber) != null) {

            saveCustomersToFile();
            System.out.println("Customer removed.");
        } else {
            System.out.println("Customer not found.");
        }
    }

    public void editCustomer(String phoneNumber, String newName, String newEmail, String newPhoneNumber){
        Customer customer = customers.get(phoneNumber);
        if(customer != null){
            if(newPhoneNumber != null && !newPhoneNumber.isEmpty() && !newPhoneNumber.equals(phoneNumber)){
                if(!isValidPhoneNumber(newPhoneNumber)){
                    throw  new IllegalArgumentException("Phone number cannot contain special characters");
                }

                customers.remove(phoneNumber);
                customer.setPhone(newPhoneNumber);
                phoneNumber = newPhoneNumber;
            }

            if (newName != null && !newName.isEmpty()) {
                customer.setName(newName);
            }

            if (newEmail != null && !newEmail.isEmpty()) {
                if(!isValidEmail(newEmail)){
                    throw  new IllegalArgumentException("Email cannot contain special characters");
                }
                customer.setEmail(newEmail);
            }

            customers.put(phoneNumber, customer);

            saveCustomersToFile();

        }else {
            System.out.printf("Customer not found");
        }
    }

    private void saveCustomersToFile() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Customer customer : customers.values()) {
                writer.write(customer.getName() + "," + customer.getEmail() + "," + customer.getPhone());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    Customer customer = new Customer(parts[0], parts[1], parts[2]);
                    customers.put(parts[2], customer);
                }
            }
        } catch (IOException e) {
            System.out.println("No existing customer file found. Starting fresh.");
        }
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return  pattern.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "\\d{10}";
        return phoneNumber.matches(phoneRegex);
    }


}
