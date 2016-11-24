package keyvent

import java.util.*

data class CommandId(val uuid: UUID = UUID.randomUUID())

data class UnitOfWorkId(val uuid: UUID = UUID.randomUUID())

data class Version(val version: Long) {
    fun nextVersion(): Version { return Version(version.inc())}
}

class Snapshot<A> (val eventSourced: A, val version: Version) {
    fun nextVersion(): Version { return version.nextVersion()}
}

class StateTransitionsTracker<A, E> (val instance: A, val applyEventOn: (event: E, A) -> A) {
    val stateTransitions : MutableList<Pair<A, E>> = mutableListOf()
    fun apply(events: List<E>) {
        val last = if (stateTransitions.size == 0) instance else stateTransitions.last().first
        events.mapTo(stateTransitions) { Pair(applyEventOn(it, last), it) }
    }
    fun collectedEvents() : List<E> {
        var i = 0
        for ((instance, event) in stateTransitions) {
            i++
            // println("on event $i -> $event \ninstance   -> $instance")
        }
        return stateTransitions.map { pair -> pair.second }
    }
}

interface EventRepository<ID, UOW> {
    fun eventsAfter(id : ID, afterVersion: Version) : List<UOW>
}

interface Journal<ID, UOW> {
    fun append(targetId: ID, unitOfWork: UOW)
}

class MapEventRepository<ID, UOW> (val map: MutableMap<ID, MutableList<UOW>> = mutableMapOf(),
                                   val versionExtractor: (UOW) -> Version)
                            : EventRepository<ID, UOW> {
    override fun eventsAfter(id: ID, afterVersion: Version): List<UOW> {
        val targetInstance = map[id]
        return targetInstance?.filter { uow -> versionExtractor(uow).version > afterVersion.version} ?: listOf()
    }

}

class MapJournal<ID, UOW> (val map: MutableMap<ID, MutableList<UOW>> = mutableMapOf(),
                           val versionExtractor: (UOW) -> Version) : Journal<ID, UOW> {
    override fun append(targetId: ID, unitOfWork: UOW) {
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