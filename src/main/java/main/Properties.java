package main;

import versions.Version;

public class Properties {
    private boolean updateSchemas;
    private boolean installViews;
    private boolean installStoredProcedures;
    private Version installUpToVersion;
    private String dbServer;
    private String user;
    private String password;
    private String dbName;

    private static Properties INSTANCE;

    private Properties() {
    }

    private static Properties getProperties() {
        if (INSTANCE == null) {
            INSTANCE = new Properties();
        }
        return INSTANCE;
    }

    public static boolean installViews() {
        return getProperties().installViews;
    }

    public static boolean installStoredProcedures() {
        return getProperties().installStoredProcedures;
    }

    public static boolean installSchemas() {
        return getProperties().updateSchemas;
    }

    public static Version installUpToVersion() {
        return getProperties().installUpToVersion;
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
        getProperties().updateSchemas = schemas;
    }

    public static void setViews(boolean views) {
        getProperties().installViews = views;
    }

    public static void setStoredProcedures(boolean storedProcedures) {
        getProperties().installStoredProcedures = storedProcedures;
    }

    public static void setVersion(Version version) {
        getProperties().installUpToVersion = version;
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
