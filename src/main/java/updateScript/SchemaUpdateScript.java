package updateScript;

import versions.Version;

public class SchemaUpdateScript extends UpdateScript implements Comparable<SchemaUpdateScript> {
    private String schema;
    private Version version;

    private SchemaUpdateScript(String description, String contents) {
        super(description, contents);
    }

    public SchemaUpdateScript(String description, String contents, String schema, Version version) {
        super(description, contents);
        this.schema = schema;
        this.version = version;
    }

    public int compareTo(SchemaUpdateScript schemaUpdateScript) {
        return version.compareTo(schemaUpdateScript.getVersion());
    }

    public Version getVersion() {
        return this.version;
    }

    public String getSchema() {
        return schema;
    }
}
