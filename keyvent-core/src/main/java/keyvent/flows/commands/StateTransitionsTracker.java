package keyvent.flows.commands;

import javaslang.Tuple2;
import javaslang.collection.List;

public interface StateTransitionsTracker<EV, AR> {

    Tuple2<AR, Long> originalSnapshot();
    void apply(List<EV> events);
    List<EV> appliedEvents();
    Tuple2<AR, Long> resultingSnapshot();
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
