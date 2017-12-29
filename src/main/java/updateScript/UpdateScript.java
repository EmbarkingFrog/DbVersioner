package updateScript;

public class UpdateScript {
    private String description;
    private String script;

    public UpdateScript(String description, String contents) {
        this.description = description;
        this.script = contents;
    }

    public String getScript() {
        return script;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
