package updateScript;

import org.apache.logging.log4j.Logger;

import static org.apache.logging.log4j.LogManager.getLogger;

public class UpdateScript {
    private final static Logger logger = getLogger("UpdateScript");

    String description;
    String script;

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
}
