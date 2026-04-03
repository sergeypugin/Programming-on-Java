package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CollectionManager;

/**
 * Команда для удаления элемента по индексу
 */
public class Remove_at implements Command {
    private static final Logger logger = LogManager.getLogger(Remove_at.class);
    private final CollectionManager collectionManager;

    public Remove_at(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        try {
            int index = Integer.parseInt(request.getArgument().trim());
            boolean isRemoved = collectionManager.removeByPos(index);
            if (isRemoved) {
                logger.info("Запрос <Remove_at> успешно выполнен");
                return new Response("Элемент на позиции " + index + " успешно удалён", true);
            } else {
                logger.error("Ошибка: индекс {} вне границ коллекции", index);
                return new Response("Ошибка: индекс вне границ коллекции", false);
            }
        } catch (NumberFormatException e) {
            logger.error("Ошибка: передан некорректный индекс в запрос <Remove_at>");
            return new Response("Ошибка: аргумент должен быть целым числом", false);
        }
    }
}