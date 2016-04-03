package keyvent.sample;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

@Value
@AllArgsConstructor
public class CommandId implements Serializable {

    private final UUID value;

    public CommandId() {
        this.value = UUID.randomUUID();
    }

    public UUID value() {
        return value;
    }

}

