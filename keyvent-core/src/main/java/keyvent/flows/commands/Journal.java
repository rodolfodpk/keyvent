package keyvent.flows.commands;

import keyvent.data.Version;

public interface Journal<ID, UOW> {

    Long append(ID targetId, UOW unitOfWork, Version version);

    Long globalEventSequence();
}
