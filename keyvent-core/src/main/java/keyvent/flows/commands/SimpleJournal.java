package keyvent.flows.commands;

import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.collection.Map;

import java.util.Objects;

public class SimpleJournal<ID, UOW> implements Journal<ID, UOW> {

    private Map<ID, List<Tuple2<UOW, Long>>> map;
    private long globalEventSequence;

    public SimpleJournal(Map<ID, List<Tuple2<UOW, Long>>> map) {
        Objects.requireNonNull(map);
        this.map = map;
        this.globalEventSequence = map.size();
    }

    @Override
    public Long append(ID targetId, UOW unitOfWork, Long version) {
        Objects.requireNonNull(targetId);
        Objects.requireNonNull(unitOfWork);
        Objects.requireNonNull(version);
        List<Tuple2<UOW, Long>> currentEvents = map.get(targetId).getOrElse(List.empty());
        if (currentEvents.isEmpty()) {
            if (version != 1)
                throw new IllegalArgumentException();
            map = map.put(targetId, currentEvents.append(new Tuple2<>(unitOfWork, version)));
        } else {
            Tuple2<UOW, Long> lastUow = currentEvents.get(currentEvents.size()-1);
            Long newVersion = lastUow._2()+1;
            if (version != newVersion)
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

    Map<ID, List<Tuple2<UOW, Long>>> map(){
        return map;
    }

}
