package kyvent

import java.time.LocalDateTime
import java.util.*

interface EventSourced
interface Event
interface AggregateRoot : EventSourced
interface Saga : EventSourced

data class EventSourcedId(val uuid: UUID)
data class CommandId(val uuid: UUID)
data class UnitOfWorkId(val uuid: UUID)
data class Version(val version: Long)
data class UnitOfWork(val id: UnitOfWorkId = UnitOfWorkId(UUID.randomUUID()), val command: Command,
                      val version: Version, val events: List<Event>,
                      val timestamp : LocalDateTime = LocalDateTime.now())

interface Command {
    val commandId: CommandId
    val eventSourcedId : EventSourcedId
}

class Snapshot<E: EventSourced> (val eventSourced: E, val version: Version) {
    fun nextVersion(): Version { return Version(version.version.inc())
    }
}

class StateTransitionsTracker<E: EventSourced> (val instance: E, val applyEventOn: (event: Event, E) -> E) {
    val stateTransitions : MutableList<Pair<E, Event>> = mutableListOf()
    fun apply(events: List<Event>) {
        val last = if (stateTransitions.size == 0) instance else stateTransitions.last().first
        for (event in events) {
            stateTransitions.add(Pair(applyEventOn(event, last), event))
        }
    }
    fun collectedEvents() : List<Event> {
        for ((instance, event) in stateTransitions) {
            println("on event -> $event \ninstance -> $instance")
        }
        return stateTransitions.map { pair -> pair.second }
    }
}

