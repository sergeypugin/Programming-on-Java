package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;

/**
 * Команда для вывода справки по доступным командам.
 */
public class Help implements Command {
    @Override
    public Response execute(Request request) {
        String helpText = """
                В данной программе реализованы следующие команды:
                help : вывести справку по доступным командам
                info : вывести информацию о коллекции
                show : вывести все элементы коллекции
                add {element} : добавить новый элемент
                update id {element} : обновить значение элемента по id
                remove_by_id id : удалить элемент по его id
                clear : очистить коллекцию
                remove_at index : удалить элемент в заданной позиции
                remove_last : удалить последний элемент
                reorder : отсортировать коллекцию в обратном порядке
                count_by_unit_of_measure unit : количество элементов с заданной единицей измерения
                filter_by_owner owner : вывести элементы конкретного владельца
                print_field_descending_unit_of_measure : вывести единицы измерения в порядке убывания""";
        return new Response(helpText, true);
    }
}
