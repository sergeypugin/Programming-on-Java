package commands;

import data.Product;
import managers.CollectionManager;


public class Filter_by_owner implements Command {
    private final CollectionManager collectionManager;

    public Filter_by_owner(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String arg) {
        if (validMethodWithArg(arg)) {
            for (Product i : collectionManager.getCollection()) {
                if (i.getOwner().getName().equals(arg)) {
                    System.out.println(i);
                    System.out.flush();
                }
            }
        }
    }
}
