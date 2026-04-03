package commands;

import managers.CollectionManager;
import managers.Console;

public class Update implements Command {
    private final CollectionManager collectionManager;
    private final Console console;

    public Update(CollectionManager collectionManager, Console console) {
        this.collectionManager = collectionManager;
        this.console = console;
    }

    @Override
    public void execute(String arg) {
        if (validMethodWithArg(arg)) {
            int updateId = Integer.parseInt(arg);
            collectionManager.replace(updateId, console);
        }
    }
}
