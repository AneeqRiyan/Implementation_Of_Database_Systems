package shop.storage;

import shop.Customer;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.PrimaryHashMap;

import java.io.IOException;

public class KVStoreImpl implements CustomerStore, CustomerStoreQuery {

    private RecordManager recordManager;
    private PrimaryHashMap<Integer, Customer> customerMap;

    @Override
    public void open() {
        try {
            recordManager = RecordManagerFactory.createRecordManager("customerStore");
            customerMap = recordManager.hashMap("customerMap");
        } catch (IOException e) {
            throw new RuntimeException("Error initializing JDBM", e);
        }
    }

    @Override
    public void insertCustomer(Customer customer) {
        if (customerMap == null) {
            throw new IllegalStateException("Store not opened. Call open() before inserting.");
        }
        customerMap.put(customer.getCustomerId(), customer);
        try {
            recordManager.commit();
        } catch (IOException e) {
            throw new RuntimeException("Error committing to JDBM", e);
        }
    }

    @Override
    public void close() {
        try {
            if (recordManager != null) {
                recordManager.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error closing JDBM", e);
        }
    }

    @Override
    public void cleanUp() {
        try {
            if (recordManager != null) {
                customerMap.clear(); // Clear map instead of deleting by long key
                recordManager.commit();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error cleaning up JDBM", e);
        }
    }

    @Override
    public void queryAllUsers() {
        if (customerMap != null) {
            customerMap.forEach((key, value) -> System.out.println(value));
        } else {
            throw new IllegalStateException("Store not opened. Call open() before querying.");
        }
    }

    @Override
    public void queryTopProduct() {
        // TODO: Implement based on the requirement (if specific)
        System.out.println("Top product query not implemented.");
    }
}
