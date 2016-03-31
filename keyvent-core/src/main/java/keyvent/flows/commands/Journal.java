package keyvent.flows.commands;

public interface Journal<ID, UOW> {

    Long append(ID targetId, UOW unitOfWork, Long version);

    Long globalEventSequence();
}
