package main;

import versions.Version;

public class Properties {
    private boolean schemas;
    private boolean views;
    private boolean storedProcedures;
    private Version version;
    private String dbServer;
    private String user;
    private String password;
    private String dbName;

    public static Properties INSTANCE;

    private static Properties getProperties() {
        if (INSTANCE == null) {
            INSTANCE = new Properties();
        }
        return INSTANCE;
    }

    public static boolean installViews() {
        return getProperties().views;
    }

    public static boolean installStoredProcedures() {
        return getProperties().storedProcedures;
    }

    public static boolean installSchemas() {
        return getProperties().schemas;
    }

    public static Version installUpToVersion() {
        return getProperties().version;
    }

    public static String dbServer() {
        return getProperties().dbServer;
    }

    public static String getUser() {
        return getProperties().user;
    }

    public static String getPassword() {
        return getProperties().password;
    }

    public static String getDbName() {
        return getProperties().dbName;
    }

    public static void setSchemas(boolean schemas) {
        getProperties().schemas = schemas;
    }

    public static void setViews(boolean views) {
        getProperties().views = views;
    }

    public static void setStoredProcedures(boolean storedProcedures) {
        getProperties().storedProcedures = storedProcedures;
    }

    public static void setVersion(Version version) {
        getProperties().version = version;
    }

    public static void setDbServer(String dbServer) {
        getProperties().dbServer = dbServer;
    }

    public static void setUser(String user) {
        getProperties().user = user;
    }

    public static void setPassword(String password) {
        getProperties().password = password;
    }

    public static void setDbName(String dbName) {
        getProperties().dbName = dbName;
    }
}
