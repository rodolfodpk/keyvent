package keyvent.core;

import keyvent.core.data.Version;

import java.util.List;
import java.util.Optional;

public interface EventRepository<ID, UOW> {

    List<UOW> eventsAfter(ID id, Version version);
    List<UOW> eventsAfter(ID id, Version version, int limit);
    Optional<Version> lastVersion(ID id);
}
