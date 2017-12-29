package filesHandler;

import org.apache.logging.log4j.Logger;
import updateScript.UpdateScript;

import java.io.*;
import java.nio.file.Path;

import static filesHandler.FileReadUtils.readSqlFile;
import static filesHandler.FileReadUtils.removeSqlExtension;
import static org.apache.logging.log4j.LogManager.getLogger;

public class UpdateFile extends UpdateScript {
    private final static Logger logger = getLogger("UpdateFile");

    private Path path;

    public UpdateFile(Path path) throws IOException {
        this(path, parseDescription(path));
    }

    UpdateFile(Path path, String description) throws IOException {
        super(description, readSqlFile(path));
        this.path = path;
    }

    private static String parseDescription(Path path) {
        return removeSqlExtension(path);
    }

    public String getFileName() {
        return path.getFileName().toString();
    }

}
