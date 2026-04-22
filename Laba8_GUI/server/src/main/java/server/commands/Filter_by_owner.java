package server.commands;

import common.data.Product;
import common.forCommunicate.Request;
import common.forCommunicate.Response;
import server.CollectionManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Команда для вывода элементов с заданным значением owner
 */
public class Filter_by_owner implements Command {
    private final CollectionManager cm;

    public Filter_by_owner(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public Response execute(Request request) {
        String ownerName = request.getArgument().trim();
        List<Product> filtered = cm.filterByOwner(ownerName);

        String result = filtered.stream()
                .map(Product::toString)
                .collect(Collectors.joining("\n"));

        return new Response(result, true, filtered);
    }
}
