package main;

import versions.Version;

import java.util.InvalidPropertiesFormatException;
import java.util.function.Consumer;

public class PropertiesFlagParser {
    private enum PossibleProperties {
        USER("-u", Properties::setUser),
        SERVER("-server", Properties::setDbServer),
        DB_NAME("-db", Properties::setDbName),
        PASSWORD("-p", Properties::setPassword),
        VERSION("-version", value -> {
            Properties.setVersion(new Version(value));
        }),
        INSTALL_LATEST_VERSION("-latest", value -> {
            Properties.setVersion(Version.LATEST_VERSION);
        }),
        INSTALL_SCHEMAS("-schemas", value -> {
            Properties.setSchemas(true);
        }),
        INSTALL_VIEWS("-views", value -> {
            Properties.setViews(true);
        }),
        INSTALL_STORED_PROCEDURES("-storedprocedures", value -> {
            Properties.setStoredProcedures(true);
        }),
        INSTALL_FULL("-full", value -> {
            Properties.setSchemas(true);
            Properties.setViews(true);
            Properties.setStoredProcedures(true);
        });

        PossibleProperties(String flag, Consumer<String> setter) {
            this.flag = flag;
            this.setter = setter;
        }

        private String flag;
        private Consumer<String> setter;
    }

    public static void parseProperties(String[] args) throws InvalidPropertiesFormatException {
        setPropertiesAccordingToFlags(args);
        validateConnectionPropertiesWereSet();
        validateUserAskedForSomethingToBeInstalled();
        validateUserProvidedVersionToInstall();
    }

    private static void setPropertiesAccordingToFlags(String[] args) throws InvalidPropertiesFormatException {
        for (String arg : args) {
            if (arg.contains("=")) {
                handleContainesEqualsArg(arg);
            } else handleArgWithoutEquals(arg);
        }
    }

    private static void handleArgWithoutEquals(String arg) {
        for (PossibleProperties possibleProperty : PossibleProperties.values()){
            if (arg.compareToIgnoreCase(possibleProperty.flag) == 0) {
                possibleProperty.setter.accept(arg);
            }
        }
    }

    private static void handleContainesEqualsArg(String arg) throws InvalidPropertiesFormatException {
        String[] splitArg = arg.split("=");
        if (splitArg.length == 1){
            throw new InvalidPropertiesFormatException("Argument with = sign must contain value after the sign! Received: " + arg);
        }
        if (splitArg.length > 2){
            throw new InvalidPropertiesFormatException("Argument must contain only one = sign! Received: " +arg);
        }
        for (PossibleProperties possibleProperty : PossibleProperties.values()){
            if (splitArg[0].compareToIgnoreCase(possibleProperty.flag) == 0){
                possibleProperty.setter.accept(splitArg[1]);
            }
        }
    }

    private static void validateConnectionPropertiesWereSet() throws InvalidPropertiesFormatException {
        if (Properties.getDbName() == null) {
            throw new InvalidPropertiesFormatException("Database name not provided! Make sure to use the flag: [-db=YOURDBNAME]");
        }
        if (Properties.dbServer() == null) {
            throw new InvalidPropertiesFormatException("Database server not provided! Make sure to use the flag: [-server=HOST:PORT]");
        }
        if (Properties.getUser() == null) {
            throw new InvalidPropertiesFormatException("Database user-name not provided! Make sure to use the flag [-u=YOURUSERNAME]");
        }
        if (Properties.getPassword() == null) {
            throw new InvalidPropertiesFormatException("Database password not provided! Make sure to use the flag [-p=YOURDPASSWORD]");
        }
    }

    private static void validateUserAskedForSomethingToBeInstalled() throws InvalidPropertiesFormatException {
        if (!Properties.installSchemas() && !Properties.installViews() && !Properties.installStoredProcedures()) {
            throw new InvalidPropertiesFormatException("No installation flag was provided! Use flags [-schemas], [-views] or [-storedprocedures]. Alternatively " +
                    "use [-full] to install all of them.)");
        }
    }

    private static void validateUserProvidedVersionToInstall() throws InvalidPropertiesFormatException {
        if (Properties.installUpToVersion() == null) {
            throw new InvalidPropertiesFormatException("No version to install was provided! Use flag [-latest] to install to latest " +
                    "version or flag [-version=X.Y.Z] to install up to a specific version");
        }
    }
}
