package keyvent.core.impl;

import javaslang.Function2;
import javaslang.collection.List;
import keyvent.core.StateTransitionsTracker;
import keyvent.core.data.Snapshot;

import java.util.Objects;

public class SimpleStateTransitionsTracker<EV, AR> implements StateTransitionsTracker<EV, AR> {

    final Snapshot<AR> originalInstance;
    final Function2<EV, AR, AR> applyEventsFn;
    List<StateTransition<EV, AR>> stateTransitions;

    public SimpleStateTransitionsTracker(Snapshot<AR> originalInstance, Function2<EV, AR, AR> applyEventsFn, List<StateTransition<EV, AR>> stateTransitions) {
        Objects.requireNonNull(originalInstance);
        Objects.requireNonNull(applyEventsFn);
        Objects.requireNonNull(stateTransitions);
        this.originalInstance = originalInstance;
        this.applyEventsFn = applyEventsFn;
        this.stateTransitions = stateTransitions;
    }

    public SimpleStateTransitionsTracker(Snapshot<AR> originalInstance, Function2<EV, AR, AR> applyEventsFn) {
        Objects.requireNonNull(originalInstance);
        Objects.requireNonNull(applyEventsFn);
        this.originalInstance = originalInstance;
        this.applyEventsFn = applyEventsFn;
        this.stateTransitions = List.empty();
    }

    @Override
    public Snapshot<AR> originalInstance() {
        return originalInstance;
    }

    @Override
    public void apply(List<EV> events) {
        Objects.requireNonNull(events);
        for (EV event : events) {
            final AR last = stateTransitions.size() == 0 ?
                    originalInstance.instance() :
                    stateTransitions.last().resultingInstance;
            this.stateTransitions = stateTransitions.append(new StateTransition<>(event, applyEventsFn.apply(event, last)));
        }
    }

    @Override
    public List<EV> appliedEvents() {
        return stateTransitions.map(stateTransition -> stateTransition.event).toList();
    }

    @Override
    public Snapshot<AR> resultingInstance() {
        return new Snapshot<>(stateTransitions.last().resultingInstance, originalInstance.nextVersion());
    }

    @Override
    public List<StateTransition<EV, AR>> stateTransitions() {
        return stateTransitions.toList();
    }

}
