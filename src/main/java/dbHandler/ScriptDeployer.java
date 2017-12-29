package dbHandler;

import filesHandler.FilesHandler;
import filesHandler.SchemaUpdateFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import main.Properties;
import updateScript.SchemaUpdateScript;
import updateScript.UpdateScript;
import versions.Version;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.PriorityQueue;
import java.util.Set;

import static dbHandler.DatabaseConnector.getConnection;

public class ScriptDeployer {
    private final static Logger logger = LogManager.getLogger("ScriptDeployer");
    private static final String DATABASE_DOESNT_EXIST_CODE = "3D000";

    public Connection initializeDbConnection() throws ScriptDeploymentException {
        try {
            return getConnection();
        } catch (SQLException error) {
            if (error.getSQLState().equals(DATABASE_DOESNT_EXIST_CODE)) {
                logger.warn("Databse [{}] doesn't exist. ScriptDeployer will now create it.", Properties.getDbName());
                Connection connection = createDb();
                try {
                    logger.info("Installing base backup to the newly created DB");
                    installBaseBackup(connection);
                } catch (Exception e) {
                    logger.error("Could not restore DB from backup! Deleting the database. See causing exception: ", e);
                    try {
                        connection.close();
                        deleteDB();
                    } catch (SQLException e1) {
                        logger.error("Could not delete the failed DB that was created: [{}]. You will need to" +
                                " manually delete it! See causing exception: ", Properties.getDbName(), e1);
                        throw new ScriptDeploymentException(e1);
                    }
                }
                return connection;
            } else {
                logger.error("Unexpected SQL exception: ", error);
                throw new ScriptDeploymentException(error);
            }
        }
    }

    public void installBaseBackup(Connection connection) throws SQLException, IOException {
        try {
            SchemaUpdateFile baseBackupFile = new FilesHandler().getBaseBackupFile();
            installVersionScript(connection, baseBackupFile);
        } catch (SQLException e) {
            logger.error("Could not install base backup!");
            throw e;
        } catch (FileNotFoundException e) {
            logger.error("Could not find backup file!");
            throw e;
        }
    }

    public Version updateSchemasUpToVersion(Connection connection, PriorityQueue<? extends SchemaUpdateScript> schemaUpdateScripts, Version installUpToVersion)
            throws SQLException, IOException {
        Version dbStartingVersion = getDbCurrentVersion(connection);
        Version latestInstalled = dbStartingVersion;
        logger.info("DB current version is [{}]. Updating DB to version [{}]", dbStartingVersion, installUpToVersion);
        for (SchemaUpdateScript currentScript : schemaUpdateScripts) {
            if (currentScript.getVersion().compareTo(latestInstalled) > 0) {
                if (currentScript.getVersion().compareTo(installUpToVersion) <= 0) {
                    installVersionScript(connection, currentScript);
                    latestInstalled = currentScript.getVersion();
                } else {
                    logger.info("Script [{}] is skipped since install up to [{}] was requested", currentScript.getDescription(), installUpToVersion);
                }
            } else {
                logger.info("Script [{}] is skipped because DB version [{}] is equal or greater", currentScript.getDescription(), latestInstalled);
            }
        }

        logger.info("DB was successfully updated from version [{}] to version {}!", dbStartingVersion, latestInstalled);

        return latestInstalled;
    }

    public void executeScripts(Connection connection, Set<? extends UpdateScript> scripts) throws IOException, SQLException {
        for (UpdateScript script : scripts) {
            executeScript(connection, script);
        }
    }

