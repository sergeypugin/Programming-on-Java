package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Exit {
    private static final Logger logger = LogManager.getLogger(Exit.class);

    public void execute(String arg) {
        logger.info("Программа завершена");
        System.exit(0);
    }
}
