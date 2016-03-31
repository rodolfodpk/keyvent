package keyvent.data;

import java.io.Serializable;

public class Snapshot<T> implements Serializable {

    private final T instance;
    private final Version version;

    public Snapshot(T instance, Version version) {
        this.instance = instance;
        this.version = version;
    }

    public T instance() {
        return instance;
    }

    public Version version() {
        return version;
    }

    public Version nextVersion() {
        return new Version(version.value()+1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Snapshot)) return false;
        Snapshot<?> snapshot = (Snapshot<?>) o;
        if (!instance().equals(snapshot.instance())) return false;
        return version().equals(snapshot.version());
    }

    @Override
    public int hashCode() {
        int result = instance().hashCode();
        result = 31 * result + version().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Snapshot{" +
                "instance=" + instance +
                ", version=" + version +
                '}';
    }
}
