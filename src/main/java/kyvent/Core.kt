package kyvent

data class Version(val version: Long)

class Snapshot<E> (val eventSourced: E, val version: Version) {
    fun nextVersion(): Version { return Version(version.version.inc())}
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
    fun eventsFor(id : ID, deser: (UOW) -> String)
    fun eventsForAfter(id : ID, deser: (UOW) -> String, afterVersion: Version)
}

interface Journal<ID, CMD, UOW> {
    fun append(targetId: ID, command: CMD, unitOfWork: UOW)
}