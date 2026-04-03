package server.commands;

import common.data.Product;
import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CollectionManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Команда для вывода элементов с заданным значением owner
 */
public class Filter_by_owner implements Command {
    private static final Logger logger = LogManager.getLogger(Filter_by_owner.class);
    private final CollectionManager cm;

    public Filter_by_owner(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public Response execute(Request request) {
        String ownerName = request.getArgument().trim();
        List<Product> filtered = cm.filterByOwner(ownerName);

        if (filtered.isEmpty()) {
            logger.info("Запрос <Filter_by_owner> выполнен, совпадений не найдено");
            return new Response("Элементов с таким владельцем не найдено.", true);
        }

        String result = filtered.stream()
                .map(Product::toString)
                .collect(Collectors.joining("\n"));

        logger.info("Запрос <Filter_by_owner> успешно выполнен");
        return new Response(result, true);
    }
}