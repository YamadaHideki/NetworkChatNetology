package logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static Logger instance = null;
    private final FileHandler fileHandler = new FileHandler();
    protected int num = 1;
    protected final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
    protected final Date date = new Date();

    private Logger() {}

    public void addLog(String file, String s) {
        s = "[" + simpleDateFormat.format(date) +"] " + s;
        fileHandler.addTextInFile(file, s);
    }

    public void log(String msg) {
        System.out.println(simpleDateFormat.format(date) + " [" + num++ + "] " + msg);
    }

    public static Logger getInstance() {
        if (instance == null) instance = new Logger();
        return instance;
    }
}
