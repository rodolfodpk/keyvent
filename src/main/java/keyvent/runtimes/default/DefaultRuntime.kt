package keyvent.runtimes.default

import com.google.inject.Injector
import keyvent.*

class StateTransitionsTracker<A : AggregateRoot, E : Event>(val instance: A,
                                                            val applyEventOn: (event: E, aggregateRoot: A) -> A,
                                                            val injector: Injector) {

    val stateTransitions: MutableList<Pair<A, E>> = mutableListOf()

    fun apply(events: List<E>) {
        events.mapTo(stateTransitions) { Pair(applyEventOn(it, currentState()), it) }
    }

    fun collectedEvents(): List<E> {
        return stateTransitions.map { pair -> pair.second }
    }

    fun currentState(): A {
        val state = if (stateTransitions.size == 0) instance else stateTransitions.last().first
        injector.injectMembers(state)
        return state
    }
}

// default implementations TODO caffeine ?

class MapEventRepository<ID>(val map: MutableMap<ID, MutableList<UnitOfWork>> = mutableMapOf(),
                             val versionExtractor: (UnitOfWork) -> Version)
    : EventRepository<ID> {
    override fun eventsAfter(id: ID, afterVersion: Version): List<UnitOfWork> {
        val targetInstance = map[id]
        return targetInstance?.filter { uow -> versionExtractor(uow).version > afterVersion.version } ?: listOf()
    }

}

class MapJournal<ID>(val map: MutableMap<ID, MutableList<UnitOfWork>> = mutableMapOf(),
                     val versionExtractor: (UnitOfWork) -> Version) : Journal<ID> {
    override fun append(targetId: ID, unitOfWork: UnitOfWork) {
        val targetInstance = map[targetId]
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
