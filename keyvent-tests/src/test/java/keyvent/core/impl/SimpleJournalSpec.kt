package keyvent.core.impl

import javaslang.Tuple2
import javaslang.collection.HashMap
import javaslang.collection.List
import javaslang.collection.Map
import keyvent.core.data.CommandId
import keyvent.core.data.UnitOfWorkId
import keyvent.core.data.Version
import keyvent.sample.customer.*
import org.jetbrains.spek.api.Spek
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SimpleJournalSpec : Spek() {

    val createCustomerCmd: CreateCustomerCmd = CreateCustomerCmd.builder()
            .commandId(CommandId())
            .customerId(CustomerIdVal.of(UUID.randomUUID())).build()

    val uow1 = CustomerUow.builder()
            .id(UnitOfWorkId())
            .command(createCustomerCmd)
            .version(Version(1))
            .events(List.of(CustomerCreatedEvt.builder().customerId(createCustomerCmd.customerId()).build()))
            .instant(Instant.now())
            .build()

    val activateCmd = ActivateCustomerCmd.builder().commandId(CommandId())
            .customerId(createCustomerCmd.customerId()).build()

    val uow2 = CustomerUow.builder().id(UnitOfWorkId())
            .command(activateCmd)
            .version(Version(2L))
            .events(List.of(CustomerActivatedEvt.builder()
                    .customerId(activateCmd.customerId())
                    .date(LocalDateTime.now()).build()))
            .instant(Instant.now())
            .build()

    init {
        given("An empty journal") {
            val map: Map<CustomerSchema.CustomerId, List<Tuple2<CustomerUow, Version>>> = HashMap.empty()
            val journal = SimpleJournal<CustomerSchema.CustomerId, CustomerUow>(map)
            on("adding a new unitOfWork with version=1") {
                val globalSeq: Long? = journal.append(createCustomerCmd.customerId(), uow1, Version(1))
                it("should result in a journal with the respective entry") {
                    val expected: List<Tuple2<CustomerUow, Version>> = List.of(Tuple2(uow1, Version(1)))
                    val current: List<Tuple2<CustomerUow, Version>> = journal.map().get(createCustomerCmd.customerId()).get()
                    assertEquals(expected, current)
                }
                it("should result in a globalSequence = 1") {
                    assertEquals(globalSeq, 1)
                }
            }
        }
        given("An empty journal") {
            val map: Map<CustomerSchema.CustomerId, List<Tuple2<CustomerUow, Version>>> = HashMap.empty()
            val journal = SimpleJournal<CustomerSchema.CustomerId, CustomerUow>(map)
            on("adding a new unitOfWork with version =2") {
                it("should throw an exception since version should be 1") {
                    try {
                        journal.append(createCustomerCmd.customerId(), uow2, Version(2))
                        assertTrue(false, "should throw IllegalArgumentException")
                    } catch (e: IllegalArgumentException) {
                    }
                }
            }
        }
        given("A journal with one uow with version =1") {
            val map: Map<CustomerSchema.CustomerId, List<Tuple2<CustomerUow, Version>>> =
                    HashMap.of(createCustomerCmd.customerId(), List.of(Tuple2(uow1, Version.firstVersion())))
            val journal = SimpleJournal<CustomerSchema.CustomerId, CustomerUow>(map)
            on("adding a new unitOfWork with version =2") {
                val globalSeq = journal.append(createCustomerCmd.customerId(), uow2, Version(2))
                it("should result in a journal with the respective first and second entries") {
                    val expected: List<Tuple2<CustomerUow, Version>> = List.of(Tuple2(uow1, Version(1)), Tuple2(uow2, Version(2)))
                    val current: List<Tuple2<CustomerUow, Version>> = journal.map()[createCustomerCmd.customerId()].get()
                    assertEquals(expected, current)
                }
                it("should result in a globalSequence = 2") {
                    assertEquals(globalSeq, 2)
                }
            }
        }
    }
}
