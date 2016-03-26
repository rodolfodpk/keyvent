package keyvent.core.impl;

import keyvent.core.Journal;

import java.util.*;
import java.util.function.Function;

public class SimpleJournal<ID, UOW> implements Journal<ID, UOW> {

    final Function<UOW, Long> versionExtractor;
    final Map<ID, List<UOW>> map;
    long globalEventSequence;

    public SimpleJournal(Function<UOW, Long> versionExtractor, Map<ID, List<UOW>> map) {
        Objects.requireNonNull(versionExtractor);
        this.versionExtractor = versionExtractor;
        this.map = map;
        this.globalEventSequence = map.size();
    }

    @Override
    public Long append(ID targetId, UOW unitOfWork) {
        Objects.requireNonNull(targetId);
        Objects.requireNonNull(unitOfWork);
        List<UOW> events = map.get(targetId);
        if (events==null) {
            events = new ArrayList<>();
            if (versionExtractor.apply(unitOfWork)!= 1)
                throw new IllegalArgumentException();
        } else {
            UOW lastUow = events.get(events.size()-1);
            if (versionExtractor.apply(unitOfWork)!= versionExtractor.apply(lastUow)+1)
                throw new IllegalArgumentException();
        }
        events.add(unitOfWork);
        map.put(targetId, events);
        globalEventSequence += 1 ;
        return globalEventSequence;
    }
}
