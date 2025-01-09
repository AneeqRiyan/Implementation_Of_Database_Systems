package shop.storage;

import shop.Customer;
import shop.Order;
import shop.Product;

import java.sql.*;
import java.util.List;

public class H2StoreImpl implements CustomerStore, CustomerStoreQuery {

    private Connection connection;

    @Override
    public void open() {
        try {
            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing H2 database", e);
        }
    }

    private void createTables() throws SQLException {
        String createCustomer = "CREATE TABLE CUSTOMERS(id INT PRIMARY KEY, name VARCHAR(255))";
        String createOrder = "CREATE TABLE ORDERS(oid INT PRIMARY KEY, customerId VARCHAR(255), " +
                "FOREIGN KEY (customerId) REFERENCES CUSTOMERS(id))";
        String createProduct = "CREATE TABLE PRODUCTS(pid INT PRIMARY KEY, pname VARCHAR(255))";
        String createOrderItem = "CREATE TABLE ORDERITEMS(otid INT AUTO_INCREMENT PRIMARY KEY, orderId VARCHAR(255), " +
                "productId VARCHAR(255), FOREIGN KEY (orderId) REFERENCES ORDERS(oid), " +
                "FOREIGN KEY (productId) REFERENCES PRODUCTS(pid))";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createCustomer);
            stmt.execute(createOrder);
            stmt.execute(createProduct);
            stmt.execute(createOrderItem);
        }
    }

    @Override
    public void insertCustomer(Customer customer) {
        try {
            connection.setAutoCommit(false);

            // Insert Customer
            String insertCustomerSQL = "INSERT INTO CUSTOMERS (id, name) VALUES (?, ?)";
            try (PreparedStatement psCustomer = connection.prepareStatement(insertCustomerSQL)) {
                psCustomer.setInt(1, customer.getCustomerId());
                psCustomer.setString(2, customer.getUserName());
                psCustomer.executeUpdate();
            }

            // Insert Orders and Products
            for (Order order : customer.getOrders()) {
                String insertOrderSQL = "INSERT INTO ORDERS (oid, customerId) VALUES (?, ?)";
                try (PreparedStatement psOrder = connection.prepareStatement(insertOrderSQL)) {
                    psOrder.setInt(1, order.getOrderId());
                    psOrder.setInt(2, customer.getCustomerId());
                    psOrder.executeUpdate();
                }

                for (Product product : order.getItems()) {
                    // Insert Product
                    String insertProductSQL = "MERGE INTO PRODUCTS (pid, pname) KEY (pid) VALUES (?, ?, ?)";
                    try (PreparedStatement psProduct = connection.prepareStatement(insertProductSQL)) {
                        psProduct.setInt(1, product.getProductId());
                        psProduct.setString(2, product.getName());
                        psProduct.executeUpdate();
                    }

                    // Insert OrderItem
                    String insertOrderItemSQL = "INSERT INTO ORDERITEMS (orderId, productId) VALUES (?, ?)";
                    try (PreparedStatement psOrderItem = connection.prepareStatement(insertOrderItemSQL)) {
                        psOrderItem.setInt(1, order.getOrderId());
                        psOrderItem.setInt(2, product.getProductId());
                        psOrderItem.executeUpdate();
                    }
                }
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Error rolling back transaction", rollbackEx);
            }
            throw new RuntimeException("Error inserting customer data", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Error resetting auto-commit", e);
            }
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error closing H2 database", e);
        }
    }

    @Override
    public void cleanUp() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP ALL OBJECTS");
        } catch (SQLException e) {
            throw new RuntimeException("Error cleaning up H2 database", e);
        }
    }

    @Override
    public void queryAllUsers() {

    }

    @Override
    public void queryTopProduct() {

    }
}
