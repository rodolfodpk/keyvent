package keyvent;

import javaslang.Function2;
import javaslang.Tuple2;
import javaslang.collection.List;

import java.util.Objects;

public class SimpleStateTransitionsTracker<EV, AR> implements StateTransitionsTracker<EV, AR> {

    final Tuple2<AR, Long> originalSnapshot;
    final Function2<EV, AR, AR> applyEventsFn;
    List<StateTransition<EV, AR>> stateTransitions;

    public SimpleStateTransitionsTracker(Tuple2<AR, Long> originalSnapshot, Function2<EV, AR, AR> applyEventsFn, List<StateTransition<EV, AR>> stateTransitions) {
        Objects.requireNonNull(originalSnapshot);
        Objects.requireNonNull(applyEventsFn);
        Objects.requireNonNull(stateTransitions);
        this.originalSnapshot = originalSnapshot;
        this.applyEventsFn = applyEventsFn;
        this.stateTransitions = stateTransitions;
    }

    public SimpleStateTransitionsTracker(Tuple2<AR, Long> originalSnapshot, Function2<EV, AR, AR> applyEventsFn) {
        Objects.requireNonNull(originalSnapshot);
        Objects.requireNonNull(applyEventsFn);
        this.originalSnapshot = originalSnapshot;
        this.applyEventsFn = applyEventsFn;
        this.stateTransitions = List.empty();
    }

    @Override
    public Tuple2<AR, Long> originalSnapshot() {
        return originalSnapshot;
    }

    @Override
    public void apply(List<EV> events) {
        Objects.requireNonNull(events);
        for (EV event : events) {
            final AR last = stateTransitions.size() == 0 ?
                    originalSnapshot._1() :
                    stateTransitions.last().resultingInstance;
            this.stateTransitions = stateTransitions.append(new StateTransition<>(event, applyEventsFn.apply(event, last)));
        }
    }

    @Override
    public List<EV> appliedEvents() {
        return stateTransitions.map(stateTransition -> stateTransition.event).toList();
    }

    @Override
    public Tuple2<AR, Long> resultingSnapshot() {
        return new Tuple2<>(stateTransitions.last().resultingInstance, originalSnapshot._2()+1);
    }

    @Override
    public List<StateTransition<EV, AR>> stateTransitions() {
        return stateTransitions.toList();
    }

}
