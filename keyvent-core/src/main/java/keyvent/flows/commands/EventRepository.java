package keyvent.flows.commands;

import javaslang.collection.List;
import keyvent.data.Version;

public interface EventRepository<ID, UOW> {

    List<UOW> eventsAfter(ID id, Version version, int limit);

}
