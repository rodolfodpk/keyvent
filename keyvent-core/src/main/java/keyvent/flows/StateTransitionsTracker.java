package keyvent.flows;

import javaslang.collection.List;
import keyvent.data.Snapshot;

public interface StateTransitionsTracker<EV, AR> {

    Snapshot<AR> originalSnapshot();
    void apply(List<EV> events);
    List<EV> appliedEvents();
    Snapshot<AR> resultingSnapshot();
    List<StateTransition<EV, AR>> stateTransitions();

    class StateTransition<EV, AR> {
        public final EV event;
        public final AR resultingInstance;
        public StateTransition(EV event, AR resultingInstance) {
            this.event = event;
            this.resultingInstance = resultingInstance;
        }
    }
}
