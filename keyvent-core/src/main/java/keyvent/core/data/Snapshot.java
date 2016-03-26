package keyvent.core.data;

public class Snapshot<T> {

    private final T instance;
    private final Long version;

    public Snapshot(T instance, Long version) {
        this.instance = instance;
        this.version = version;
    }

    T instance() {
        return null;
    }

    Long version() {
        return null;
    }

    public T getInstance() {
        return instance;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Snapshot)) return false;

        Snapshot<?> snapshot = (Snapshot<?>) o;

        if (!getInstance().equals(snapshot.getInstance())) return false;
        return getVersion().equals(snapshot.getVersion());

    }

    @Override
    public int hashCode() {
        int result = getInstance().hashCode();
        result = 31 * result + getVersion().hashCode();
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
