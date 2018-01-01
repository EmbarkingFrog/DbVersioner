package filesHandler;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.json.JSONArray;
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
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import static filesHandler.FileReadUtils.parseVersion;

public class IndexGenerator {
    private final static String rootFolder = "/sqlFiles";
    private final static String baseBackupFile = rootFolder + "/0-backup-first_backup.sql";

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
        List<Path> schemaPaths = getFilesInFolder(rootSqlFilesFolder.resolve("schemas"));

        TreeSet<VersionedPath> sortedSchemas = new TreeSet<>();
        for (Path schemaPath : schemaPaths) {
            VersionedPath schemaVersionedPath = new VersionedPath(schemaPath);
            if (sortedSchemas.contains(schemaVersionedPath)) {
                throw new IllegalArgumentException(String.format("Can't add [%s], a schema script with this version already exists!",
                        schemaVersionedPath));
            }
            sortedSchemas.add(schemaVersionedPath);
        }

        validateSchemasHaveNoHole(sortedSchemas);

        JSONArray schemasJsonArray = new JSONArray();
        sortedSchemas.forEach(s -> schemasJsonArray.put(s.path));

        filesIndex.put("schemas", schemasJsonArray);
    }

    private void validateSchemasHaveNoHole(TreeSet<VersionedPath> sortedSchemas) {
        Iterator<VersionedPath> iterator = sortedSchemas.iterator();
        if (iterator.hasNext()) {
            VersionedPath versionedPath = iterator.next();
            VersionedPath nextVersionedPath;
            while (iterator.hasNext()) {
                nextVersionedPath = iterator.next();
                if (!versionedPath.version.isConsecutive(nextVersionedPath.version)) {
                    throw new IllegalArgumentException(String.format("There's a gap in the versions of the sql update" +
                                    " script, between the files [%s] and [%s]",
                            versionedPath.path.toString(), nextVersionedPath.path.toString()));
                } else {
                    versionedPath = nextVersionedPath;
                }
            }
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

    private class VersionedPath implements Comparable<VersionedPath> {
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            VersionedPath that = (VersionedPath) o;

            return version.equals(that.version);
        }

        @Override
        public int hashCode() {
            return version.hashCode();
        }

        @Override
        public String toString() {
            return this.path.toString();
        }
    }
}
