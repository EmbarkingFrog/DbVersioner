package filesHandler;

import org.apache.logging.log4j.Logger;
import versions.Version;

import java.io.*;
import java.nio.file.Path;
import java.util.function.Predicate;

import static org.apache.logging.log4j.LogManager.getLogger;
import static updateScript.UpdateScriptUtils.isCommentLine;

class FileReadUtils {
    private final static Logger logger = getLogger("FileReadUtils");

    private FileReadUtils() {
    }

    static String readResource(Path path) throws IOException {
        return readResource(path, s -> true);
    }

    static String readResource(Path path, Predicate<String> filter) throws IOException {
        FileReadUtils utils = new FileReadUtils();
        return utils.nonStaticReadResource(path, filter);
    }

    static String readSqlFile(Path path) throws IOException {
        return readResource(path, s -> !isCommentLine(s));
    }

    private String nonStaticReadResource(Path path, Predicate<String> filter) throws IOException {
        StringBuilder result = new StringBuilder();

        InputStream in = getClass().getResourceAsStream(path.toString().replace('\\','/'));
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;

        while ((line = reader.readLine()) != null) {
            if (filter.test(line)) {
                result.append(line).append("\n");
            }
        }

        return result.toString();
    }

    static String removeSqlExtension(Path path) {
        String pathString = path.toString();
        logger.debug("Formatting SchemaUpdateFile {} to be without extension", pathString);
        String sqlExtension = pathString.substring(pathString.length() - 4);
        if (!sqlExtension.toLowerCase().equals(".sql")) {
            throw new IllegalArgumentException("SchemaUpdateFile file path must end in .sql!");
        }
        String fileNameWithoutExtension = pathString.substring(0, pathString.length() - 4);
        logger.debug("File [{}] without extension is [{}]", pathString, fileNameWithoutExtension);
        return fileNameWithoutExtension;
    }

    static Version parseVersion(Path path) {
        return new Version(getHyphenatedStringPart(removeSqlExtension(path.getFileName()), 0));
    }

    static String parseSchema(Path path) {
        return getHyphenatedStringPart(removeSqlExtension(path.getFileName()), 1);
    }

    static String parseDescription(Path path) {
        return getHyphenatedStringPart(removeSqlExtension(path.getFileName()), 2);
    }

    private static String getHyphenatedStringPart(String string, int partIndex) {
        String[] stringParts = string.split("-");
        return stringParts[partIndex];
    }
}
