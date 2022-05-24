package logger;

public class MessageLogger {

    private static final Logger logger = Logger.getInstance();
    private static final String fileName = "message_log.txt";

    public static void log(String nick, String s) {
        logger.addLog(fileName, nick + ": " + s);
    }
}
