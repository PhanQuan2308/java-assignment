package customer_manage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Main {
    public static final Scanner sc = new Scanner(System.in);

    public static void viewCustomers(CustomerManage customerManage) {
        customerManage.showCustomers();
    }

    public static void addCustomer(CustomerManage customerManage) {
        String name;
        do {
            System.out.print("Enter name: ");
            name = sc.nextLine();
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty. Please enter a valid name.");
            }
        } while (name.isEmpty());

        String email;
        do {
            System.out.print("Enter email: ");
            email = sc.nextLine();
            if (!CustomerManage.isValidEmail(email)) {
                System.out.println("Invalid email format. Please enter a valid email.");
            }
        } while (!CustomerManage.isValidEmail(email));

        String phone;
        boolean validPhone = false;
        while (!validPhone) { // Lặp lại cho đến khi nhập đúng định dạng số điện thoại
            System.out.print("Enter phone number: ");
            phone = sc.nextLine();
            if (!CustomerManage.isValidPhoneNumber(phone)) {
                System.out.println("Invalid phone number format. It must be 10 digits. Please enter a valid phone number.");
            } else {
                try {
                    Customer customer = new Customer(name, email, phone);
                    customerManage.addCustomer(customer);
                    System.out.println("Customer added successfully.");
                    validPhone = true; // Số điện thoại hợp lệ, thoát khỏi vòng lặp
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }

    public static void editCustomer(CustomerManage customerManage) {
        System.out.print("Enter the phone number of the customer to edit: ");
        String phone = sc.nextLine();

        Customer customer = customerManage.searchCustomerByPhone(phone);
        if (customer != null) {
            // Nhập tên mới (không bắt buộc)
            String name;
            do {
                System.out.print("Enter new name (or press Enter to skip): ");
                name = sc.nextLine();
                if (!name.isEmpty() && name.trim().isEmpty()) {
                    System.out.println("Name cannot be empty spaces. Please enter a valid name.");
                }
            } while (!name.isEmpty() && name.trim().isEmpty());

            // Nhập email mới (nếu có)
            String email;
            do {
                System.out.print("Enter new email (or press Enter to skip): ");
                email = sc.nextLine();
                if (!email.isEmpty() && !CustomerManage.isValidEmail(email)) {
                    System.out.println("Invalid email format. Please enter a valid email.");
                }
            } while (!email.isEmpty() && !CustomerManage.isValidEmail(email));

            // Nhập số điện thoại mới (nếu có)
            String newPhone;
            boolean validPhone = false;
            while (!validPhone) {
                System.out.print("Enter new phone number (or press Enter to skip): ");
                newPhone = sc.nextLine();
                if (!newPhone.isEmpty()) {
                    if (!CustomerManage.isValidPhoneNumber(newPhone)) {
                        System.out.println("Invalid phone number format. It must be 10 digits. Please enter a valid phone number.");
                    } else {
                        try {
                            customerManage.editCustomer(phone, name, email, newPhone);
                            System.out.println("Customer edited successfully.");
                            validPhone = true; // Số điện thoại hợp lệ, thoát khỏi vòng lặp
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    }
                } else {
                    // Nếu không nhập số điện thoại mới, tiến hành cập nhật các thông tin khác
                    try {
                        customerManage.editCustomer(phone, name, email, newPhone);
                        System.out.println("Customer edited successfully.");
                        validPhone = true;
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
            }
        } else {
            System.out.println("Customer not found.");
        }
    }

    public static void searchCustomerByPhone(CustomerManage customerManage) {
        System.out.print("Enter phone number: ");
        String phone = sc.nextLine();

        Customer customer = customerManage.searchCustomerByPhone(phone);

        if (customer != null) {
            System.out.println("Customer found: " + customer);
        } else {
            System.out.println("Customer not found.");
        }
    }

    public static void deleteCustomer(CustomerManage customerManage) {
        System.out.print("Enter phone number: ");
        String phone = sc.nextLine();

        Customer customer = customerManage.searchCustomerByPhone(phone);
        if (customer != null) {
            customerManage.deleteCustomer(phone);
            System.out.println("Customer deleted successfully.");
        } else {
            System.out.println("Customer not found.");
        }
    }

    private static void addMultipleCustomers(CustomerManage customerManage) {
        System.out.print("Enter the number of customers to add: ");
        int n;
        try {
            n = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return;
        }

        List<Customer> customersToAdd = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            System.out.println("Enter details for customer " + i + ":");

            String name;
            do {
                System.out.print("Enter name: ");
                name = sc.nextLine();
                if (name.isEmpty()) {
                    System.out.println("Name cannot be empty. Please enter a valid name.");
                }
            } while (name.isEmpty());

            String email;
            do {
                System.out.print("Enter email: ");
                email = sc.nextLine();
                if (!CustomerManage.isValidEmail(email)) {
                    System.out.println("Invalid email format. Please enter a valid email.");
                }
            } while (!CustomerManage.isValidEmail(email));

            String phone;
            do {
                System.out.print("Enter phone number: ");
                phone = sc.nextLine();
                if (!CustomerManage.isValidPhoneNumber(phone)) {
                    System.out.println("Invalid phone number format. It must be 10 digits.");
                }
            } while (!CustomerManage.isValidPhoneNumber(phone));

            customersToAdd.add(new Customer(name, email, phone));
        }

        try {
            customerManage.addCustomersInBatch(customersToAdd);
            System.out.println("All customers added successfully.");
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error adding customers in batch: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        CustomerManage customerManage = new CustomerManage();

        while (true) {
            System.out.println("\nCustomer Management System:");
            System.out.println("1. View all customers");
            System.out.println("2. Add new customer");
            System.out.println("3. Search customer by phone number");
            System.out.println("4. Edit customer information");
            System.out.println("5. Delete customer");
            System.out.println("6. Add multiple customers");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 7.");
                continue;
            }

            switch (choice) {
                case 1:
                    viewCustomers(customerManage);
                    break;
                case 2:
                    addCustomer(customerManage);
                    break;
                case 3:
                    searchCustomerByPhone(customerManage);
                    break;
                case 4:
                    editCustomer(customerManage);
                    break;
                case 5:
                    deleteCustomer(customerManage);
                    break;
                case 6:
                    addMultipleCustomers(customerManage);
                    break;
                case 7:
                    System.out.println("Exiting the system.");
                    customerManage.shutdown();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
