package keyvent.core;

public interface Journal<ID, UOW> {

    Long append(ID targetId, UOW unitOfWork);
}
