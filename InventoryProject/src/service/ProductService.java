package service;

import model.Product;
import java.util.List;

public interface ProductService {
    int addProduct(Product p) throws Exception;
    List<Product> getAllProducts() throws Exception;
    boolean updateProduct(Product p) throws Exception;
    boolean deleteProduct(int id) throws Exception;
    Product getProductById(int id) throws Exception;
}
