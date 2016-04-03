package keyvent.sample;

import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

@Value
public class AggregateRootId implements Serializable {
    private final UUID value;
    public AggregateRootId() {
        this.value = UUID.randomUUID();
    }
    public UUID value() {
        return value;
    }
}
