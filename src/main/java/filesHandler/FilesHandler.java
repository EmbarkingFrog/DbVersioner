package filesHandler;

import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static org.apache.logging.log4j.LogManager.*;

public class FilesHandler {
    private final static Logger logger = getLogger("filesHandler");

    private static Path schemasRoot;
    private static Path viewsRoot;
    private static Path storedProceduresRoot;

    public FilesHandler() {
        schemasRoot = getResourceFolder().resolve("schemas");
        viewsRoot = getResourceFolder().resolve("views");
        storedProceduresRoot = getResourceFolder().resolve("storedProcedures");
    }

    public PriorityQueue<SchemaUpdateFile> getSchemaUpdateFiles() throws IOException {
        PriorityQueue<SchemaUpdateFile> schemaUpdateFiles = new PriorityQueue<>();
        Files.walkFileTree(schemasRoot, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().toLowerCase().endsWith(".sql")){
                    schemaUpdateFiles.add(new SchemaUpdateFile(file));
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
        return schemaUpdateFiles;
    }

    public Set<UpdateFile> getViews() throws IOException {
        return getUpdateFiles(viewsRoot);
    }

    public Set<UpdateFile> getStoredProcedures() throws IOException {
        return getUpdateFiles(storedProceduresRoot);
    }

    private Set<UpdateFile> getUpdateFiles(Path path) throws IOException{
        Set<UpdateFile> files = new HashSet<>();
        Files.walkFileTree(path, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().toLowerCase().endsWith(".sql")){
                    files.add(new UpdateFile(file));
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
        return files;
    }

    public SchemaUpdateFile getBaseBackupFile(){
        Path sqlFolder = getResourceFolder();
        Path baseBackupFile = sqlFolder.resolve("sqlFiles/1.0.0-backup-first_backup.sql");
        return new SchemaUpdateFile(baseBackupFile);
    }

    private Path getResourceFolder(){
        Path currentDir = Paths.get(".");
        return currentDir.toAbsolutePath().getParent().resolve("sqlFiles");

    }
}
