package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import server.CollectionManager;

import java.util.stream.Collectors;

/**
 * Команда для вывода значений поля unitOfMeasure всех элементов в порядке убывания
 */
public class Print_field_descending_unit_of_measure implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор команды
     *
     * @param collectionManager менеджер коллекции
     */
    public Print_field_descending_unit_of_measure(CollectionManager collectionManager) {
        this.cm = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        if (cm.getCollection().isEmpty()) {
            return new Response("Коллекция пуста.", true);
        }
        String result = cm.getCollection().stream()
                .sorted((p1, p2) -> p2.getUnitOfMeasure().compareTo(p1.getUnitOfMeasure()))
                .map(pr -> pr.getName() + ": " + pr.getUnitOfMeasure())
                .collect(Collectors.joining("\n"));

        return new Response(result, true);
    }
}