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
        String username = request.getUsername();
        try {
            int index = Integer.parseInt(request.getArgument().trim());
            boolean isRemoved = collectionManager.removeByPos(index, username);
            if (isRemoved) {
                logger.info("Запрос <Remove_at> (user: {}) успешно выполнен для index={}", username, index);
                return new Response("Элемент на позиции " + index + " успешно удалён", true);
            } else {
                logger.warn("Ошибка при выполнении <Remove_at> (user: {}): элемент с index={} не найден или у пользователя нет прав на его удаление", username, index);
                return new Response("Ошибка: элемент на указанной позиции не найден или у вас нет прав на его удаление.", false);
            }
        } catch (NumberFormatException e) {
            logger.error("Ошибка: передан некорректный индекс в запрос <Remove_at> (user: {})", username);
            return new Response("Ошибка: аргумент должен быть целым числом", false);
        }
    }
}