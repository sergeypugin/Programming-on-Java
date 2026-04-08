package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;

/**
 * Команда для вывода справки по доступным командам.
 */
public class Help implements Command {
    @Override
    public Response execute(Request request) {
        String helpText = "В данной программе реализованы следующие команды:\n" +
                "help : вывести справку по доступным командам\n" +
                "info : вывести информацию о коллекции\n" +
                "show : вывести все элементы коллекции\n" +
                "add {element} : добавить новый элемент\n" +
                "update id {element} : обновить значение элемента по id\n" +
                "remove_by_id id : удалить элемент по его id\n" +
                "clear : очистить коллекцию\n" +
                "execute_script file_name : исполнить скрипт из файла\n" +
                "exit : завершить работу клиента\n" +
                "remove_at index : удалить элемент в заданной позиции\n" +
                "remove_last : удалить последний элемент\n" +
                "reorder : отсортировать коллекцию в обратном порядке\n" +
                "count_by_unit_of_measure unit : количество элементов с заданной единицей измерения\n" +
                "filter_by_owner owner : вывести элементы конкретного владельца\n" +
                "print_field_descending_unit_of_measure : вывести единицы измерения в порядке убывания";
        return new Response(helpText, true);
    }
}