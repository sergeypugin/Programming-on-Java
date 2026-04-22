package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import server.CollectionManager;

/**
 * Команда для сортировки коллекции в обратном порядке
 */
public class Reorder implements Command {
    private final CollectionManager collectionManager;

    public Reorder(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        if (collectionManager.isEmpty()) {
            return new Response("Коллекция пуста, нечего сортировать", false);
        }
        collectionManager.reorder();
        return new Response("Коллекция успешно отсортирована в обратном порядке", true);
    }
}
