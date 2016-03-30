package keyvent.core;

import keyvent.core.data.Version;

public interface Journal<ID, UOW> {

    Long append(ID targetId, UOW unitOfWork, Version version);

    Long globalEventSequence();
}
