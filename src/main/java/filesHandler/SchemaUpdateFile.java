package filesHandler;

import org.apache.logging.log4j.Logger;
import versions.Version;
import java.io.*;
import java.nio.file.Path;

import static org.apache.logging.log4j.LogManager.getLogger;

public class SchemaUpdateFile  extends UpdateFile implements Comparable<SchemaUpdateFile>{
    private final static Logger logger = getLogger("SchemaUpdateFile");

    private Version version;
    private String schema;

    /**
     * Returns an instance of SchemaUpdateFile. File name must be in the format of VERSION-SCHEMA-DESCRIPTION.SQL
     * where VERSION is in the format NUMBER.NUMBER.NUMBER.
     * For example- 1.4.5-customers-addedLastNameColumn.sql
     * If you want to use hyphens in your SCHEMA or DESCRIPTION you can use {@link #SchemaUpdateFile(Path, String, String)}
     * @param path
     */
    public SchemaUpdateFile(Path path){
        this(path, parseSchema(path), parseDescription(path));
    }

    public SchemaUpdateFile(Path path, String schema, String description){
        super(path, description);
        this.version = parseVersion(path);
        this.schema = schema;
    }

    private Version parseVersion(Path path) {
        return new Version(getHyphenatedStringPart(removeSqlExtension(path), 0));
    }

    private static String parseSchema(Path path) {
        return getHyphenatedStringPart(removeSqlExtension(path), 1);
    }

    private static String parseDescription(Path path) {
        return getHyphenatedStringPart(removeSqlExtension(path), 2);
    }

    private static String getHyphenatedStringPart(String string, int index){
        String[] stringParts = string.split("-");
        return stringParts[index];
    }

    @Override
    public int compareTo(SchemaUpdateFile schemaUpdateFile) {
        return version.compareTo(schemaUpdateFile.getVersion());
    }

    public Version getVersion() {
        return version;
    }

    public String getSchema() {
        return schema;
    }
}
