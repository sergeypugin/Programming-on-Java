package commands;

import data.Product;
import managers.CollectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Count_by_unit_of_measure implements Command {
    private static final Logger logger = LoggerFactory.getLogger(Count_by_unit_of_measure.class);
    private final CollectionManager collectionManager;

    public Count_by_unit_of_measure(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String arg) {
        String upperCase = arg.toUpperCase();
        if (validMethodWithArg(arg)) {
            int c = 0;
            for (Product i : collectionManager.getCollection()) {
                if (i.getUnitOfMeasure().toString().equals(upperCase)) c++;
            }
            if (c>0) System.out.println(arg+" : "+c);
            else logger.error("Такой единицы измерения в коллекции нет");
        }
    }
}
