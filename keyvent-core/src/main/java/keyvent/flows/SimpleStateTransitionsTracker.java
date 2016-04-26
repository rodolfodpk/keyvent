package keyvent.flows;

import javaslang.Function2;
import javaslang.collection.List;

import java.util.Objects;

import static keyvent.flows.Version.nextVersion;

public class SimpleStateTransitionsTracker<EV, AR> implements StateTransitionsTracker<EV, AR> {

    final Snapshot<AR> originalSnapshot;
    final Function2<EV, AR, AR> applyEventsFn;
    List<StateTransition<EV, AR>> stateTransitions;

    public SimpleStateTransitionsTracker(Snapshot<AR> originalSnapshot, Function2<EV, AR, AR> applyEventsFn) {
        Objects.requireNonNull(originalSnapshot);
        Objects.requireNonNull(applyEventsFn);
        this.originalSnapshot = originalSnapshot;
        this.applyEventsFn = applyEventsFn;
        this.stateTransitions = List.empty();
    }

    public SimpleStateTransitionsTracker(Snapshot<AR> originalSnapshot, Function2<EV, AR, AR> applyEventsFn,
                                         List<StateTransition<EV, AR>> stateTransitions) {
        Objects.requireNonNull(originalSnapshot);
        Objects.requireNonNull(applyEventsFn);
        Objects.requireNonNull(stateTransitions);
        this.originalSnapshot = originalSnapshot;
        this.applyEventsFn = applyEventsFn;
        this.stateTransitions = stateTransitions;
    }

    @Override
    public Snapshot<AR> originalSnapshot() {
        return originalSnapshot;
    }

    @Override
    public void apply(List<EV> events) {
        Objects.requireNonNull(events);
        for (EV event : events) {
            final AR last = stateTransitions.size() == 0 ?
                    originalSnapshot.getValue() :
                    stateTransitions.last().resultingInstance;
            this.stateTransitions = stateTransitions.append(
                    new StateTransition<>(event, applyEventsFn.apply(event, last)));
        }
    }

    @Override
    public List<EV> appliedEvents() {
        return stateTransitions.map(stateTransition -> stateTransition.event).toList();
    }

    @Override
    public Snapshot<AR> resultingSnapshot() {
        return new Snapshot<>(stateTransitions.last().resultingInstance, nextVersion(originalSnapshot.getVersion()));
    }

    @Override
    public List<StateTransition<EV, AR>> stateTransitions() {
        return stateTransitions.toList();
    }

}