    public Version getDbCurrentVersion(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String CURRENT_VERSION_QUERY = "SELECT version FROM versions.versions ORDER BY install_date DESC LIMIT 1";
            ResultSet resultSet = statement.executeQuery(CURRENT_VERSION_QUERY);
            resultSet.next();
            String version = resultSet.getString("version");
            return new Version(version);
        } catch (SQLException e) {
            logger.error("Couldn't extract database current version! See causing exception: ", e);
            throw e;
        }
    }

    private void deleteDB() {
        String databaseToDelete = Properties.getDbName();
        logger.info("Deleting database [{}]", databaseToDelete);
        try (Connection adminConnection = getConnectionToPostgresSchema();
             Statement statement = adminConnection.createStatement()) {
            statement.executeUpdate("DROP DATABASE " + databaseToDelete);
            logger.info("Successfully deleted the new databse: [{}]", databaseToDelete);
        } catch (SQLException e) {
            logger.error("Could not connect to postgres database to delete {}! You might need to manually delete it. See causing exception: ",
                    databaseToDelete, e);
            System.exit(-1);
        }
    }

    private Connection createDb() {
        String databaseToCreate = Properties.getDbName();
        logger.info("Creating database [{}]", databaseToCreate);
        try (Connection adminConnection = getConnectionToPostgresSchema();
             Statement statement = adminConnection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE " + databaseToCreate);
            logger.info("Successfully created new databse: [{}]. Connecting to it...", databaseToCreate);
            return getConnection();
        } catch (SQLException e) {
            logger.error("Could not connect to postgres database to create [{}]. See causing exception: ",
                    databaseToCreate, e);
            System.exit(-1);
        }
        throw new RuntimeException("Code shouldn't reach here!");
    }

    private void installVersionScript(Connection connection, SchemaUpdateScript schemaUpdateScript) throws IOException, SQLException {
        Statement statement = connection.createStatement();
        statement.execute(schemaUpdateScript.getScript());
        String INSERT_VERSION_TO_VERSION_TABLE_QUERY = String.format(
                "INSERT INTO versions.versions(version, install_date, creator, description) VALUES ('%s', now(), 'AutoDeployer', '%s')",
                schemaUpdateScript.getVersion(), schemaUpdateScript.getDescription());
        statement.executeUpdate(INSERT_VERSION_TO_VERSION_TABLE_QUERY);
        statement.close();
    }

    private void executeScript(Connection connection, UpdateScript script) throws IOException, SQLException {
        logger.info("Installing script: [{}]", script.getDescription());
        Statement statement = connection.createStatement();
        statement.execute(script.getScript());
        statement.close();
    }

    private Connection getConnectionToPostgresSchema() throws SQLException {
        return getConnection(Properties.dbServer(),
                "postgres",
                Properties.getUser(),
                Properties.getPassword());
    }

    public void cleanViewsAndStoredProcedures(Connection connection) throws SQLException {
        cleanViews(connection);
        cleanStoredProcedures(connection);
    }

    private void cleanViews(Connection connection) throws SQLException {
        logger.info("Cleaning all views:");
        Statement statement = connection.createStatement();
        String GET_ALL_DROP_VIEWS_QUERY = "  SELECT 'DROP VIEW ' || table_schema || '.' || table_name || ';' AS views\n" +
                "  FROM information_schema.views\n" +
                " WHERE table_schema NOT IN ('pg_catalog', 'information_schema')\n" +
                "   AND table_name !~ '^pg_';";
        ResultSet resultSet = statement.executeQuery(GET_ALL_DROP_VIEWS_QUERY);
        if (!resultSet.isBeforeFirst()) {
            logger.info("No views to clean!");
        }
        while (!resultSet.isClosed() && resultSet.next()) {
            String NEXT_DROP_VIEW_QUERY = resultSet.getString("views");
            logger.info("Executing: {}", NEXT_DROP_VIEW_QUERY);
            statement.execute(NEXT_DROP_VIEW_QUERY);
        }
    }

    private void cleanStoredProcedures(Connection connection) throws SQLException {
        logger.info("Cleaning all stored procedures:");
        Statement statement = connection.createStatement();
        String GET_ALL_DROP_STORED_PROCEDURES_QUERY = "SELECT 'DROP FUNCTION ' || n.nspname || '.' || proname \n" +
                "       || '(' || oidvectortypes(proargtypes) || ');' AS drop_procedure_query\n" +
                "FROM pg_catalog.pg_proc p\n" +
                "     LEFT JOIN pg_catalog.pg_namespace n ON n.oid = p.pronamespace\n" +
                "     LEFT JOIN pg_depend d ON d.objid = p.oid AND d.deptype = 'e'\n" +
                "WHERE \n" +
                "      n.nspname <> 'pg_catalog'\n" +
                "      AND n.nspname <> 'information_schema'\n" +
                "      AND d.objid IS NULL";
        ResultSet resultSet = statement.executeQuery(GET_ALL_DROP_STORED_PROCEDURES_QUERY);
        if (!resultSet.isBeforeFirst()) {
            logger.info("No procedures to clean!");
        }
        while (!resultSet.isClosed() && resultSet.next()) {
            String NEXT_DROP_PROCEDURE_QUERY = resultSet.getString("drop_procedure_query");
            logger.info("Executing: {}", NEXT_DROP_PROCEDURE_QUERY);
            statement.execute(NEXT_DROP_PROCEDURE_QUERY);
        }
    }
}
