package logger;

public class ServerLogger {

    private static final Logger logger = Logger.getInstance();

    public static void log(String s) {
        logger.addLog("server_log.txt", s);
        logger.log(s);
    }
}
