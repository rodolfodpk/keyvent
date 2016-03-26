package keyvent.core.impl;

import keyvent.core.StateTransitionTracker;

import java.util.List;
import java.util.function.BiFunction;

import static java.util.stream.Collectors.toList;

// TODO javaslang impl with persistent collections + jhm

public class SimpleStateTransitionTracker<EV, AR> implements StateTransitionTracker<EV, AR>{

    final AR originalInstance;
    final BiFunction<EV, AR, AR> applyEventsFn;
    final List<StateTransition<EV, AR>> stateTransitions;

    public SimpleStateTransitionTracker(AR originalInstance, BiFunction<EV, AR, AR> applyEventsFn, List<StateTransition<EV, AR>> stateTransitions) {
        this.originalInstance = originalInstance;
        this.applyEventsFn = applyEventsFn;
        this.stateTransitions = stateTransitions;
    }

    @Override
    public AR originalInstance() {
        return originalInstance;
    }

    @Override
    public void apply(List<EV> events) {
        for (EV event : events) {
            final AR last = stateTransitions.size() == 0 ?
                    originalInstance :
                    stateTransitions.get(stateTransitions.size()-1).aggregateRootInstance;
            stateTransitions.add(new StateTransition<>(event, applyEventsFn.apply(event, last)));
        }
    }

    @Override
    public List<EV> collectedEvents() {
        return stateTransitions.stream().map(stateTransition -> stateTransition.event).collect(toList());
    }

    @Override
    public AR resultingInstance() {
        return stateTransitions.get(stateTransitions.size()-1).aggregateRootInstance;
    }

    class StateTransition<EV, AR> {
        final EV event;
        final AR aggregateRootInstance;
        StateTransition(EV event, AR aggregateRootInstance) {
            this.event = event;
            this.aggregateRootInstance = aggregateRootInstance;
        }
    }

}
