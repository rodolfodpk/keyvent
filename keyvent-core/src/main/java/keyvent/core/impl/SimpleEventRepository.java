package keyvent.core.impl;

import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.collection.Map;
import keyvent.core.EventRepository;
import keyvent.core.data.Version;

import java.util.Optional;

public class SimpleEventRepository<ID, UOW> implements EventRepository<ID, UOW> {

    final Map<ID, List<Tuple2<UOW, Version>>> map;
    final int DEFAULT_LIMIT = 1000;

    public SimpleEventRepository(Map<ID, List<Tuple2<UOW, Version>>> map) {
        this.map = map;
    }

    @Override
    public java.util.List<UOW> eventsAfter(ID id, Version version) {
        return eventsAfter(id, version, DEFAULT_LIMIT);
    }

    @Override
    public java.util.List<UOW> eventsAfter(ID id, Version version, int limit) {
        List<Tuple2<UOW, Version>> events = map.get(id).getOrElse(List.empty());
        return events
                .filter(tuple -> tuple._2.value() > version.value())
                .map(tuple -> tuple._1)
                .take(limit).toJavaList();
    }

    @Override
    public Optional<Version> lastVersion(ID id) {
        List<Tuple2<UOW, Version>> events = map.get(id).getOrElse(List.empty());
        return events.size() == 0 ? Optional.empty() : Optional.of(events.get(events.size()-1)._2);
    }
}
