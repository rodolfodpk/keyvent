package keyvent.flows.commands;

import javaslang.collection.List;
import javaslang.control.Option;

public interface EventRepository<ID, UOW> {

    List<UOW> eventsAfter(ID id, Long version);
    List<UOW> eventsAfter(ID id, Long version, int limit);
    Option<Long> lastVersion(ID id);
}
