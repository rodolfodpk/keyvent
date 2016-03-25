package keyvent.core;

import java.util.List;

public interface StateTransitionTracker<EV, AR> {

    void apply(List<EV> events, AR aggregateRootInstance);
    List<EV> collectedEvents();
}
