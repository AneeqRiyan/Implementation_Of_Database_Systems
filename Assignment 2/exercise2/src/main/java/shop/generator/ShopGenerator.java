package shop.generator;

import shop.Customer;
import shop.Order;
import shop.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ShopGenerator {

    private static final AtomicInteger customerIdCounter = new AtomicInteger(1);
    private static final AtomicInteger orderIdCounter = new AtomicInteger(1);
    private static final AtomicInteger productIdCounter = new AtomicInteger(1);

    public static List<Product> generateProducts(int numProducts) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < numProducts; i++) {
            products.add(new Product(productIdCounter.getAndIncrement(), "Product" + i));
        }
        return products;
    }

    public static List<Order> generateOrders(List<Product> products, int numOrders) {
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < numOrders; i++) {
            List<Product> orderProducts = new ArrayList<>();
            int numOrderProducts = 1 + (int) (Math.random() * Math.min(5, products.size())); // At least one product per order
            for (int j = 0; j < numOrderProducts; j++) {
                orderProducts.add(products.get((int) (Math.random() * products.size())));
            }
            orders.add(new Order(orderIdCounter.getAndIncrement(), "Shipping Address " + i, orderProducts));
        }
        return orders;
    }

    public static Customer generateCustomer(List<Product> products, int maxOrders) {
        List<Order> orders = generateOrders(products, 1 + (int) (Math.random() * maxOrders)); // At least one order
        return new Customer(customerIdCounter.getAndIncrement(), "CustomerName", "Address");
    }
}
