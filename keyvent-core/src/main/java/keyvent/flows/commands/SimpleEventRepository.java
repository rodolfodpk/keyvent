package keyvent.flows.commands;

import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.control.Option;
import keyvent.data.Version;

import java.util.Objects;

public class SimpleEventRepository<ID, UOW> implements EventRepository<ID, UOW> {

    final Map<ID, List<Tuple2<UOW, Version>>> map;

    public SimpleEventRepository(Map<ID, List<Tuple2<UOW, Version>>> map) {
        this.map = map;
    }

    @Override
    public List<UOW> eventsAfter(ID id, Version version, int limit) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(version);
        List<Tuple2<UOW, Version>> events = map.get(id).getOrElse(List.empty());
        return events
                .filter(tuple -> tuple._2().getValue() > version.getValue())
                .map(tuple -> tuple._1)
                .take(limit)
                .toList();
    }

    @Override
    public Option<Version> lastVersion(ID id) {
        List<Tuple2<UOW, Version>> events = map.get(id).getOrElse(List.empty());
        return events.size() == 0 ? Option.none() : Option.of(events.get(events.size()-1)._2);
    }
}
