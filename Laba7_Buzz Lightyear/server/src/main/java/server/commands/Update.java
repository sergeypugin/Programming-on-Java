package server.commands;

import common.data.Product;
import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CollectionManager;

/**
 * Команда для обновления элемента коллекции по его id
 */
public class Update implements Command {
    private static final Logger logger = LogManager.getLogger(Update.class);
    private final CollectionManager cm;

    public Update(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public Response execute(Request request) {
        if (request.getObjectArgument() == null) {
            logger.error("Ошибка: в запросе <Update> отсутствует объект продукта");
            return new Response("Ошибка: объект для обновления не передан", false);
        }

        String username = request.getUsername();
        Product product = request.getObjectArgument();

        try {
            int id = Integer.parseInt(request.getArgument().trim());
            if (cm.replace(id, product, username)) {
                logger.info("Запрос <Update> (user: {}) успешно выполнен для ID={}", username, id);
                return new Response("Продукт с ID=" + id + " успешно обновлен", true);
            } else {
                logger.warn("Ошибка при выполнении <Update> (user: {}): продукт с ID={} не найден или у пользователя нет прав на его изменение", username, id);
                return new Response("Ошибка: продукт с таким ID не найден или у вас нет прав на его изменение.", false);
            }
        } catch (NumberFormatException e) {
            logger.error("Ошибка: передан некорректный ID в запрос <Update> (user: {})", username);
            return new Response("Ошибка: ID должен быть числом", false);
        }
    }
}