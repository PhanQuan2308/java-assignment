package customer_manage;

import java.util.Scanner;

public class Main {

    public static final Scanner sc = new Scanner(System.in);

    public static void viewCustomers(CustomerManage customerManage){
        customerManage.showCustomers();
    }

    public static void addCustomer(CustomerManage customerManage){
        System.out.printf("Enter name: ");
        String name = sc.nextLine();

        System.out.printf("Enter email: ");
        String email = sc.nextLine();

        System.out.printf("Enter phone number: ");
        String phone = sc.nextLine();

        try {
            Customer customer = new Customer(name, email, phone);
            customerManage.addCustomer(customer);
            System.out.printf("Customer added successfully.");
        }catch (IllegalArgumentException e){
            System.out.printf(e.getMessage());
        }
    }

    public static void editCustomer(CustomerManage customerManage){
        System.out.printf("Enter phone customer to edit");
        String phone = sc.nextLine();

        System.out.printf("Enter name: ");
        String name = sc.nextLine();

        System.out.printf("Enter email: ");
        String email = sc.nextLine();

        System.out.printf("Enter phone number: ");
        String newPhone = sc.nextLine();

        try {
            customerManage.editCustomer(phone,name,email,newPhone);
            System.out.printf("Customer edit successfully.");
        } catch (IllegalArgumentException e){
            System.out.printf(e.getMessage());
        }
    }

    public static void searchCustomerByPhone(CustomerManage customerManage){
        System.out.printf("Enter phone number: ");
        String phone = sc.nextLine();

        customerManage.searchCustomerByPhone(phone);

        if(customerManage.searchCustomerByPhone(phone) != null){
            System.out.printf("Customer search successfully.");
        }else {
            System.out.printf("Customer not found.");
        }
    }

    public static void deleteCustomer(CustomerManage customerManage){
        System.out.printf("Enter phone number: ");
        String phone = sc.nextLine();

        customerManage.deleteCustomer(phone);
        if(customerManage.searchCustomerByPhone(phone) != null){
            System.out.printf("Customer delete successfully.");
        }else {
            System.out.printf("Customer not found.");
        }
    }
    private static void addMultipleCustomers(CustomerManage manager) {
        System.out.print("Enter the number of customers to add: ");
        int n = Integer.parseInt(sc.nextLine());

        for (int i = 1; i <= n; i++) {
            System.out.println("Enter details for customer " + i + ":");
            addCustomer(manager);
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

            int choice = Integer.parseInt(sc.nextLine());

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
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}