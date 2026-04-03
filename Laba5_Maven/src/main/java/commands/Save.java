package commands;

import managers.CollectionManager;
import managers.FileManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Save implements Command {
    private final CollectionManager collectionManager;

    public Save(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
//    public void execute(String arg) {
//        FileManager.writeCollectionToXML("Collection.xml",collectionManager.getCollection());
//    }
    public void execute(String arg) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH-mm-ss_dd-MM-yyyy");
        String formattedDate = formatter.format(new Date());
        String fileName = "Collection_" + formattedDate + ".xml";
        FileManager.writeCollectionToXML(fileName, collectionManager.getCollection());
        logger.info("Коллекция была сохранена в файл \"{}\"", fileName);
    }
}
