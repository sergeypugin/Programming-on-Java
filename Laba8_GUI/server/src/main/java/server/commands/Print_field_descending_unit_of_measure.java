package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import common.data.Product;
import server.CollectionManager;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Команда для вывода значений поля unitOfMeasure всех элементов в порядке убывания
 */
public class Print_field_descending_unit_of_measure implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор команды
     *
     * @param collectionManager менеджер коллекции
     */
    public Print_field_descending_unit_of_measure(CollectionManager collectionManager) {
        this.cm = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        var collection = cm.getCollectionSnapshot();
        if (collection.isEmpty()) {
            return new Response("Коллекция пуста.", true);
        }

        List<Product> sortedProducts = collection.stream()
                .sorted(Comparator.comparing(Product::getUnitOfMeasure).reversed())
                .toList();

        List<String> units = sortedProducts.stream()
                .map(product -> product.getUnitOfMeasure().name())
                .toList();

        String result = sortedProducts.stream()
                .map(product -> product.getName() + ": " + product.getUnitOfMeasure())
                .collect(Collectors.joining("\n"));

        return new Response(result, true, units);
    }
}
