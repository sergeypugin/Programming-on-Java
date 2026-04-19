package server.commands;

import common.data.UnitOfMeasure;
import common.forCommunicate.Request;
import common.forCommunicate.Response;
import server.CollectionManager;

/**
 * Команда для подсчета элементов с заданным значением unitOfMeasure
 */
public class Count_by_unit_of_measure implements Command {
    private final CollectionManager cm;

    public Count_by_unit_of_measure(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public Response execute(Request request) {
        try {
            UnitOfMeasure unit = UnitOfMeasure.valueOf(request.getArgument().trim().toUpperCase());
            long count = cm.countByUnitOfMeasure(unit);

            return new Response("Количество элементов с единицей измерения " + unit + ": " + count, true);
        } catch (IllegalArgumentException e) {
            return new Response("Ошибка: такой единицы измерения не существует. " + e.getMessage(), false);
        }
    }
}