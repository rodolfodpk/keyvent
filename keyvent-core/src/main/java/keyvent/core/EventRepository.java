package keyvent.core;

import java.util.List;

public interface EventRepository<ID, UOW> {

    List<UOW> eventsAfter(ID id, Long version);
    List<UOW> eventsAfter(ID id, Long version, int limit);
    Long lastVersion(ID id);
}
