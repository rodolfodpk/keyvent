package kyvent

import java.util.*

data class CommandId(val uuid: UUID = UUID.randomUUID())
data class UnitOfWorkId(val uuid: UUID = UUID.randomUUID())
data class Version(val version: Long) {
    fun nextVersion(): Version { return Version(version.inc())}
}

class Snapshot<E> (val eventSourced: E, val version: Version) {
    fun nextVersion(): Version { return version.nextVersion()}
}

class StateTransitionsTracker<E, V> (val instance: E, val applyEventOn: (event: V, E) -> E) {
    val stateTransitions : MutableList<Pair<E, V>> = mutableListOf()
    fun apply(events: List<V>) {
        val last = if (stateTransitions.size == 0) instance else stateTransitions.last().first
        for (event in events) {
            stateTransitions.add(Pair(applyEventOn(event, last), event))
        }
    }
    fun collectedEvents() : List<V> {
        var i = 0
        for ((instance, event) in stateTransitions) {
            i++
            println("on event $i -> $event \ninstance   -> $instance")
        }
        return stateTransitions.map { pair -> pair.second }
    }
}

interface EventRepository<ID, UOW> {
    fun eventsForAfter(id : ID, afterVersion: Version) : List<UOW>
}

interface Journal<ID, UOW> {
    fun append(targetId: ID, unitOfWork: UOW)
}

class MapEventRepository<ID, UOW> (val map: MutableMap<ID, MutableList<UOW>> = mutableMapOf(),
                                   val versionExtractor: (UOW) -> Version)
                            : EventRepository<ID, UOW> {
    override fun eventsForAfter(id: ID, afterVersion: Version): List<UOW> {
        val targetInstance: MutableList<UOW>? = map[id]
        if (targetInstance == null) {
            return listOf()
        } else {
            return targetInstance.filter { uow -> versionExtractor(uow).version > afterVersion.version}
        }
    }

}

class MapJournal<ID, UOW> (val map: MutableMap<ID, MutableList<UOW>> = mutableMapOf(),
                           val versionExtractor: (UOW) -> Version) : Journal<ID, UOW> {
    override fun append(targetId: ID, unitOfWork: UOW) {
        val targetInstance: MutableList<UOW>? = map[targetId]
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