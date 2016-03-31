package keyvent.core.impl

import javaslang.collection.List
import keyvent.core.data.Snapshot
import keyvent.core.data.Version
import keyvent.sample.customer.*
import org.jetbrains.spek.api.Spek
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

class SimpleStateTransitionsTrackerSpec : Spek() {

    init {
        given("A fresh tracker for a given customer snapshot with version 0") {
            val snapshotZero = Snapshot(CustomerAgg.builder().build(), Version(0))
            val tracker: SimpleStateTransitionsTracker<CustomerSchema.CustomerEvent, CustomerAgg> =
                    SimpleStateTransitionsTracker(snapshotZero, CustomerEvtFn(), List.empty())
            on("simple checking") {
                it ("should have the correct originalInstance") {
                    assertEquals(snapshotZero, tracker.originalInstance())
                }
            }
        }
        given("A fresh tracker for a given customer snapshot with version 0") {
            val snapshotZero = Snapshot(CustomerAgg.builder().build(), Version(0))
            val tracker: SimpleStateTransitionsTracker<CustomerSchema.CustomerEvent, CustomerAgg> =
                    SimpleStateTransitionsTracker(snapshotZero, CustomerEvtFn(), List.empty())
            on("applying an customerCreated event against it") {
                val event: CustomerSchema.CustomerEvent = CustomerCreatedEvt.builder().customerId(CustomerIdVal.of(UUID.randomUUID())).build()
                tracker.apply(List.of(event))
                it("should have 1 transition with proper event and resulting instance") {
                    assertEquals(1, tracker.stateTransitions.size())
                    assertEquals(event, tracker.stateTransitions.get(0).event)
                    assertEquals(CustomerAgg.builder().id(event.customerId()).build(), tracker.stateTransitions.get(0).resultingInstance)
                }
                it("should result in a list with customerCreated event when calling appliedEvents") {
                    assertEquals(tracker.appliedEvents(), List.of(event))
                }
                it("should result in a proper customer instance when calling resultingSnapshot") {
                    val expected: CustomerSchema.Customer = CustomerAgg.builder().id(event.customerId()).build()
                    assertEquals(expected, tracker.resultingSnapshot().instance())
                }
            }
        }
        given("A fresh tracker for a given customer snapshot with version 0") {
            val snapshotZero = Snapshot(CustomerAgg.builder().build(), Version(0))
            val tracker: SimpleStateTransitionsTracker<CustomerSchema.CustomerEvent, CustomerAgg> =
                    SimpleStateTransitionsTracker(snapshotZero, CustomerEvtFn(), List.empty())
            on("applying both customerCreated and customerActivated events against it") {
                val event1 = CustomerCreatedEvt.builder().customerId(CustomerIdVal.of(UUID.randomUUID())).build()
                val event2 = CustomerActivatedEvt.builder().customerId(event1.customerId()).date(LocalDateTime.now()).build()
                tracker.apply(List.of(event1, event2))
                val expected1 = CustomerAgg.builder().id(event1.customerId()).build()
                val expected2 = CustomerAgg.builder().id(event1.customerId())
                        .isActive(true)
                        .activeSince(event2.date()).build()
                it("should have 2 transition2 with proper event and resulting instance") {
                    assertEquals(2, tracker.stateTransitions.size())
                    assertEquals(event1, tracker.stateTransitions.get(0).event)
                    assertEquals(expected1, tracker.stateTransitions.get(0).resultingInstance)
                    assertEquals(event2, tracker.stateTransitions.get(1).event)
                    assertEquals(expected2, tracker.stateTransitions.get(1).resultingInstance)
                }
                it("should result in a list with both customerCreated and customerActivated events when calling appliedEvents") {
                    assertEquals(tracker.appliedEvents(), List.of(event1, event2))
                }
                it("should result in a proper customer instance when calling resultingSnapshot") {
                    assertEquals(tracker.resultingSnapshot(), Snapshot(expected2, Version(1)))
                }
            }

        }

    }
}