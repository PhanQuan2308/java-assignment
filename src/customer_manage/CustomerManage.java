package customer_manage;

import java.io.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;

public class CustomerManage {
    private ConcurrentHashMap<String, Customer> customers = new ConcurrentHashMap<>();
    private static final String FILE_NAME = "customers.txt";
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private ReentrantLock lock = new ReentrantLock();

    public CustomerManage() {
        loadCustomersFromFile();
    }

    // Thêm khách hàng mới và lưu trực tiếp vào file (append vào cuối)
    public void addCustomer(Customer customer) {
        lock.lock();
        try {
            if (customer.getName() == null || customer.getName().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be null or empty.");
            }
            if (!isValidEmail(customer.getEmail())) {
                throw new IllegalArgumentException("Invalid email format.");
            }
            if (!isValidPhoneNumber(customer.getPhone())) {
                throw new IllegalArgumentException("Invalid phone number format. It must be 10 digits.");
            }

            // Kiểm tra xem số điện thoại có tồn tại trong bộ nhớ không
            Customer existingCustomer = customers.putIfAbsent(customer.getPhone(), customer);
            if (existingCustomer != null) {
                throw new IllegalArgumentException("Phone number already exists."); // Số điện thoại đã tồn tại
            }

            // Append khách hàng vào file sau khi kiểm tra
            saveCustomerToFile(customer);
        } finally {
            lock.unlock();
        }
    }

    // Append khách hàng vào cuối file
    private void saveCustomerToFile(Customer customer) {
        lock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) { // Chế độ append
            writer.write(customer.getName() + "," + customer.getEmail() + "," + customer.getPhone());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void addCustomersInBatch(List<Customer> customerList) throws InterruptedException, ExecutionException {
        int batchSize = 1000;
        List<Future<Void>> futures = new ArrayList<>();

        for (int i = 0; i < customerList.size(); i += batchSize) {
            List<Customer> batch = customerList.subList(i, Math.min(i + batchSize, customerList.size()));

            Future<Void> future = executorService.submit(() -> {
                processBatch(batch);
                return null;
            });

            futures.add(future);
        }

        for (Future<Void> future : futures)  {
            future.get();
        }

        // Không cần ghi đè toàn bộ file nữa, chỉ cần append các khách hàng mới
    }

    private void processBatch(List<Customer> batch) {
        for (Customer customer : batch) {
            if (isValidCustomer(customer)) {
                customers.put(customer.getPhone(), customer);  // Lưu vào bộ nhớ
                saveCustomerToFile(customer);  // Append từng khách hàng vào file
            }
        }
    }

    private boolean isValidCustomer(Customer customer) {
        return customer.getName() != null && !customer.getName().isEmpty()
                && isValidEmail(customer.getEmail())
                && isValidPhoneNumber(customer.getPhone());
    }

    public void showCustomers() {
        if (customers.isEmpty()) {
            System.out.println("No customers found");
        } else {
            customers.values().forEach(System.out::println);
        }
    }

    public Customer searchCustomerByPhone(String phone) {
        return customers.get(phone);
    }

    public void deleteCustomer(String phoneNumber) {
        lock.lock();
        try {
            if (customers.remove(phoneNumber) != null) {
                System.out.println("Customer removed.");
                // Nếu cần ghi đè lại toàn bộ file sau khi xóa
                saveAllCustomersToFile();
            } else {
                System.out.println("Customer not found.");
            }
        } finally {
            lock.unlock();
        }
    }

    public void editCustomer(String phoneNumber, String newName, String newEmail, String newPhoneNumber) {
        lock.lock();
        try {
            Customer customer = customers.get(phoneNumber);
            if (customer != null) {
                // Kiểm tra nếu newPhoneNumber đã tồn tại nhưng không phải của chính khách hàng đang sửa
                if (newPhoneNumber != null && !newPhoneNumber.isEmpty() && !newPhoneNumber.equals(phoneNumber)) {
                    if (!isValidPhoneNumber(newPhoneNumber)) {
                        throw new IllegalArgumentException("Invalid phone number format.");
                    }
                    // Nếu số điện thoại đã tồn tại cho một khách hàng khác, ném ngoại lệ
                    if (customers.containsKey(newPhoneNumber)) {
                        throw new IllegalArgumentException("Phone number already exists for another customer.");
                    }
                    // Cập nhật số điện thoại trong Map
                    customers.remove(phoneNumber); // Xóa khách hàng cũ với phoneNumber cũ
                    customer.setPhone(newPhoneNumber); // Đặt số điện thoại mới
                    phoneNumber = newPhoneNumber; // Cập nhật phoneNumber để lưu lại sau
                }

                // Cập nhật tên và email nếu có thay đổi
                if (newName != null && !newName.isEmpty()) {
                    customer.setName(newName);
                }
                if (newEmail != null && !newEmail.isEmpty()) {
                    if (!isValidEmail(newEmail)) {
                        throw new IllegalArgumentException("Invalid email format.");
                    }
                    customer.setEmail(newEmail);
                }

                // Lưu lại thông tin khách hàng với số điện thoại mới
                customers.put(phoneNumber, customer);
                saveAllCustomersToFile(); // Ghi lại toàn bộ file sau khi chỉnh sửa
            } else {
                System.out.println("Customer not found.");
            }
        } finally {
            lock.unlock();
        }
    }

    // Ghi đè toàn bộ khách hàng hiện tại vào file (sử dụng khi xóa hoặc chỉnh sửa)
    private void saveAllCustomersToFile() {
        lock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) { // Không dùng append
            for (Customer customer : customers.values()) {
                writer.write(customer.getName() + "," + customer.getEmail() + "," + customer.getPhone());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void loadCustomersFromFile() {
        lock.lock();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    Customer customer = new Customer(parts[0], parts[1], parts[2]);
                    customers.put(parts[2], customer);  // Load vào bộ nhớ
                }
            }
        } catch (IOException e) {
            System.out.println("No existing customer file found. Starting fresh.");
        } finally {
            lock.unlock();
        }
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(emailRegex);
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "\\d{10}";
        return phoneNumber.matches(phoneRegex);
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
