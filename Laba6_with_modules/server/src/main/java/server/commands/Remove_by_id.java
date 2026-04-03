package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import server.CollectionManager;

/**
 * Команда для удаления элемента из коллекции по его ID
 */
public class Remove_by_id implements Command {
    private final CollectionManager collectionManager;

    public Remove_by_id(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        try {
            int id = Integer.parseInt(request.getArgument().trim());
            boolean success = collectionManager.removeById(id);
            if (success) {
                return new Response("Продукт с ID " + id + " успешно удалён", true);
            } else {
                return new Response("Ошибка: Продукт с таким ID не найден", false);
            }
        } catch (NumberFormatException e) {
            return new Response("Ошибка: ID должен быть числом", false);
        }
    }
}