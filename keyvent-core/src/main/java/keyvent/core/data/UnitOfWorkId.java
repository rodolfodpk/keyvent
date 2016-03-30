package keyvent.core.data;

import java.io.Serializable;
import java.util.UUID;

public class UnitOfWorkId implements Serializable {

    private final UUID value;

    public UnitOfWorkId() {
        this.value = UUID.randomUUID();
    }

    public UnitOfWorkId(UUID value) {
        this.value = value;
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnitOfWorkId)) return false;
        UnitOfWorkId that = (UnitOfWorkId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "UnitOfWorkId{" +
                "value=" + value +
                '}';
    }
}
