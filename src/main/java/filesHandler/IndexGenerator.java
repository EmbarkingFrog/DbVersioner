package filesHandler;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IndexGenerator {
    private final static String rootFolder = "/sqlFiles";
    private final static String baseBackupFile = rootFolder + "/1.0.0-backup-first_backup.sql";

    public static void main(String[] args) throws MojoFailureException, MojoExecutionException {
        IndexGenerator indexGenerator = new IndexGenerator();
        indexGenerator.execute();
    }

    private void execute() throws MojoExecutionException, MojoFailureException {
        try {
            JSONObject filesIndex = new JSONObject();
            Path rootSqlFilesFolder = getRootFolder();

            filesIndex.put("base_backup", baseBackupFile);
            addToFileIndex(filesIndex, rootSqlFilesFolder, "schemas");
            addToFileIndex(filesIndex, rootSqlFilesFolder, "storedProcedures");
            addToFileIndex(filesIndex, rootSqlFilesFolder, "views");

            try {
                writeIndexFile(filesIndex, rootSqlFilesFolder);
            } catch (IOException e) {
                throw new MojoFailureException("Couldn't write index file!", e);
            }
        } catch (URISyntaxException e) {
            throw new MojoFailureException("Bad URI syntax when accessing sqlFiles folder!", e);
        } catch (IOException e) {
            throw new MojoFailureException("Couldn't access subfolder for indexing files!", e);
        }
    }

    private void writeIndexFile(JSONObject filesIndex, Path rootSqlFilesFolder) throws IOException {
        Path indexFilePath = rootSqlFilesFolder.resolve("index.json");
        if (!Files.exists(indexFilePath)) {
            Files.createFile(indexFilePath);
        }

        Files.write(indexFilePath, filesIndex.toString(2).getBytes());
    }

    private void addToFileIndex(JSONObject filesIndex, Path rootSqlFilesFolder, String subFolderName) throws IOException {
        filesIndex.put(subFolderName, getFilesInFolder(rootSqlFilesFolder.resolve(subFolderName)));
    }

    private Path getRootFolder() throws URISyntaxException {
        URL sqlFilesUrl = this.getClass().getResource(baseBackupFile);
        Path firstBackupPath = Paths.get(sqlFilesUrl.toURI());
        return firstBackupPath.getParent();
    }

    private static JSONArray getFilesInFolder(Path subFolder) throws IOException {
        JSONArray filesArray = new JSONArray();
        if (Files.exists(subFolder)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(subFolder)) {
                for (Path child : stream) {
                    filesArray.put(rootFolder + "/" + subFolder.getFileName() + "/" + child.getFileName());
                }
            }
        }
        return filesArray;
    }
}
