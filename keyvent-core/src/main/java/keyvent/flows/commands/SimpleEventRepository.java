package keyvent.flows.commands;

import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.control.Option;

import java.util.Objects;

public class SimpleEventRepository<ID, UOW> implements EventRepository<ID, UOW> {

    final Map<ID, List<Tuple2<UOW, Long>>> map;
    final int DEFAULT_LIMIT = 1000;

    public SimpleEventRepository(Map<ID, List<Tuple2<UOW, Long>>> map) {
        this.map = map;
    }

    @Override
    public List<UOW> eventsAfter(ID id, Long version) {
        return eventsAfter(id, version, DEFAULT_LIMIT);
    }

    @Override
    public List<UOW> eventsAfter(ID id, Long version, int limit) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(version);
        List<Tuple2<UOW, Long>> events = map.get(id).getOrElse(List.empty());
        return events
                .filter(tuple -> tuple._2() > version)
                .map(tuple -> tuple._1)
                .take(limit)
                .toList();
    }

    @Override
    public Option<Long> lastVersion(ID id) {
        List<Tuple2<UOW, Long>> events = map.get(id).getOrElse(List.empty());
        return events.size() == 0 ? Option.none() : Option.of(events.get(events.size()-1)._2);
    }
}
