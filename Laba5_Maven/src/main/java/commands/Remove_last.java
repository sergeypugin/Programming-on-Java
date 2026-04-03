package commands;

import managers.CollectionManager;

public class Remove_last implements Command {
    private final CollectionManager collectionManager;

    public Remove_last(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String arg) {
        collectionManager.removeLast();
    }
}