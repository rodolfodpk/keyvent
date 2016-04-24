package keyvent.flows;

import customer.CustomerEvtFn;
import javaslang.collection.List;
import lombok.val;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static customer.CustomerSchema.*;
import static junit.framework.Assert.assertEquals;

public class SimpleStateTransitionsTrackerTest {

    @Test
    public void should_return_original_snapshot(){
        val snapshotZero = new Snapshot<Customer>(Customer.builder().build(), new Version(0L));
        val tracker = new SimpleStateTransitionsTracker<CustomerEvent, Customer>(snapshotZero, new CustomerEvtFn(), List.empty());
        assertEquals(snapshotZero, tracker.originalSnapshot());
    }

    @Test
    public void should_reflect_after_applying_event1() {
        val snapshotZero = new Snapshot<Customer>(Customer.builder().build(), new Version(0L));
        val tracker = new SimpleStateTransitionsTracker<CustomerEvent, Customer>(snapshotZero, new CustomerEvtFn(), List.empty());
        val event = CustomerCreated.builder().customerId(new CustomerId(UUID.randomUUID())).build();
        tracker.apply(List.of(event));
        val expected = Customer.builder().id(event.getCustomerId()).build();
        assertEquals(1, tracker.stateTransitions().size());
        assertEquals(event, tracker.stateTransitions().get(0).event);
        assertEquals(tracker.stateTransitions().get(0).resultingInstance, expected);
        assertEquals(tracker.appliedEvents(), List.of(event));
        val expectedSnapshot = new Snapshot<>(Customer.builder().id(event.getCustomerId()).build(), new Version(1L));
        assertEquals(expectedSnapshot, tracker.resultingSnapshot());
    }

    @Test
    public void should_reflect_after_applying_event1_and_event2() {
        val snapshotZero = new Snapshot<Customer>(Customer.builder().build(), new Version(0L));
        val tracker = new SimpleStateTransitionsTracker<CustomerEvent, Customer>(snapshotZero, new CustomerEvtFn(), List.empty());
        val event1 = CustomerCreated.builder().customerId(new CustomerId(UUID.randomUUID())).build();
        val event2 = CustomerActivated.builder().customerId(event1.getCustomerId()).date(LocalDateTime.now()).build();
        tracker.apply(List.of(event1, event2));
        val expected1 = Customer.builder().id(event1.getCustomerId()).build();
        val expected2 = Customer.builder().id(event1.getCustomerId())
                .isActive(true)
                .activeSince(event2.getDate()).build();
        assertEquals(2, tracker.stateTransitions().size());
        assertEquals(event1, tracker.stateTransitions().get(0).event);
        assertEquals(expected1, tracker.stateTransitions().get(0).resultingInstance);
        assertEquals(event2, tracker.stateTransitions().get(1).event);
        assertEquals(expected2, tracker.stateTransitions().get(1).resultingInstance);
        assertEquals(tracker.appliedEvents(), List.of(event1, event2));
        assertEquals(tracker.resultingSnapshot(), new Snapshot<>(expected2, new Version(1L)));
   }

}