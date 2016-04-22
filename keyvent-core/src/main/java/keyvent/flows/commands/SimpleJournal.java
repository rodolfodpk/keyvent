package keyvent.flows.commands;

import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.collection.Map;
import keyvent.data.Version;
import keyvent.data.GlobalEventSeq;

import java.util.Objects;

public class SimpleJournal<ID, UOW> implements Journal<ID, UOW> {

    private Map<ID, List<Tuple2<UOW, Version>>> map;
    private GlobalEventSeq globalEventSequence;

    public SimpleJournal(Map<ID, List<Tuple2<UOW, Version>>> map) {
        Objects.requireNonNull(map);
        this.map = map;
        this.globalEventSequence = new GlobalEventSeq(new Integer(map.size()).longValue());
    }

    @Override
    public GlobalEventSeq append(ID targetId, UOW unitOfWork, Version resultingVersion) {
        Objects.requireNonNull(targetId);
        Objects.requireNonNull(unitOfWork);
        Objects.requireNonNull(resultingVersion);
        List<Tuple2<UOW, Version>> currentEvents = map.get(targetId).getOrElse(List.empty());
        if (currentEvents.isEmpty()) {
            if (resultingVersion.getValue() != 1)
                throw new IllegalArgumentException();
            map = map.put(targetId, currentEvents.append(new Tuple2<>(unitOfWork, resultingVersion)));
        } else {
            Tuple2<UOW, Version> lastUow = currentEvents.get(currentEvents.size()-1);
            Long newVersion = lastUow._2().getValue()+1;
            if (!Objects.equals(resultingVersion.getValue(), newVersion))
                throw new IllegalArgumentException();
            map = map.put(targetId, currentEvents.append(new Tuple2<>(unitOfWork, new Version(newVersion))));
        }
        globalEventSequence = new GlobalEventSeq(globalEventSequence.getValue() + 1) ;
        return globalEventSequence;
    }

    Map<ID, List<Tuple2<UOW, Version>>> map(){
        return map;
    }

}
