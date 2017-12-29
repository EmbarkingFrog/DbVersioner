package filesHandler;

import updateScript.SchemaUpdateScript;
import versions.Version;

import java.io.*;
import java.nio.file.Path;

import static filesHandler.FileReadUtils.readSqlFile;
import static filesHandler.FileReadUtils.removeSqlExtension;

public class SchemaUpdateFile extends SchemaUpdateScript {

    /**
     * Returns an instance of SchemaUpdateFile. File name must be in the format of VERSION-SCHEMA-DESCRIPTION.SQL
     * where VERSION is in the format NUMBER.NUMBER.NUMBER.
     * For example- 1.4.5-customers-addedLastNameColumn.sql
     * If you want to use hyphens in your SCHEMA or DESCRIPTION you can use {@link #SchemaUpdateFile(Path, String, String)}
     *
     * @param path
     */
    SchemaUpdateFile(Path path) throws IOException {
        this(path, parseSchema(path), parseDescription(path));
    }

    SchemaUpdateFile(Path path, String schema, String description) throws IOException {
        super(description, readSqlFile(path), schema, parseVersion(path));
    }

    private static Version parseVersion(Path path) {
        return new Version(getHyphenatedStringPart(removeSqlExtension(path.getFileName()), 0));
    }

    private static String parseSchema(Path path) {
        return getHyphenatedStringPart(removeSqlExtension(path.getFileName()), 1);
    }

    private static String parseDescription(Path path) {
        return getHyphenatedStringPart(removeSqlExtension(path.getFileName()), 2);
    }

    private static String getHyphenatedStringPart(String string, int partIndex) {
        String[] stringParts = string.split("-");
        return stringParts[partIndex];
    }

}
