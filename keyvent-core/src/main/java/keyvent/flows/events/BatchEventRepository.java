package keyvent.flows.events;

import javaslang.collection.List;
import keyvent.flows.GlobalEventSeq;

public interface BatchEventRepository<ID, UOW> {

    List<UOW> eventsAfter(GlobalEventSeq globalEventSequence, int limit);

}
