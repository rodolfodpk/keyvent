package keyvent.flows.commands

import javaslang.Tuple2
import javaslang.collection.List
import javaslang.collection.Map
import keyvent.sample.CommandId
import keyvent.sample.UnitOfWorkId
import keyvent.sample.customer.*
import keyvent.sample.customer.CustomerSchema.CreateCustomer
import keyvent.sample.customer.CustomerSchema.CustomerUow
import org.jetbrains.spek.api.Spek
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SimpleJournalSpec : Spek() {

    val createCustomerCmd: CreateCustomer = CreateCustomer.builder()
            .commandId(CommandId())
            .customerId(CustomerSchema.CustomerId(UUID.randomUUID()))
            .name("alice")
            .age(35)
            .build()

    val uow1 = CustomerUow.builder()
            .id(UnitOfWorkId())
            .command(createCustomerCmd)
            .version(1)
            .events(List.of(CustomerSchema.CustomerCreated.builder().customerId(createCustomerCmd.getCustomerId()).build()))
            .instant(Instant.now())
            .build()

    val activateCmd = CustomerSchema.ActivateCustomer.builder().commandId(CommandId())
            .customerId(createCustomerCmd.getCustomerId()).build()

    val uow2 = CustomerUow.builder().id(UnitOfWorkId())
            .command(activateCmd)
            .version(2)
            .events(List.of(CustomerSchema.CustomerActivated.builder()
                    .customerId(activateCmd.getCustomerId())
                    .date(LocalDateTime.now()).build()))
            .instant(Instant.now())
            .build()

    init {
        given("An empty journal") {
            val map: Map<CustomerSchema.CustomerId, List<Tuple2<CustomerUow, Long>>> = javaslang.collection.HashMap.empty()
            val journal = SimpleJournal<CustomerSchema.CustomerId, CustomerUow>(map)
            on("adding a new unitOfWork with version=1") {
                val globalSeq: Long? = journal.append(createCustomerCmd.getCustomerId(), uow1, 1L)
                it("should result in a journal with the respective entry") {
                    val expected: List<Tuple2<CustomerUow, Long>> = List.of(Tuple2(uow1, 1L))
                    val current: List<Tuple2<CustomerUow, Long>> = journal.map().get(createCustomerCmd.getCustomerId()).get()
                    assertEquals(expected, current)
                }
                it("should result in a globalSequence = 1") {
                    assertEquals(globalSeq, 1)
                }
            }
        }
        given("An empty journal") {
            val map: Map<CustomerSchema.CustomerId, List<Tuple2<CustomerUow, Long>>> = javaslang.collection.HashMap.empty()
            val journal = SimpleJournal<CustomerSchema.CustomerId, CustomerUow>(map)
            on("adding a new unitOfWork with version =2") {
                it("should throw an exception since version should be 1") {
                    try {
                        journal.append(createCustomerCmd.getCustomerId(), uow2, 2)
                        assertTrue(false, "should throw IllegalArgumentException")
                    } catch (e: IllegalArgumentException) {
                    }
                }
            }
        }
        given("A journal with one uow with version =1") {
            val map: Map<CustomerSchema.CustomerId, List<Tuple2<CustomerUow, Long>>> =
                    javaslang.collection.HashMap.of(createCustomerCmd.getCustomerId(), List.of(Tuple2(uow1, 1L)))
            val journal = SimpleJournal<CustomerSchema.CustomerId, CustomerUow>(map)
            on("adding a new unitOfWork with version =2") {
                val globalSeq = journal.append(createCustomerCmd.getCustomerId(), uow2, 2)
                it("should result in a journal with the respective first and second entries") {
                    val expected: List<Tuple2<CustomerUow, Long>> = List.of(Tuple2(uow1, 1L), Tuple2(uow2, 2L))
                    val current: List<Tuple2<CustomerUow, Long>> = journal.map()[createCustomerCmd.getCustomerId()].get()
                    assertEquals(expected, current)
                }
                it("should result in a globalSequence = 2") {
                    assertEquals(globalSeq, 2)
                }
            }
        }
    }
}
