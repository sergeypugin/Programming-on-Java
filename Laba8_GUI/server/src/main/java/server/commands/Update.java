package server.commands;

import common.data.Product;
import common.forCommunicate.Request;
import common.forCommunicate.Response;
import server.CollectionManager;

/**
 * Команда для обновления элемента коллекции по его id
 */
public class Update implements Command {
    private final CollectionManager cm;

    public Update(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public Response execute(Request request) {
        if (request.getObjectArgument() == null) {
            return new Response("Ошибка: объект для обновления не передан", false);
        }

        String username = request.getUsername();
        Product product = request.getObjectArgument();

        try {
            long id = Long.parseLong(request.getArgument().trim());
            if (cm.replace(id, product, username)) {
                return new Response("Продукт с ID=" + id + " успешно обновлен", true);
            } else {
                return new Response("Ошибка: продукт с таким ID не найден или у вас нет прав на его изменение.", false);
            }
        } catch (NumberFormatException e) {
            return new Response("Ошибка: ID должен быть числом.", false);
        }
    }
}