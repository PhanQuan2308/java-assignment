package customer_manage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CustomerManageTest {

    public static void testAdding5000Customers(CustomerManage customerManage) {
        List<Customer> customersToAdd = new ArrayList<>();
        for (int i = 0; i < 50000; i++) {
            String name = "Customer" + i;
            String email = "customer" + i + "@test.com";
            String phone = String.format("%010d", i + 1000000000); // Tạo số điện thoại ngẫu nhiên 10 chữ số

            customersToAdd.add(new Customer(name, email, phone));
        }

        try {
            long startTime = System.currentTimeMillis();  // Đo thời gian bắt đầu
            customerManage.addCustomersInBatch(customersToAdd);
            long endTime = System.currentTimeMillis();  // Đo thời gian kết thúc
            System.out.println("All 50000l customers added successfully.");
            System.out.println("Time taken: " + (endTime - startTime) + " ms");
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error adding customers in batch: " + e.getMessage());
        }


    }

    public static void testRetrievingCustomers(CustomerManage customerManage) {
        long startTime = System.currentTimeMillis();
        customerManage.showCustomers();
        long endTime = System.currentTimeMillis();
        System.out.println("All customers retrieved successfully.");
    }

    public static void main(String[] args) {
        CustomerManage customerManage = new CustomerManage();

        testAdding5000Customers(customerManage);



        customerManage.shutdown();
    }
}
