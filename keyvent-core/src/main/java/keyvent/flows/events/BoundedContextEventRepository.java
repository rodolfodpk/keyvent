package keyvent.flows.events;

import javaslang.collection.List;
import keyvent.data.GlobalEventSeq;

public interface BoundedContextEventRepository<ID, UOW> {

    List<UOW> eventsAfter(GlobalEventSeq globalEventSequence, int limit);

}
