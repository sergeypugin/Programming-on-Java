package commands;

import managers.CollectionManager;

public class Remove_at implements Command {
    private final CollectionManager collectionManager;

    public Remove_at(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String arg) {
        if (validMethodWithArg(arg)) {
            int index = Integer.parseInt(arg);
            collectionManager.removeByPos(index);
        }
    }
}
