package keyvent.core.data;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
@DefaultStyle
public interface AbstractUnitOfWorkId {
    UUID uuid();
}
