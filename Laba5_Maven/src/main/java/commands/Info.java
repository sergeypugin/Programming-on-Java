package commands;

import managers.CollectionManager;

public class Info implements Command {
    private final CollectionManager collectionManager;
    public Info(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public void execute(String arg) {
        System.out.println("Информация о коллекции:");
        System.out.println("    Тип: " + collectionManager.getCollection().getClass().getName());
        System.out.println("    Дата инициализации: " + collectionManager.getCreationDate());
        System.out.println("    Количество элементов: " + collectionManager.getCollection().size());
    }
}
