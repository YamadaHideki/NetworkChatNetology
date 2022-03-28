package logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class FileHandler {
    private final Queue<String> errors = new PriorityQueue<>();

    public void addTextInFile(String fileName, String s) {
        File file = new File(fileName);

        if (!createFile(file)) {
            errors.add("Can't create file: " + "\"" + file + "\"");
        }

        try (FileInputStream fs = new FileInputStream(file);
             FileWriter fw = new FileWriter(file, true)) {
            if (fs.read() == 0) {
                fw.write(s);
            } else {
                fw.write(s + "\n");
            }
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            errors.add("Can't write/read " + fileName);
        }

        logsError();
    }

    public boolean createFile(File f) {
        if(!f.exists()) {
            try {
                if (!f.createNewFile()) {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean deleteFile(File f) {
        if (f.exists()) {
            return f.delete();
        }
        return false;
    }

    public void logsError() {
        for (String s : errors) {
            ServerLogger.log(s);
        }
    }
}
