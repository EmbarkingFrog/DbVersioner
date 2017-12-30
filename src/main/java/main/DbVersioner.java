package main;

import dbHandler.ScriptDeployer;
import dbHandler.ScriptDeploymentException;
import filesHandler.FilesHandler;
import filesHandler.SchemaUpdateFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import versions.Version;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.InvalidPropertiesFormatException;
import java.util.PriorityQueue;

public class DbVersioner {
    private final static Logger logger = LogManager.getLogger("DbVersioner");

    public static void main(String[] args) {
        initializeProperties(args);
        ScriptDeployer scriptDeployer = new ScriptDeployer();
        Connection connection = null;
        try {
            connection = scriptDeployer.initializeDbConnection();
        } catch (ScriptDeploymentException e) {
            handleError("Couldn't connect and/or create requested DB! See causing exception: ", e);
        }
        FilesHandler filesHandler = null;
        try {
            filesHandler = new FilesHandler();
        } catch (IOException e) {
            handleError("Couldn't read script update files, see causing exception: ", e);
        }

        Version dbStartingVersion = null;
        Version dbCurrentVersion = null;
        try {
            dbStartingVersion = scriptDeployer.getDbCurrentVersion(connection);
            dbCurrentVersion = dbStartingVersion;
        } catch (SQLException e) {
            handleError("Could not verify current DB version! Does it have a versions.versions table? See causing exception: ", e);
        }

        try {
            scriptDeployer.cleanViewsAndStoredProcedures(connection);
        } catch (SQLException e) {
            handleError("Could not clean views and stored procedures! See causing exception: ", e);
        }

        if (Properties.installSchemas()) {
            try {
                PriorityQueue<SchemaUpdateFile> updateFiles = filesHandler.getSchemaUpdateFiles();
                Version mostRecentScriptVersionAvailable = findMostRecentVersionAvailable(updateFiles);

                if (dbStartingVersion.compareTo(Properties.installUpToVersion()) >= 0) {
                    logger.info("Skipping schema installation since DB version [{}] is already equal or greater to requested version [{}]",
                            dbStartingVersion, Properties.installUpToVersion());

                } else if (dbStartingVersion.compareTo(mostRecentScriptVersionAvailable) >= 0) {
                    logger.info("Skipping schema installation since DB version [{}] is already equal or greater to most recent script file [{}]",
                            dbStartingVersion, mostRecentScriptVersionAvailable);

                } else {
                    logger.info("Installing schemas, up to version [{}]", Properties.installUpToVersion());
                    try {
                        dbCurrentVersion = scriptDeployer.updateSchemasUpToVersion(connection, updateFiles, Properties.installUpToVersion());
                        logger.info("DB schemas were successfully updated from version [{}] to version [{}]!", dbStartingVersion, dbCurrentVersion);
                    } catch (SQLException e) {
                        handleError("Could not install schemas! See causing exception: ", e);
                    }
                }
            } catch (IOException e) {
                handleError("Error while reading update script files! See causing exception: ", e);
            }
        }

        if (Properties.installViews()) {
            logger.info("Installing views...");
            try {
                scriptDeployer.executeScripts(connection, filesHandler.getViews());
            } catch (SQLException | IOException e) {
                handleError("Could not install views! See causing exception: ", e);
            }
            logger.info("Views were successfully installed!");
        }

        if (Properties.installStoredProcedures()) {
            logger.info("Installing stored procedures...");
            try {
                scriptDeployer.executeScripts(connection, filesHandler.getStoredProcedures());
            } catch (SQLException | IOException e) {
                handleError("Could not install stored procedures! See causing exception: ", e);
            }
            logger.info("Stored procedures were successfully installed!");
        }

        logger.info("Installation successfully completed! The DB is now version: [{}]", dbCurrentVersion);
    }

    private static Version findMostRecentVersionAvailable(PriorityQueue<SchemaUpdateFile> updateFiles) {
        PriorityQueue<SchemaUpdateFile> queueCopy = new PriorityQueue<>(updateFiles);

        Version mostRecent = null;
        while (queueCopy.peek() != null) {
            mostRecent = queueCopy.poll().getVersion();
        }
        if (mostRecent == null) {
            handleError("No update scripts available! Check update scripts folder for correctly versioned files!");
        }
        return mostRecent;
    }

    private static void initializeProperties(String[] args) {
        try {
            PropertiesFlagParser.parseProperties(args);
        } catch (InvalidPropertiesFormatException e) {
            handleError("Invalid flags supplied at startup! See causing exception: ", e);
        }
    }

    private static void handleError(String message, Throwable e) {
        logger.error(message, e);
        System.exit(-1);
    }

    private static void handleError(String message) {
        logger.error(message);
        System.exit(-1);
    }
}
