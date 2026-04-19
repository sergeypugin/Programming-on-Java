package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import server.CollectionManager;

/**
 * Команда для удаления последнего элемента из коллекции
 */
public class Remove_last implements Command {
    private final CollectionManager cm;

    public Remove_last(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public Response execute(Request request) {
        String username = request.getUsername();
        if (cm.removeLast(username)) {
            return new Response("Последний элемент успешно удален", true);
        } else {
            return new Response("Ошибка: коллекция пуста или у вас нет прав на удаление последнего элемента.", false);
        }
    }
}