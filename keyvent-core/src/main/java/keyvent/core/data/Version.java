package keyvent.core.data;

import keyvent.core.utils.Wrapped;
import keyvent.core.utils.Wrapper;
import org.immutables.value.Value;

@Value.Immutable
@Wrapped
public abstract class Version extends Wrapper<Long> {

    public static Version of(Long version) {
        return VersionVal.of(version);
    }

    public static Version firstVersion() {
        return VersionVal.of(1L);
    }

    public static Version nextVersionOf(Version v) {
        return VersionVal.of(v.value()+1);
    }

}
