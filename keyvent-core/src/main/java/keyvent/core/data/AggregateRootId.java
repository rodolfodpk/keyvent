package keyvent.core.data;

import java.io.Serializable;
import java.util.UUID;

public class AggregateRootId implements Serializable {

    private final UUID value;

    public AggregateRootId() {
        this.value = UUID.randomUUID();
    }

    public AggregateRootId(UUID value) {
        this.value = value;
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AggregateRootId)) return false;
        AggregateRootId that = (AggregateRootId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "AggregateRootId{" +
                "value=" + value +
                '}';
    }
}
