package dbHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import main.Properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private final static Logger logger = LogManager.getLogger("DatabaseConnector");

    public static Connection getConnection(String url, String database, String user, String password) throws SQLException {
        logger.debug("Initiating connection to DB [{}]...", database);
        String formattedUrl = String.format("jdbc:postgresql://%s/%s", url, database);
        Connection connection = DriverManager.getConnection(formattedUrl, user, password);
        logger.info("Successfully connected to [{}] with user [{}]", formattedUrl, user);
        return connection;
    }

    public static Connection getConnection() throws SQLException {
        return getConnection(Properties.dbServer(),
                Properties.getDbName(),
                Properties.getUser(),
                Properties.getPassword());
    }
}
