package keyvent.core.impl;

import javaslang.Function2;
import javaslang.collection.List;
import keyvent.core.StateTransitionsTracker;
import keyvent.core.data.Snapshot;

public class SimpleStateTransitionsTracker<EV, AR> implements StateTransitionsTracker<EV, AR> {

    final Snapshot<AR> originalInstance;
    final Function2<EV, AR, AR> applyEventsFn;
    List<StateTransition<EV, AR>> stateTransitions;

    public SimpleStateTransitionsTracker(Snapshot<AR> originalInstance, Function2<EV, AR, AR> applyEventsFn, List<StateTransition<EV, AR>> stateTransitions) {
        this.originalInstance = originalInstance;
        this.applyEventsFn = applyEventsFn;
        this.stateTransitions = stateTransitions;
    }

    public SimpleStateTransitionsTracker(Snapshot<AR> originalInstance, Function2<EV, AR, AR> applyEventsFn) {
        this.originalInstance = originalInstance;
        this.applyEventsFn = applyEventsFn;
        this.stateTransitions = List.empty();
    }

    @Override
    public Snapshot<AR> originalInstance() {
        return originalInstance;
    }

    @Override
    public void apply(java.util.List<EV> events) {
        for (EV event : events) {
            final AR last = stateTransitions.size() == 0 ?
                    originalInstance.instance() :
                    stateTransitions.get(stateTransitions.size()-1).resultingInstance;
            this.stateTransitions = stateTransitions.append(new StateTransition<>(event, applyEventsFn.apply(event, last)));
        }
    }

    @Override
    public java.util.List<EV> appliedEvents() {
        return stateTransitions.map(stateTransition -> stateTransition.event).toJavaList();
    }

    @Override
    public Snapshot<AR> resultingInstance() {
        return new Snapshot<>(stateTransitions.get(stateTransitions.size()-1).resultingInstance, originalInstance.nextVersion());
    }

    @Override
    public java.util.List<StateTransition<EV, AR>> stateTransitions() {
        return stateTransitions.toJavaList();
    }

}
