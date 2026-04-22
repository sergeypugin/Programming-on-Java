package common.forCommunicate;

import common.data.Product;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Данные для команды show
 */
public class ShowData implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    private final List<Product> products;

    public ShowData(List<Product> products) {
        this.products = List.copyOf(products);
    }

    public List<Product> getProducts() {
        return products;
    }
}
