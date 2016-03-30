package keyvent.core.impl;

import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.control.Option;
import keyvent.core.EventRepository;
import keyvent.core.data.Version;

public class SimpleEventRepository<ID, UOW> implements EventRepository<ID, UOW> {

    final Map<ID, List<Tuple2<UOW, Version>>> map;
    final int DEFAULT_LIMIT = 1000;

    public SimpleEventRepository(Map<ID, List<Tuple2<UOW, Version>>> map) {
        this.map = map;
    }

    @Override
    public List<UOW> eventsAfter(ID id, Version version) {
        return eventsAfter(id, version, DEFAULT_LIMIT);
    }

    @Override
    public List<UOW> eventsAfter(ID id, Version version, int limit) {
        List<Tuple2<UOW, Version>> events = map.get(id).getOrElse(List.empty());
        return events
                .filter(tuple -> tuple._2.value() > version.value())
                .map(tuple -> tuple._1)
                .take(limit).toList();
    }

    @Override
    public Option<Version> lastVersion(ID id) {
        List<Tuple2<UOW, Version>> events = map.get(id).getOrElse(List.empty());
        return events.size() == 0 ? Option.none() : Option.of(events.get(events.size()-1)._2);
    }
}
