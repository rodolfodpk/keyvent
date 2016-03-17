package kyvent

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDateTime
import java.util.*

interface EventSourced
interface AggregateRoot : EventSourced
interface Saga : EventSourced

data class EventSourcedId(val uuid: UUID = UUID.randomUUID())
data class CommandId(val uuid: UUID = UUID.randomUUID())
data class UnitOfWorkId(val uuid: UUID = UUID.randomUUID())
data class Version(val version: Long)

data class UnitOfWork(val id: UnitOfWorkId = UnitOfWorkId(),
                      val command: Command,
                      val version: Version,
                      val events: List<Event>,
                      val timestamp : LocalDateTime = LocalDateTime.now())

class Snapshot<E: EventSourced> (val eventSourced: E, val version: Version) {
    fun nextVersion(): Version { return Version(version.version.inc())
    }
}

class StateTransitionsTracker<E: EventSourced, V> (val instance: E, val applyEventOn: (event: V, E) -> E) {
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

