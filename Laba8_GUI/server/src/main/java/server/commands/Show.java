package server.commands;

import common.data.Product;
import common.forCommunicate.Request;
import common.forCommunicate.Response;
import common.forCommunicate.ShowData;
import server.CollectionManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Команда для вывода всех элементов коллекции
 */
public class Show implements Command {
    private final CollectionManager cm;

    public Show(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public Response execute(Request request) {
        List<Product> products = cm.getCollectionSnapshot();
        ShowData data = new ShowData(products);

        if (products.isEmpty()) {
            return new Response("Коллекция пуста.", true, data);
        }

        String result = products.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));

        return new Response(result, true, data);
    }
}
