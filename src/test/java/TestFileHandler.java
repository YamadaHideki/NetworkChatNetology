import logger.FileHandler;
import logger.Logger;
import org.junit.jupiter.api.*;

import java.io.*;

public class TestFileHandler {
    private final FileHandler fileHandler = new FileHandler();
    private final String fileName = "test.txt";
    private final File f = new File(fileName);

    @BeforeEach
    @DisplayName("Создание файла")
    public void createFile() {
        boolean createFile = fileHandler.createFile(f);
        Assertions.assertTrue(createFile);
    }

    @AfterEach
    @DisplayName("Удаление файла")
    public void deleteFile() {
        boolean deleteFile = fileHandler.deleteFile(f);
        Assertions.assertTrue(deleteFile);
    }

    @Test
    @DisplayName("Тест на добавление текста в файл")
    public void addTextInFile() {

        String[] testString = {"Test string", "Test string two"};

        fileHandler.addTextInFile(fileName, testString[0]);
        fileHandler.addTextInFile(fileName, testString[1]);

        try (FileInputStream fs = new FileInputStream(f);
             BufferedReader br = new BufferedReader(new FileReader(f))) {

            int fsReadBytes = fs.read();
            Assertions.assertNotEquals(0, fsReadBytes);

            int countLines = 0;
            while (br.ready()) {
                Assertions.assertEquals(testString[countLines], br.readLine());
                countLines++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
