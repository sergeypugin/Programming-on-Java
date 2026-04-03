package server;

import common.data.Product;
import common.data.UnitOfMeasure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс для управления коллекцией продуктов на сервере
 */
public class CollectionManager {
    private static final Logger logger = LogManager.getLogger(CollectionManager.class);
    private LinkedList<Product> collection;
    private final Date creationDate;
    private int lastId = 0;

    /**
     * Конструктор менеджера коллекции
     */
    public CollectionManager() {
        this.collection = new LinkedList<>();
        this.creationDate = new Date();
    }

    /**
     * Возвращает текущую коллекцию
     *
     * @return список продуктов
     */
    public LinkedList<Product> getCollection() {
        return collection;
    }

    /**
     * Возвращает дату создания коллекции
     *
     * @return дата создания
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Устанавливает новую коллекцию и пересчитывает идентификаторы
     *
     * @param collection новая коллекция для установки
     */
    public void setCollection(LinkedList<Product> collection) {
        this.collection = collection;
        lastId = 0;
        for (Product pr : this.collection) {
            pr.setId(++lastId);
        }
        Collections.sort(this.collection);
        logger.info("Коллекция успешно загружена и отсортирована");
    }

    /**
     * Добавляет новый продукт в коллекцию
     * Идентификатор генерируется автоматически с помощью Stream API
     *
     * @param product - продукт для добавления
     */
    public void add(Product product) {
        lastId++;
        product.setId(lastId);
        collection.add(product);
        Collections.sort(collection);
        logger.debug("Продукт успешно добавлен с ID={}", lastId);
    }

    /**
     * Очищает коллекцию
     */
    public void clear() {
        collection.clear();
        logger.debug("Коллекция очищена");
    }

    /**
     * Удаляет элемент по его позиции в коллекции
     *
     * @param index позиция элемента (начиная с 0)
     */
    public boolean removeByPos(int index) {
        if (index >= 0 && index < collection.size()) {
            collection.remove(index);
            logger.debug("Элемент на позиции {} удалён", index);
            return true;
        } else {
            logger.error("Ошибка: попытка удаления по несуществующему индексу {}", index);
            return false;
        }
    }

    /**
     * Удаляет продукт из коллекции по его ID
     *
     * @param id - идентификатор продукта
     * @return true если продукт найден и удалён, иначе false
     */
    public boolean removeById(int id) {
        boolean isRemoved = collection.removeIf(p -> p.getId().equals(id));
        if (isRemoved) {
            logger.debug("Продукт с id {} успешно удалён", id);
        } else {
            logger.error("Ошибка: продукт с id {} не найден", id);
        }
        return isRemoved;
    }

    /**
     * Заменяет элемент по заданному ID на новый
     *
     * @param id ID заменяемого элемента
     * @param updatedProduct новый продукт
     * @return true если продукт найден и обновлён, иначе false
     */
    public boolean replace(int id, Product updatedProduct) {
        boolean isRemoved = removeById(id);
        if (isRemoved) {
            updatedProduct.setId(id);
            collection.add(updatedProduct);
            Collections.sort(collection);
            logger.debug("Продукт с id {} успешно обновлён", id);
            return true;
        }
        return false;
    }

    /**
     * Удаляет последний элемент из коллекции
     *
     * @return true если элемент удалён, false если коллекция была пуста
     */
    public boolean removeLast() {
        if (collection.isEmpty()) {
            logger.error("Ошибка: коллекция пуста, удалять нечего!");
            return false;
        }
        collection.removeLast();
        logger.debug("Последний элемент удалён");
        return true;
    }

    /**
     * Подсчитывает количество элементов с заданной единицей измерения
     *
     * @param unit - единица измерения для фильтрации
     * @return количество найденных элементов
     */
    public long countByUnitOfMeasure(UnitOfMeasure unit) {
        return collection.stream()
                    .filter(p -> p.getUnitOfMeasure() == unit)
                    .count();
    }

    /**
     * Возвращает список продуктов, принадлежащих указанному владельцу
     *
     * @param ownerName имя владельца
     * @return отфильтрованный список продуктов
     */
    public List<Product> filterByOwner(String ownerName) {
        return collection.stream()
                .filter(p -> p.getOwner() != null && p.getOwner().getName().equals(ownerName))
                .collect(Collectors.toList());
    }
}