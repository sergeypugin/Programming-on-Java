package commands;

import managers.CollectionManager;
import java.util.Collections;

public class Reorder implements Command {
    private final CollectionManager collectionManager;

    public Reorder(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String arg) {
//        if (collectionManager.getCollection().isEmpty())
//            Console.printError("Ошибка: коллекция пуста!");
//        else
        Collections.reverse(collectionManager.getCollection());
    }
}
