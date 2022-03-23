package logger;

public class MessageLogger {

    private static final Logger logger = Logger.getInstance();

    public static void log() {
        logger.log("test");
    }
}
