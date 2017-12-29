package updateScript;

public class UpdateScriptUtils {

    public static boolean isCommentLine(String line) {
        return line.startsWith("--");
    }
}
