package keyvent.core.impl.mem

import keyvent.core.kotlin.*
import org.jetbrains.spek.api.Spek
import java.time.LocalDateTime
import java.util.function.BiFunction
import kotlin.test.assertEquals

class SimpleStateTransitionsTrackerSpec : Spek() {

    init {
        given("An empty tracker and customer") {
            val applyEventsFn = BiFunction<CustomerEvent, Customer, Customer>({
                event, instance ->
                applyEventOnCustomer.invoke(event, instance)
            })
            val customer = Customer()
            val tracker = SimpleStateTransitionTracker<CustomerEvent, Customer>(customer, applyEventsFn,
                    mutableListOf())
            on("applying an customerCreated event against it") {
                val event = CustomerCreated(CustomerId())
                tracker.apply(listOf(event), customer)
                it("should have 1 transition with proper event and resulting instance") {
                    assertEquals(1, tracker.stateTransitions.size)
                    assertEquals(event, tracker.stateTransitions.get(0).event)
                    assertEquals(Customer(event.customerId, null, false, null), tracker.stateTransitions.get(0).aggregateRootInstance)
                }
                it("should result in a list with customerCreated event when calling collectedEvents") {
                    assertEquals(tracker.collectedEvents(), listOf(event))
                }
            }
        }
        given("An empty tracker and customer") {
            val applyEventsFn = BiFunction<CustomerEvent, Customer, Customer>({
                event, instance ->
                applyEventOnCustomer.invoke(event, instance)
            })
            val customer = Customer()
            val tracker = SimpleStateTransitionTracker<CustomerEvent, Customer>(customer, applyEventsFn,
                    mutableListOf())
            on("applying both customerCreated and customerActivated events against it") {
                val event1 = CustomerCreated(CustomerId())
                val event2 = CustomerActivated(LocalDateTime.now())
                tracker.apply(listOf(event1, event2), customer)
                it("should have 2 transition2 with proper event and resulting instance") {
                    assertEquals(2, tracker.stateTransitions.size)
                    assertEquals(event1, tracker.stateTransitions.get(0).event)
                    assertEquals(Customer(event1.customerId, null, false, null), tracker.stateTransitions.get(0).aggregateRootInstance)
                    assertEquals(event2, tracker.stateTransitions.get(1).event)
                    assertEquals(Customer(event1.customerId, null, true, event2.date), tracker.stateTransitions.get(1).aggregateRootInstance)
                }
                it("should result in a list with both customerCreated and customerActivated events when calling collectedEvents") {
                    assertEquals(tracker.collectedEvents(), listOf(event1, event2))
                }
            }
        }

    }
}