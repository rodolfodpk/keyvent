package keyvent.flows.commands;

import keyvent.data.GlobalEventSeq;
import keyvent.data.Version;

public interface Journal<ID, UOW> {

    GlobalEventSeq append(ID targetId, UOW unitOfWork, Version resultingVersion);

}
