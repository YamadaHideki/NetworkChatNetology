import logger.FileHandler;
import logger.Logger;
import logger.ServerLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestLogger {
    private final String testFile = "test.txt";

    /*@Test
    public void logger() {
        Logger.getInstance().addLog("test.txt", "Test");
        File file = new File(testFile);
        Assertions.assertTrue(file.exists());
        boolean deleteFile = file.delete();
    }*/
}
