package commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Exit implements Command {
    private static final Logger logger = LoggerFactory.getLogger(Exit.class);
    @Override
    public void execute(String arg) {
        logger.info("Программа завершена");
        System.exit(0);
    }
}
