package filesHandler;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.json.JSONObject;
import versions.Version;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static filesHandler.FileReadUtils.parseVersion;

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
            addSortedSchemasToIndex(filesIndex, rootSqlFilesFolder);
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

    private void addSortedSchemasToIndex(JSONObject filesIndex, Path rootSqlFilesFolder) throws IOException {
        class VersionedPath implements Comparable<VersionedPath> {
            Path path;
            Version version;

            VersionedPath(Path p) {
                this.path = p;
                version = parseVersion(p);
            }


            @Override
            public int compareTo(VersionedPath otherVersionedPath) {
                return this.version.compareTo(otherVersionedPath.version);
            }

            @Override
            public String toString() {
                return this.path.toString();
            }
        }
        List<Path> schemaPaths = getFilesInFolder(rootSqlFilesFolder.resolve("schemas"));
        TreeSet<VersionedPath> sortedSchemas = new TreeSet<>();
        for (Path schemaPath : schemaPaths) {
            VersionedPath schemaVersionedPath = new VersionedPath(schemaPath);
            for (VersionedPath schemaVersionedPathInTree : sortedSchemas) {
                if (schemaVersionedPath.compareTo(schemaVersionedPathInTree) == 0) {
                    throw new IllegalArgumentException(String.format("There are two schema update scripts with the same version: [%s], [%s]",
                            schemaVersionedPath, schemaVersionedPathInTree));
                }
            }
            sortedSchemas.add(schemaVersionedPath);
        }
        filesIndex.put("schemas", sortedSchemas);
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

    private static List<Path> getFilesInFolder(Path subFolder) throws IOException {
        List<Path> filesInFolder = new ArrayList<>();
        if (Files.exists(subFolder)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(subFolder)) {
                for (Path child : stream) {
                    filesInFolder.add(Paths.get(rootFolder + "/" + subFolder.getFileName() + "/" + child.getFileName()));
                }
            }
        }
        return filesInFolder;
    }
}
