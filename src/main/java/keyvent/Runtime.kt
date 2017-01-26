package keyvent

import com.github.kittinunf.result.Result
import com.google.inject.Injector

interface ArContainer<AR> {

    fun initialInstanceFn(injector: Injector): AR

    fun stateTransitionFn(instance: AR, event: Event): AR

    fun handleCommandFn(instance: AR, version: Version,
                        command: Command, stateTransitionFn: (Event, AR) -> AR): Result<UnitOfWork, Exception>


}

class StateTransitionsTracker<A : AggregateRoot, E : Event>(val instance: A,
                                                            val applyEventOn: (event: E, aggregateRoot: A) -> A) {
    val stateTransitions: MutableList<Pair<A, E>> = mutableListOf()
    fun apply(events: List<E>) {
        val last = if (stateTransitions.size == 0) instance else stateTransitions.last().first
        events.mapTo(stateTransitions) { Pair(applyEventOn(it, last), it) }
    }

    fun collectedEvents(): List<E> {
        return stateTransitions.map { pair -> pair.second }
    }

    fun currentState(): A {
        return stateTransitions.last().first;
    }
}
