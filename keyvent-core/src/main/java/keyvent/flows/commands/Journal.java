package keyvent.flows.commands;

import keyvent.data.Version;
import keyvent.data.GlobalEventSeq;

public interface Journal<ID, UOW> {

    GlobalEventSeq append(ID targetId, UOW unitOfWork, Version resultingVersion);

}
