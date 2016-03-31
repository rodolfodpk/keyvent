package keyvent.sample;

import java.io.Serializable;
import java.util.UUID;

public class CommandId implements Serializable {

    private final UUID value;

    public CommandId() {
        this.value = UUID.randomUUID();
    }
    public CommandId(UUID value) {
        this.value = value;
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandId)) return false;
        CommandId that = (CommandId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "CommandId{" +
                "value=" + value +
                '}';
    }
}

