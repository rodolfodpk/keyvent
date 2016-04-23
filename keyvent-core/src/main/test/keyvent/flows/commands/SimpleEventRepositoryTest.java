package keyvent.flows.commands;

import javaslang.Tuple2;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import keyvent.data.Version;
import lombok.val;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import static customer.CustomerSchema.*;
import static junit.framework.Assert.assertEquals;

public class SimpleEventRepositoryTest {

    CustomerCommand createCustomerCmd = CreateCustomer.builder()
            .commandId(new CommandId(UUID.randomUUID()))
            .customerId(new CustomerId(UUID.randomUUID())).build();

    CustomerUow uow1 = CustomerUow.builder()
            .id(new UnitOfWorkId())
            .command(createCustomerCmd)
            .version(1L)
            .events(List.of(CustomerCreated.builder().customerId(createCustomerCmd.getCustomerId()).build()))
            .instant(Instant.now())
            .build();

    ActivateCustomer activateCmd = ActivateCustomer.builder().commandId(new CommandId(UUID.randomUUID()))
            .customerId(createCustomerCmd.getCustomerId()).build();

    CustomerUow uow2 = CustomerUow.builder().id(new UnitOfWorkId())
            .command(activateCmd)
            .version(2L)
            .events(List.of(CustomerActivated.builder()
                    .customerId(activateCmd.getCustomerId())
                    .date(LocalDateTime.now()).build()))
            .instant(Instant.now())
            .build();

    @Test
    public void querying_a_missing_id_should_return_an_empty_list(){
         SimpleEventRepository<CustomerId, CustomerUow> eventRepo = new SimpleEventRepository<>();
         CustomerId customerId = new CustomerId(UUID.randomUUID());
         val uowList = eventRepo.eventsAfter(customerId, new Version(0L), Integer.MAX_VALUE);
         assertEquals(uowList, List.empty());
    }

    @Test
    public void querying_valid_id_should_return_list_with_uow1(){
        val map = HashMap.of(createCustomerCmd.getCustomerId(), List.of(new Tuple2<>(uow1, new Version(1L))));
        SimpleEventRepository<CustomerId, CustomerUow> eventRepo = new SimpleEventRepository<>(map);
        val uowList = eventRepo.eventsAfter(createCustomerCmd.getCustomerId(), new Version(0L), Integer.MAX_VALUE);
        assertEquals(uowList, List.of(uow1));
    }

    @Test
    public void quering_two_valid_ids_should_return_a_list_with_both_uow1_and_uow2 () {
        val map = HashMap.of(createCustomerCmd.getCustomerId(), List.of(new Tuple2<>(uow1, new Version(1L)), new Tuple2<>(uow2, new Version(2L))));
        SimpleEventRepository<CustomerId, CustomerUow> eventRepo = new SimpleEventRepository<>(map);
        val uowList = eventRepo.eventsAfter(createCustomerCmd.getCustomerId(), new Version(0L), Integer.MAX_VALUE);
        assertEquals(uowList, List.of(uow1, uow2));
    }

    @Test
    public void quering_after_version1_should_return_a_list_with_only_uow2 () {
        val map = HashMap.of(createCustomerCmd.getCustomerId(), List.of(new Tuple2<>(uow1, new Version(1L)), new Tuple2<>(uow2, new Version(2L))));
        SimpleEventRepository<CustomerId, CustomerUow> eventRepo = new SimpleEventRepository<>(map);
        val uowList = eventRepo.eventsAfter(createCustomerCmd.getCustomerId(), new Version(1L), Integer.MAX_VALUE);
        assertEquals(uowList, List.of(uow2));
    }

    @Test
    public void quering_0_lines_should_return_an_empty_list () {
        val map = HashMap.of(createCustomerCmd.getCustomerId(), List.of(new Tuple2<>(uow1, new Version(1L)), new Tuple2<>(uow2, new Version(2L))));
        SimpleEventRepository<CustomerId, CustomerUow> eventRepo = new SimpleEventRepository<>(map);
        val uowList = eventRepo.eventsAfter(createCustomerCmd.getCustomerId(), new Version(0L), 0);
        assertEquals(uowList, List.empty());
    }

}
