package keyvent.flows.events;

import javaslang.collection.List;

public interface EventRepository<ID, UOW> {

    List<UOW> eventsAfter(Long globalEventSequence, int limit);

}
