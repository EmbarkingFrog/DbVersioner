package versions;

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
        if (unparsedVersion.compareToIgnoreCase("latest") == 0){
            int[] latestVersion = new int[3];
            latestVersion[0] = Integer.MAX_VALUE;
            latestVersion[1] = Integer.MAX_VALUE;
            latestVersion[2] = Integer.MAX_VALUE;
            return latestVersion;
        }
        String[] splitVersion = unparsedVersion.split("\\.");
        if (splitVersion.length != VERSION_DIVISIONS) {
            throw new IllegalArgumentException("Illegal Version numbers received! Must be in the form of [0.0.0], or [latest]. Received: "
                    + unparsedVersion);
        }
        int[] parsedVersion = new int[VERSION_DIVISIONS];
        for (int index = 0; index < splitVersion.length; index++) {
            try {
                int versionPart = Integer.parseInt(splitVersion[index]);
                if (versionPart < 0) {
                    throw new IllegalArgumentException("Version must contain positive numbers only. (Good example: 1.1.1, bad example: 1.-1.2." +
                            "Received: " + versionPart);
                }
                parsedVersion[index] = versionPart;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Version contains something that's not a number (" + splitVersion[index] + ")! Received: " + unparsedVersion, e);
            }
        }
        return parsedVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Version version = (Version) o;

        if (major != version.major) return false;
        if (minor != version.minor) return false;
        return revision == version.revision;
    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + revision;
        return result;
    }

    @Override
    public String toString() {
        if (major == minor && minor == revision && revision == Integer.MAX_VALUE) {
            return "latest";
        } else return major + "." + minor + "." + revision;
    }
}
