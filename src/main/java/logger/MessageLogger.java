package logger;

public class MessageLogger {

    private static final Logger logger = Logger.getInstance();

    public static void log(String s) {
        logger.addLog("message_log.txt", s);
    }
}
