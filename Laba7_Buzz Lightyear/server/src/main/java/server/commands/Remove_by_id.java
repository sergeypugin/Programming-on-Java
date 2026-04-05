package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CollectionManager;

/**
 * Команда для удаления элемента из коллекции по его ID
 */
public class Remove_by_id implements Command {
    private static final Logger logger = LogManager.getLogger(Remove_by_id.class);
    private final CollectionManager collectionManager;

    public Remove_by_id(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        String username = request.getUsername();
        try {
            int id = Integer.parseInt(request.getArgument().trim());
            boolean success = collectionManager.removeById(id, username);
            if (success) {
                logger.info("Запрос <Remove_by_id> (user: {}) успешно выполнен для ID={}", username, id);
                return new Response("Продукт с ID " + id + " успешно удалён", true);
            } else {
                logger.warn("Ошибка при выполнении <Remove_by_id> (user: {}): продукт с ID={} не найден или у пользователя нет прав на его удаление", username, id);
                return new Response("Ошибка: Продукт с таким ID не найден или у вас нет прав на его удаление.", false);
            }
        } catch (NumberFormatException e) {
            logger.error("Ошибка: передан некорректный ID в запрос <Remove_by_id> (user: {})", username);
            return new Response("Ошибка: ID должен быть числом", false);
        }
    }
}