package filesHandler;

import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.apache.logging.log4j.LogManager.getLogger;

public class UpdateFile {
    private final static Logger logger = getLogger("UpdateFile");

    Path path;
    String description;

    public UpdateFile(Path path) {
        this(path, parseDescription(path));
    }

    UpdateFile(Path path, String description) {
        this.path = path;
        this.description = description;
    }

    private static String parseDescription(Path path) {
        return removeSqlExtension(path);
    }


    public String readSqlFile() throws IOException {
        logger.info("Reading file: [{}]", path.toString());
        StringBuilder stringBuilder = new StringBuilder();
        Stream<String> stream = Files.lines(path);
        stream.forEach(line -> {
            if (!isCommentLine(line)) {
                stringBuilder.append(line);
            }
        });
        return stringBuilder.toString();
    }

    private static boolean isCommentLine(String line) {
        return line.startsWith("--");
    }

    static String removeSqlExtension(Path path) {
        String pathString = path.toString();
        logger.debug("Formatting SchemaUpdateFile {} to be without extension", pathString);
        String sqlExtension = pathString.substring(pathString.length() - 4);
        if (!sqlExtension.toLowerCase().equals(".sql")) {
            throw new IllegalArgumentException("SchemaUpdateFile file path must end in .sql!");
        }
        String fileNameWithoutExtension = pathString.substring(0, pathString.length() - 4);
        logger.debug("File [{}] without extension is [{}]", pathString, fileNameWithoutExtension);
        return fileNameWithoutExtension;
    }

    public String getDescription() {
        return description;
    }

    public String getFileName() {
        return path.getFileName().toString();
    }

}
