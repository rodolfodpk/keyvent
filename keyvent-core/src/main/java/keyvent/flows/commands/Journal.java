package keyvent.flows.commands;

import keyvent.flows.GlobalEventSeq;
import keyvent.flows.Version;

public interface Journal<ID, UOW> {

    GlobalEventSeq append(ID targetId, UOW unitOfWork, Version resultingVersion);

}
