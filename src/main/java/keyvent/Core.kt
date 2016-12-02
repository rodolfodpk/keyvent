package keyvent

import java.time.LocalDateTime
import java.util.*

data class CommandId(val uuid: UUID = UUID.randomUUID())

interface Command { val commandId : CommandId }

interface Event

interface AggregateRoot

data class UnitOfWorkId(val uuid: UUID = UUID.randomUUID())

data class Version(val version: Long) {
    fun nextVersion(): Version { return Version(version.inc())}
}

data class UnitOfWork(val id: UnitOfWorkId = UnitOfWorkId(),
                              val command: Command,
                              val version: Version,
                              val events: List<Event>,
                              val timestamp : LocalDateTime = LocalDateTime.now())


interface EventRepository<ID> {
    fun eventsAfter(id : ID, afterVersion: Version) : List<UnitOfWork>
}

interface Journal<ID> {
    fun append(targetId: ID, unitOfWork: UnitOfWork)
}

class StateTransitionsTracker<A : AggregateRoot, E : Event> (val instance: A,
                                                             val applyEventOn: (event: E, aggregateRoot: A) -> A) {
    val stateTransitions : MutableList<Pair<A, E>> = mutableListOf()
    fun apply(events: List<E>) {
        val last = if (stateTransitions.size == 0) instance else stateTransitions.last().first
        events.mapTo(stateTransitions) { Pair(applyEventOn(it, last), it) }
    }
    fun collectedEvents() : List<E> {
        return stateTransitions.map { pair -> pair.second }
    }
}

// default implementations - just for tests

class MapEventRepository<ID> (val map: MutableMap<ID, MutableList<UnitOfWork>> = mutableMapOf(),
                              val versionExtractor: (UnitOfWork) -> Version)
                            : EventRepository<ID> {
    override fun eventsAfter(id: ID, afterVersion: Version): List<UnitOfWork> {
        val targetInstance = map[id]
        return targetInstance?.filter { uow -> versionExtractor(uow).version > afterVersion.version} ?: listOf()
    }

}

class MapJournal<ID> (val map: MutableMap<ID, MutableList<UnitOfWork>> = mutableMapOf(),
                           val versionExtractor: (UnitOfWork) -> Version) : Journal<ID> {
    override fun append(targetId: ID, unitOfWork: UnitOfWork) {
        val targetInstance =  map[targetId]
        if (targetInstance == null) {
            require(versionExtractor(unitOfWork) == Version(1))
            map.put(targetId, mutableListOf((unitOfWork)))
        } else {
            val lastVersion = versionExtractor(targetInstance.last())
            require(versionExtractor(unitOfWork) == lastVersion.nextVersion())
            targetInstance.add(unitOfWork)
        }
    }
}