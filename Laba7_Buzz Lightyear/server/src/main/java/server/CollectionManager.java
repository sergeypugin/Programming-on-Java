package server;

import common.data.Product;
import common.data.UnitOfMeasure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Менеджер коллекции.
 * Все публичные методы синхронизированы для безопасной многопоточной работы.
 * Коллекция в памяти обновляется исключительно после успешной операции в БД.
 * Команды чтения работают с коллекцией в памяти.
 */
public class CollectionManager {
    private static final Logger logger = LogManager.getLogger(CollectionManager.class);
    private final LinkedList<Product> collection = new LinkedList<>();
    private final Date creationDate = new Date();
    private final DatabaseManager db;

    public CollectionManager(DatabaseManager db) {
        this.db = db;
    }

    public synchronized void loadFromDatabase() throws SQLException {
        collection.clear();
        try {
            collection.addAll(db.loadAllProducts());
            Collections.sort(collection);
            // Внимание: загрузка всех продуктов в память может не выполниться,
            // т.к. в базе данных - long, а в LinkedList - int
        } catch (SQLException e) {
            throw e;
        }
    }

    // Всё для чтения
    public synchronized LinkedList<Product> getCollection() {
        return collection;
    }

    public synchronized Date getCreationDate() {
        return creationDate;
    }

    public synchronized long countByUnitOfMeasure(UnitOfMeasure unit) {
        return collection.stream()
                .filter(p -> p.getUnitOfMeasure() == unit)
                .count();
    }

    public synchronized List<Product> filterByOwner(String ownerName) {
        return collection.stream()
                .filter(p -> p.getOwner() != null
                        && p.getOwner().getName().equals(ownerName))
                .collect(Collectors.toList());
    }

    // Всё для записи

    public synchronized boolean add(Product product, String creator) {
        try {
            product.setCreatorUsername(creator);
            long id = db.insertProduct(product, creator);
            product.setId(id);
            collection.add(product);
            Collections.sort(collection);
            logger.debug("Продукт с id={} успешно добавлен пользователем {}", id, creator);
            return true;
        } catch (SQLException e) {
            logger.error("Ошибка добавления продукта в базу данных: {}", e.getMessage());
            return false;
        }
    }

    public synchronized boolean removeByPos(long index, String userName) {
        if (index < 0 || index >= collection.size()) return false;
        Product target = collection.get((int) index);
        return removeById(target.getId(), userName);
    }

    public synchronized boolean removeById(long id, String userName) {
        try {
            boolean dbDeleted = db.deleteProduct(id, userName);
            if (!dbDeleted) return false;
            collection.removeIf(p -> p.getId() == id);
            logger.debug("Продукт id={} удалён пользователем {}", id, userName);
            return true;
        } catch (SQLException e) {
            logger.error("Ошибка удаления продукта id={}: {}", id, e.getMessage());
            return false;
        }
    }

    public synchronized boolean removeLast(String userName) {
        Product last = collection.getLast();
        return removeById(last.getId(), userName);
    }

    public synchronized boolean replace(long id, Product product, String userName) {
        try {
            boolean dbUpdated = db.updateProduct(id, product, userName);
            if (!dbUpdated) return false;
            collection.removeIf(p -> p.getId() == id);
            product.setId(id);
            product.setCreatorUsername(userName);
            collection.add(product);
            Collections.sort(collection);
            logger.debug("Продукт id={} обновлён пользователем {}", id, userName);
            return true;
        } catch (SQLException e) {
            logger.error("Ошибка обновления продукта с id={}: {}", id, e.getMessage());
            return false;
        }
    }

    public synchronized long clearByCreator(String userName) {
        try {
            // TODO: без (long)-а сработает?
            long count = (long) db.deleteUserProducts(userName);
            collection.removeIf(p -> userName.equals(p.getCreatorUsername()));
            logger.debug("Пользователь {} удалил все свои {} продуктов", userName, count);
            return count;
        } catch (SQLException e) {
            logger.error("Ошибка удаления продуктов пользователя {}: {}", userName, e.getMessage());
            return 0;
        }
    }

    public synchronized void reorder() {
        Collections.reverse(collection);
    }
}