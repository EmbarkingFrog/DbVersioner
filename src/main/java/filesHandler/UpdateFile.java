package filesHandler;

import updateScript.UpdateScript;

import java.io.*;
import java.nio.file.Path;

import static filesHandler.FileReadUtils.readSqlFile;
import static filesHandler.FileReadUtils.removeSqlExtension;

public class UpdateFile extends UpdateScript {
    private Path path;

    UpdateFile(Path path) throws IOException {
        this(path, parseDescription(path));
    }

    private UpdateFile(Path path, String description) throws IOException {
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
