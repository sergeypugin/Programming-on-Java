package managers;

import data.Product;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionManager {
    private static final Logger logger = LoggerFactory.getLogger(CollectionManager.class);
    private LinkedList<Product> collection;
    private Date creationDate;
    private int lastId = 0;

    public CollectionManager() {
        this.collection = new LinkedList<>();
        this.creationDate = new Date();
    }

    public LinkedList<Product> getCollection() {
        return collection;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCollection(LinkedList<Product> collection) {
        this.collection = collection;
        for (Product pr : collection) {
            lastId++;
            pr.setId(lastId);
        }
    }

//    static private class C implements Comparator<String>{
//        @Override
//        public int compare(String o1, String o2) {
//            return ;
//        }
//    }

    public void add(Product product) {
        lastId++;
        product.setId(lastId);
        collection.add(product);
        Collections.sort(collection);
    }

    public void clear() {
        collection.clear();
        lastId = 0;
    }

    public void removeByPos(int index) {
        if (index >= 0 && index < collection.size()) {
            collection.remove(index);
        } else {
            logger.error("Ошибка: индекс вне диапазона.");
        }
    }

    public void removeById(int id) {
        boolean isFound = false;
        int index = 0;
        for (var product : collection) {
            if (product.getId() == id) {
                isFound = true;
                break;
            }
            index++;
        }
        if (isFound) removeByPos(index);
        else logger.error("Продукт с таким ID не найден.");
    }

    public void replace(int updateId, Console console) {
        boolean isFound = false;
        for (Product pr : collection) {
            if (pr.getId() == updateId) {
                isFound = true;
                break;
            }
        }
        if (isFound) {
            removeById(updateId);
            Product updatedProduct = console.askProduct();
            updatedProduct.setId(updateId);
            collection.addLast(updatedProduct);
            Collections.sort(collection);
        } else logger.error("Продукт с таким ID не найден.");
    }

    public void removeLast() {
        if (collection.isEmpty()) logger.error("Ошибка: коллекция пуста, удалять нечего!");
        else collection.remove(collection.size() - 1);
    }
}