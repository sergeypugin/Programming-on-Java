package commands;

import data.Product;
import managers.CollectionManager;
import managers.Console;

public class Add implements Command {
    private final CollectionManager collectionManager;
    private final Console console;
    public Add(CollectionManager collectionManager, Console console) {
        this.collectionManager = collectionManager;
        this.console=console;
    }
    @Override
    public void execute(String arg) {
        Product newProduct = console.askProduct();
        collectionManager.add(newProduct);
    }
}
