package keyvent.core;

import javaslang.collection.List;
import javaslang.control.Option;
import keyvent.core.data.Version;

public interface EventRepository<ID, UOW> {

    List<UOW> eventsAfter(ID id, Version version);
    List<UOW> eventsAfter(ID id, Version version, int limit);
    Option<Version> lastVersion(ID id);
}
