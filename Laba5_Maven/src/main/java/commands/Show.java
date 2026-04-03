package commands;

import data.Product;
import managers.CollectionManager;

public class Show implements Command {
    private final CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String arg) {
        if (collectionManager.getCollection().isEmpty()) {
            System.out.println("Коллекция пуста.");
        } else {
            for (Product product : collectionManager.getCollection()) {
                System.out.println(product);
            }
        }
    }
}
