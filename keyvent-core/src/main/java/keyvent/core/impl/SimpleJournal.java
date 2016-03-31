package keyvent.core.impl;

import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.collection.Map;
import keyvent.core.Journal;
import keyvent.core.data.Version;

import java.util.Objects;

public class SimpleJournal<ID, UOW> implements Journal<ID, UOW> {

    private Map<ID, List<Tuple2<UOW, Version>>> map;
    private long globalEventSequence;

    public SimpleJournal(Map<ID, List<Tuple2<UOW, Version>>> map) {
        Objects.requireNonNull(map);
        this.map = map;
        this.globalEventSequence = map.size();
    }

    @Override
    public Long append(ID targetId, UOW unitOfWork, Version version) {
        Objects.requireNonNull(targetId);
        Objects.requireNonNull(unitOfWork);
        Objects.requireNonNull(version);
        List<Tuple2<UOW, Version>> currentEvents = map.get(targetId).getOrElse(List.empty());
        if (currentEvents.isEmpty()) {
            if (version.value() != 1)
                throw new IllegalArgumentException();
            map = map.put(targetId, currentEvents.append(new Tuple2<>(unitOfWork, version)));
        } else {
            Tuple2<UOW, Version> lastUow = currentEvents.get(currentEvents.size()-1);
            Version newVersion = Version.nextVersionOf(lastUow._2());
            if (!version.equals(newVersion))
                throw new IllegalArgumentException();
            map = map.put(targetId, currentEvents.append(new Tuple2<>(unitOfWork, newVersion)));
        }
        globalEventSequence += 1 ;
        return globalEventSequence;
    }

    @Override
    public Long globalEventSequence() {
        return globalEventSequence;
    }

    Map<ID, List<Tuple2<UOW, Version>>> map(){
        return map;
    }

}
