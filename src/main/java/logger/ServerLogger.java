package logger;

public class ServerLogger {

    private static final Logger logger = Logger.getInstance();
    private static final String fileName = "server_log.txt";

    public static void log(String s) {
        logger.addLog(fileName, s);
        logger.log(s);
    }
}
