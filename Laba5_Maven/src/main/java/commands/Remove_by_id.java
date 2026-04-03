package commands;

import managers.CollectionManager;

public class Remove_by_id implements Command {
    private final CollectionManager collectionManager;

    public Remove_by_id(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String arg) {
        if (validMethodWithArg(arg)) {
            int deleteId = Integer.parseInt(arg);
            collectionManager.removeById(deleteId);
        }
    }
}
