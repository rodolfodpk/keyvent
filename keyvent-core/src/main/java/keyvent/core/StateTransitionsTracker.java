package keyvent.core;

import keyvent.core.data.Snapshot;

import java.util.List;

public interface StateTransitionsTracker<EV, AR> {

    Snapshot<AR> originalInstance();
    void apply(List<EV> events);
    List<EV> appliedEvents();
    Snapshot<AR> resultingInstance();
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
