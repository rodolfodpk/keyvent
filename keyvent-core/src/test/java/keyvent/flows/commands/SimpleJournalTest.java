package keyvent.flows.commands;

import javaslang.Tuple2;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import keyvent.flows.GlobalEventSeq;
import keyvent.flows.Version;
import lombok.val;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import static customer.CustomerSchema.*;
import static junit.framework.Assert.assertEquals;

public class SimpleJournalTest {

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
    public void after_adding_uow1_v1_it_should_reflect(){
        val journal = new SimpleJournal<CustomerId, CustomerUow>();
        val globalSeq = journal.append(createCustomerCmd.getCustomerId(), uow1, new Version(1L));
        val expected =  List.of(new Tuple2<>(uow1, new Version(1L)));
        assertEquals(journal.map().get(createCustomerCmd.getCustomerId()).get(), expected);
        assertEquals(globalSeq, new GlobalEventSeq(1L));
    }

    @Test(expected = Exception.class)
    public void after_adding_uow2_v2_without_uow1_v1_it_should_reject(){
        val journal = new SimpleJournal<CustomerId, CustomerUow>();
        journal.append(createCustomerCmd.getCustomerId(), uow2, new Version(2L));
    }

    @Test
    public void after_adding_uow1_v1_it_should_accept_uow2_v2_and_reflect(){
        val map = HashMap.of(createCustomerCmd.getCustomerId(), List.of(new Tuple2<>(uow1, new Version(1L))));
        val journal = new SimpleJournal<CustomerId, CustomerUow>(map);
        val globalSeq = journal.append(createCustomerCmd.getCustomerId(), uow2, new Version(2L));
        val expected =  List.of(new Tuple2<>(uow1, new Version(1L)), new Tuple2<>(uow2, new Version(2L)));
        assertEquals(journal.map().get(createCustomerCmd.getCustomerId()).get(), expected);
        assertEquals(globalSeq, new GlobalEventSeq(2L));
    }

}