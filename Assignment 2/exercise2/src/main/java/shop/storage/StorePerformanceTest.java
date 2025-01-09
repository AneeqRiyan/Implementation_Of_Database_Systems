package shop.storage;

import shop.Customer;
import shop.Product;
import shop.generator.ShopGenerator;

import java.util.ArrayList;
import java.util.List;

public class StorePerformanceTest {

    public static void main(String[] args) {
        final int NUM_CUSTOMERS = 10_000;
        final int MAX_ORDERS_PER_CUSTOMER = 10;
        final int MAX_PRODUCTS_PER_ORDER = 5;

        // Generate Products
        List<Product> products = ShopGenerator.generateProducts(100);

        // Generate Customers
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < NUM_CUSTOMERS; i++) {
            customers.add(ShopGenerator.generateCustomer(products, MAX_ORDERS_PER_CUSTOMER));
        }

        // Measure insertion time for KVStoreImpl
        KVStoreImpl kvStore = new KVStoreImpl();
        kvStore.open();
        long kvStartTime = System.currentTimeMillis();
        for (Customer customer : customers) {
            kvStore.insertCustomer(customer);
        }
        long kvEndTime = System.currentTimeMillis();
        kvStore.close();
        System.out.println("KVStoreImpl insertion time: " + (kvEndTime - kvStartTime) + " ms");

        // Measure insertion time for H2StoreImpl
        H2StoreImpl h2Store = new H2StoreImpl();
        h2Store.open();
        long h2StartTime = System.currentTimeMillis();
        for (Customer customer : customers) {
            h2Store.insertCustomer(customer);
        }
        long h2EndTime = System.currentTimeMillis();
        h2Store.close();
        System.out.println("H2StoreImpl insertion time: " + (h2EndTime - h2StartTime) + " ms");

        /*
        Results Explanation:
        - KVStoreImpl is optimized for write operations due to its simplicity and use of a key-value structure.
        - H2StoreImpl, being a relational database, incurs additional overhead for maintaining schema integrity
          and relationships but offers more powerful query capabilities.
        - Observed time differences are expected based on these trade-offs.
        my result:
            KVStoreImpl insertion time: 78887 ms
            H2StoreImpl insertion time: 157 ms
        - In my case the H2 is performing better and that can be because of implementation or because of system performance
        */
    }
}
