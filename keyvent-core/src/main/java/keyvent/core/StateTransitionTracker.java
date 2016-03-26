package keyvent.core;

import java.util.List;

public interface StateTransitionTracker<EV, AR> {

    AR originalInstance();
    void apply(List<EV> events);
    List<EV> collectedEvents();
    AR resultingInstance();
}
