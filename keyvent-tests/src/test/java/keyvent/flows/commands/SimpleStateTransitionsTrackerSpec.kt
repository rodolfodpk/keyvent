package keyvent.flows.commands

import javaslang.Tuple2
import javaslang.collection.List
import keyvent.SimpleStateTransitionsTracker
import keyvent.sample.customer.CustomerEvtFn
import keyvent.sample.customer.CustomerSchema
import keyvent.sample.customer.CustomerSchema.*
import org.jetbrains.spek.api.Spek
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

class SimpleStateTransitionsTrackerSpec : Spek() {

    init {
        given("A fresh tracker for a given customer snapshot with version 0") {
            val snapshotZero = Tuple2(Customer.builder().build(), 0L)
            val tracker: SimpleStateTransitionsTracker<CustomerSchema.CustomerEvent, Customer> =
                    SimpleStateTransitionsTracker(snapshotZero, CustomerEvtFn(), List.empty())
            on("simple checking") {
                it ("should have the correct originalSnapshot") {
                    assertEquals(snapshotZero, tracker.originalSnapshot())
                }
            }
        }
        given("A fresh tracker for a given customer snapshot with version 0") {
            val snapshotZero = Tuple2(Customer.builder().build(), 0L)
            val tracker: SimpleStateTransitionsTracker<CustomerSchema.CustomerEvent, Customer> =
                    SimpleStateTransitionsTracker(snapshotZero, CustomerEvtFn(), List.empty())
            on("applying an customerCreated event against it") {
                val event: CustomerSchema.CustomerEvent = CustomerCreated.builder().customerId(CustomerId(UUID.randomUUID())).build()
                tracker.apply(List.of(event))
                it("should have 1 transition with proper event and resulting instance") {
                    assertEquals(1, tracker.stateTransitions().size())
                    assertEquals(event, tracker.stateTransitions().get(0).event)
                    assertEquals(Customer.builder().id(event.getCustomerId()).build(), tracker.stateTransitions().get(0).resultingInstance)
                }
                it("should result in a list with customerCreated event when calling appliedEvents") {
                    assertEquals(tracker.appliedEvents(), List.of(event))
                }
                it("should result in a proper customer instance when calling resultingSnapshot") {
                    val expected: CustomerSchema.Customer = Customer.builder().id(event.getCustomerId()).build()
                    assertEquals(expected, tracker.resultingSnapshot()._1())
                }
            }
        }
        given("A fresh tracker for a given customer snapshot with version 0") {
            val snapshotZero = Tuple2(Customer.builder().build(), 0L)
            val tracker: SimpleStateTransitionsTracker<CustomerSchema.CustomerEvent, Customer> =
                    SimpleStateTransitionsTracker(snapshotZero, CustomerEvtFn(), List.empty())
            on("applying both customerCreated and customerActivated events against it") {
                val event1 = CustomerCreated.builder().customerId(CustomerId(UUID.randomUUID())).build()
                val event2 = CustomerActivated.builder().customerId(event1.getCustomerId()).date(LocalDateTime.now()).build()
                tracker.apply(List.of(event1, event2))
                val expected1 = Customer.builder().id(event1.getCustomerId()).build()
                val expected2 = Customer.builder().id(event1.getCustomerId())
                        .isActive(true)
                        .activeSince(event2.getDate()).build()
                it("should have 2 transition2 with proper event and resulting instance") {
                    assertEquals(2, tracker.stateTransitions().size())
                    assertEquals(event1, tracker.stateTransitions().get(0).event)
                    assertEquals(expected1, tracker.stateTransitions().get(0).resultingInstance)
                    assertEquals(event2, tracker.stateTransitions().get(1).event)
                    assertEquals(expected2, tracker.stateTransitions().get(1).resultingInstance)
                }
                it("should result in a list with both customerCreated and customerActivated events when calling appliedEvents") {
                    assertEquals(tracker.appliedEvents(), List.of(event1, event2))
                }
                it("should result in a proper customer instance when calling resultingSnapshot") {
                    assertEquals(tracker.resultingSnapshot(), Tuple2(expected2, 1L))
                }
            }

        }

    }
}