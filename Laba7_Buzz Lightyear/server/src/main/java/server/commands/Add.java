package server.commands;

import common.data.Product;
import common.forCommunicate.Request;
import common.forCommunicate.Response;
import server.CollectionManager;

/**
 * Команда для добавления нового элемента в коллекцию
 */
public class Add implements Command {
    private final CollectionManager collectionManager;

    public Add(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        if (request.getObjectArgument() == null) {
            return new Response("Ошибка: не передан объект для добавления!", false);
        }

        Product product = request.getObjectArgument();
        String username = request.getUsername();

        boolean success = collectionManager.add(product, username);
        if (success) {
            return new Response("Продукт успешно добавлен в коллекцию!", true);
        } else {
            return new Response("Ошибка при добавлении продукта. Возможны проблемы с базой данных.", false);
        }
    }
}