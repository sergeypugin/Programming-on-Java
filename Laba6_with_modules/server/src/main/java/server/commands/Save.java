package server.commands;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CollectionManager;
import server.FileManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Команда сохранения коллекции в файл
 */
public class Save {
    private static final Logger logger = LogManager.getLogger(Save.class);
    private final CollectionManager cm;

    public Save(CollectionManager collectionManager) {
        this.cm = collectionManager;
    }

    public void execute() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH-mm-ss_dd-MM-yyyy");
        String formattedDate = formatter.format(new Date());
        File dir = new File("Collections");
        if (!dir.exists()) {
            dir.mkdir();
            logger.info("Создана директория Collections для хранения данных");
        }
        String filePath = "Collections" + File.separator + "Collection_" + formattedDate + ".xml";
        FileManager.writeCollectionToXML(filePath, cm.getCollection());
//        logger.info("Коллекция была сохранена в файл \"{}\"", fileName);
    }
}
