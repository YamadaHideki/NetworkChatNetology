import logger.FileHandler;
import logger.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TestFileHandler {
    private final Logger logger = Logger.getInstance();
    private final FileHandler fileHandler = new FileHandler();
    private final String fileName = "test.txt";
    private final File f = new File(fileName);

    @Test
    public void createFile() {
        boolean createFile = fileHandler.createFile(f);
        Assertions.assertTrue(createFile);
    }

    @Test
    public void deleteFile() {
        fileHandler.createFile(f);
        boolean deleteFile = fileHandler.deleteFile(f);
        Assertions.assertTrue(deleteFile);
    }

    @Test
    public void addTextInFile() {
        fileHandler.deleteFile(f);
        fileHandler.createFile(f);
        fileHandler.addTextInFile(fileName, "Test string");
        fileHandler.addTextInFile(fileName, "Test string 2");
        try (FileInputStream fs = new FileInputStream(f)) {
            int fsReadBytes = fs.read();
            Assertions.assertNotEquals(0, fsReadBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileHandler.deleteFile(f);
    }
}
