package keyvent.sample;

import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

@Value
public class UnitOfWorkId implements Serializable {
    private final UUID value;
    public UnitOfWorkId() {
        this.value = UUID.randomUUID();
    }
    public UUID value() {
        return value;
    }
}
