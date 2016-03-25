package keyvent.core.test;

import keyvent.core.EventRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;


public class SimpleEventRepository<ID, UOW> implements EventRepository<ID, UOW> {

    final Function<UOW, Long> versionExtractor;
    final Map<ID, List<UOW>> map;

    public SimpleEventRepository(Function<UOW, Long> versionExtractor, Map<ID, List<UOW>> map) {
        this.versionExtractor = versionExtractor;
        this.map = map;
    }

    @Override
    public List<UOW> eventsAfter(ID id, Long version) {
        return eventsAfter(id, version, Integer.MAX_VALUE);
    }

    @Override
    public List<UOW> eventsAfter(ID id, Long version, int limit) {
        List<UOW> events = map.get(id);
        return events == null ? emptyList() : events.stream()
                .filter(uow -> versionExtractor.apply(uow) > version)
                .limit(limit).collect(Collectors.toList());
    }

    @Override
    public Long lastVersion(ID id) {
        List<UOW> events = map.get(id);
        return events == null ? 0L : versionExtractor.apply(events.get(events.size()-1));
    }
}
