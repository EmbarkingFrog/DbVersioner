package versions;

public class Version implements Comparable<Version> {
    private final long versionNumber;

    public static Version LATEST_VERSION = new Version(Long.MAX_VALUE);

    public Version(String unparsedVersion) {
        if (unparsedVersion.equalsIgnoreCase("latest")) {
            this.versionNumber = Long.MAX_VALUE;
        } else {
            this.versionNumber = Long.parseLong(unparsedVersion);
        }
    }

    public Version(long version) {
        this.versionNumber = version;
    }

    public boolean isConsecutive(Version version) {
        return (Math.abs(this.versionNumber - version.versionNumber) == 1);
    }

    @Override
    public int compareTo(Version version) {
        return ((Long) this.versionNumber).compareTo(version.versionNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Version otherVersion = (Version) o;

        return this.versionNumber == otherVersion.versionNumber;
    }

    @Override
    public int hashCode() {
        return (int) (versionNumber ^ (versionNumber >>> 32));
    }

    @Override
    public String toString() {
        if (versionNumber == Long.MAX_VALUE) {
            return "latest";
        } else return "" + versionNumber;
    }
}
