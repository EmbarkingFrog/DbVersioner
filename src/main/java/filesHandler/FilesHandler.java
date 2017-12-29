package filesHandler;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class FilesHandler {
    private final JSONObject filesIndex;

    private final String baseBackupFile;

    private final List<String> schemasFiles;
    private final List<String> viewsFiles;
    private final List<String> storedProceduresFiles;

    public FilesHandler() throws IOException {
        filesIndex = readIndex();
        baseBackupFile = filesIndex.getString("base_backup");
        schemasFiles = initFilesList("schemas");
        viewsFiles = initFilesList("views");
        storedProceduresFiles = initFilesList("storedProcedures");
    }

    private List<String> initFilesList(String subfolder) {
        List<String> fileList = new ArrayList<>();
        filesIndex.getJSONArray(subfolder).forEach(o -> fileList.add(o.toString()));
        return fileList;
    }

    private JSONObject readIndex() throws IOException {
        return new JSONObject(FileReadUtils.readResource(Paths.get("/sqlFiles/index.json")));
    }

    public PriorityQueue<SchemaUpdateFile> getSchemaUpdateFiles() throws IOException {
        PriorityQueue<SchemaUpdateFile> schemaUpdateFiles = new PriorityQueue<>();
        schemasFiles.forEach(s -> {
            try {
                schemaUpdateFiles.add(new SchemaUpdateFile(Paths.get(s)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return schemaUpdateFiles;
    }

    public Set<UpdateFile> getViews() throws IOException {
        Set<UpdateFile> files = new HashSet<>();
        viewsFiles.forEach(s -> {
            try {
                files.add(new UpdateFile(Paths.get(s)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return files;
    }

    public Set<UpdateFile> getStoredProcedures() throws IOException {
        Set<UpdateFile> files = new HashSet<>();
        storedProceduresFiles.forEach(s -> {
            try {
                files.add(new UpdateFile(Paths.get(s)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return files;
    }

    public SchemaUpdateFile getBaseBackupFile() throws IOException {
        return new SchemaUpdateFile(Paths.get(baseBackupFile));
    }
}
