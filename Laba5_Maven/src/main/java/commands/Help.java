package commands;

public class Help implements Command{
    @Override
    public void execute(String arg) {
        System.out.println("В данной программе реализованы следующие комманды:");
        System.out.println("help : вывести справку по доступным командам");
        System.out.println("info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        System.out.println("show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        System.out.println("add {element} : добавить новый элемент в коллекцию");
        System.out.println("update id {element} : обновить значение элемента коллекции, id которого равен заданному");
        System.out.println("remove_by_id id : удалить элемент из коллекции по его id");
        System.out.println("clear : очистить коллекцию");
        System.out.println("save : сохранить коллекцию в файл");
        System.out.println("execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
        System.out.println("exit : завершить программу (без сохранения в файл)");
        System.out.println("remove_at index : удалить элемент, находящийся в заданной позиции коллекции (index)");
        System.out.println("remove_last : удалить последний элемент из коллекции");
        System.out.println("reorder : отсортировать коллекцию в порядке, обратном нынешнему");
        System.out.println("count_by_unit_of_measure unitOfMeasure : вывести количество элементов, значение поля unitOfMeasure которых равно заданному");
        System.out.println("filter_by_owner owner : вывести элементы, значение поля owner которых равно заданному");
        System.out.println("print_field_descending_unit_of_measure : вывести значения поля unitOfMeasure всех элементов в порядке убывания");
    }
}
