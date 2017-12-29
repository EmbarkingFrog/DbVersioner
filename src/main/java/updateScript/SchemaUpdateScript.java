package updateScript;

import versions.Version;

public class SchemaUpdateScript extends UpdateScript implements Comparable<SchemaUpdateScript> {
    private String schema;
    private Version version;

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

    @Override
    public String toString(){
        return String.format("version [%s], schema [%s], description [%s]",
                this.getVersion(), this.getSchema(), this.getDescription());
    }
}
