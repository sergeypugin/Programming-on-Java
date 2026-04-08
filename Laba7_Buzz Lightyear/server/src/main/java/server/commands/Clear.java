package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import server.CollectionManager;

public class Clear implements Command {
    private final CollectionManager collectionManager;

    public Clear(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        String username = request.getUsername();
        long clearedCount = collectionManager.clearByCreator(username);
        if (clearedCount > 0) {
            return new Response("Ваши элементы в коллекции успешно удалены. Всего удалено: " + clearedCount, true);
        } else {
            return new Response("В коллекции нет элементов, принадлежащих вам.", true);
        }
    }
}
