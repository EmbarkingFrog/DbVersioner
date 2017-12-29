package versions;

import java.util.Arrays;

public class Version implements Comparable<Version> {
    private final int major;
    private final int minor;
    private final int revision;

    private final static int VERSION_DIVISIONS = 3;

    public static Version LATEST_VERSION = new Version(Integer.MAX_VALUE + "." + Integer.MAX_VALUE + "." + Integer.MAX_VALUE);

    public Version(String unparsedVersion) {
        int[] parsedVersion = parseVersion(unparsedVersion);
        this.major = parsedVersion[0];
        this.minor = parsedVersion[1];
        this.revision = parsedVersion[2];
    }

    @Override
    public int compareTo(Version version) {
        if (this.major > version.major) return 1;
        else if (this.major < version.major) return -1;
        else if (this.minor > version.minor) return 1;
        else if (this.minor < version.minor) return -1;
        else return Integer.compare(this.revision, version.revision);
    }

    private int[] parseVersion(String unparsedVersion) {
        String[] splitVersion = unparsedVersion.split("\\.");
        if (splitVersion.length != VERSION_DIVISIONS) {
            throw new IllegalArgumentException("Illegal Version flag received! Must be in the form of 0.0.0. " +
                    "Alternatively, use -latest to install to latest version. Received: " + unparsedVersion);
        }
        int[] parsedVersion = new int[VERSION_DIVISIONS];
        for (int index = 0; index < splitVersion.length; index++) {
            try {
                parsedVersion[index] = Integer.parseInt(splitVersion[index]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Version contains something that's not a number (" + splitVersion[index] + ")! Received: " + unparsedVersion, e);
            }
        }
        return parsedVersion;
    }

    @Override
    public String toString() {
        if (major == minor && minor == revision && revision == Integer.MAX_VALUE) {
            return "latest";
        }
        return major + "." + minor + "." + revision;
    }
}
