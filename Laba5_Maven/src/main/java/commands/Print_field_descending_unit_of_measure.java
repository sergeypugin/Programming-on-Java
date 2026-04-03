package commands;

import data.Person;
import data.Product;
import managers.CollectionManager;

import java.util.ArrayList;

public class Print_field_descending_unit_of_measure implements Command {
    private final CollectionManager cm;

    public Print_field_descending_unit_of_measure(CollectionManager collectionManager) {
        this.cm = collectionManager;
    }

    @Override
    public void execute(String arg) {
        if (cm.getCollection().isEmpty()) {
            System.out.println("Коллекция пуста.");
        } else {
            ArrayList<Product> sortedList = new ArrayList<>(cm.getCollection());
            sortedList.sort((p1, p2) -> p2.getUnitOfMeasure().compareTo(p1.getUnitOfMeasure()));
            for (Product pr: cm.getCollection())
                System.out.println(pr.getName()+": "+pr.getUnitOfMeasure());
        }

    }
}
