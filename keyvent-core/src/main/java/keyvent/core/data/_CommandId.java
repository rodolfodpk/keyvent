package keyvent.core.data;

import keyvent.core.utils.Wrapped;
import keyvent.core.utils.Wrapper;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
@Wrapped
public abstract class _CommandId extends Wrapper<UUID> {}

