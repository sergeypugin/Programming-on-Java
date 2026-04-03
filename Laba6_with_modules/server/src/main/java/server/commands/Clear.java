package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CollectionManager;

public class Clear implements Command {
    private static final Logger logger = LogManager.getLogger(Clear.class);
    private final CollectionManager collectionManager;

    public Clear(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        collectionManager.clear();
        logger.info("Коллекция удалена");
        return new Response("Коллекция успешно удалена",true);
    }
}
