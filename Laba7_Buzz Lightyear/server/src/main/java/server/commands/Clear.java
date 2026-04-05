package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CollectionManager;

public class Clear implements Command {
    private static final Logger logger = LogManager.getLogger(Clear.class);
    private final CollectionManager collectionManager;

    public Clear(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        String username = request.getUsername();
        long clearedCount = collectionManager.clearByCreator(username);
        if (clearedCount > 0) {
            logger.info("Запрос <Clear> (user: {}) успешно выполнен, удалено {} элементов", username, clearedCount);
            return new Response("Ваши элементы в коллекции успешно удалены. Всего удалено: " + clearedCount, true);
        } else {
            logger.info("Запрос <Clear> (user: {}) выполнен, но для пользователя не найдено элементов для удаления", username);
            return new Response("В коллекции нет элементов, принадлежащих вам.", true);
        }
    }
}
