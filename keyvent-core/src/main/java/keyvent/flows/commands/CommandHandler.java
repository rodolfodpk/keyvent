package keyvent.flows.commands;

import javaslang.Tuple3;
import javaslang.control.Try;
import keyvent.flows.StateTransitionsTracker;

public interface CommandHandler<ID, AR, CMD, EV> {

    Try<Tuple3<ID, CMD, StateTransitionsTracker<EV, AR>>> handle(Tuple3<ID, CMD, StateTransitionsTracker<EV, AR>> commandContext) ;

}
