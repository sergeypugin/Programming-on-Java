package commands;

import managers.CollectionManager;

public class Clear implements Command {
    private final CollectionManager collectionManager;

    public Clear(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String arg) {
        collectionManager.clear();
    }
}
