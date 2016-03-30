package keyvent.core.data;

import java.io.Serializable;

public class Version implements Serializable {

    private final Long value;

    public Version(Long value) {
        this.value = value;
    }

    public Long value() {
        return value;
    }

    public static Version firstVersion() {
        return new Version(1L);
    }

    public static Version nextVersionOf(Version v) {
        return new Version(v.value()+1L);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Version)) return false;
        Version version = (Version) o;
        return value().equals(version.value());
    }

    @Override
    public int hashCode() {
        return value().hashCode();
    }

    @Override
    public String toString() {
        return "Version{" +
                "value=" + value +
                '}';
    }
}
