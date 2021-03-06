package filesHandler;

import updateScript.SchemaUpdateScript;
import versions.Version;

import java.io.*;
import java.nio.file.Path;

import static filesHandler.FileReadUtils.*;

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

}
