package shop;

import java.io.Serializable;

public class Product implements Serializable {

    /*
     * productId should be unique for every new Product!
     */
    private int productId;
    private String productName;

    public Product(int productId, String productName) {
        this.productId = productId;
        this.productName = productName;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return productName;
    }
}
