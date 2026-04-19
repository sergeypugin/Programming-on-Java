package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import server.CollectionManager;

/**
 * Команда для удаления элемента по индексу
 */
public class Remove_at implements Command {
    private final CollectionManager collectionManager;

    public Remove_at(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        String username = request.getUsername();
        try {
            long index = Long.parseLong(request.getArgument().trim());
            boolean isRemoved = collectionManager.removeByPos(index, username);
            if (isRemoved) {
                return new Response("Элемент на позиции " + index + " успешно удалён", true);
            } else {
                return new Response("Ошибка: элемент на указанной позиции не найден или у вас нет прав на его удаление.", false);
            }
        } catch (NumberFormatException e) {
            return new Response("Ошибка: аргумент должен быть целым числом.", false);
        }
    }
}