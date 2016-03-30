package keyvent.core.impl

import javaslang.Tuple2
import javaslang.collection.HashMap
import javaslang.collection.List
import javaslang.collection.Map
import keyvent.core.data.CommandIdVal
import keyvent.core.data.UnitOfWorkIdVal
import keyvent.core.data.Version
import keyvent.core.data.VersionVal
import keyvent.sample.customer.*
import org.jetbrains.spek.api.Spek
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

class SimpleJournalSpec : Spek() {

    val createCustomerCmd: CreateCustomerCmd = CreateCustomerCmd.builder()
            .commandId(CommandIdVal.of(UUID.randomUUID()))
            .customerId(CustomerIdVal.of(UUID.randomUUID())).build()

    val uow1 = CustomerUow.builder()
            .id(UnitOfWorkIdVal.of(UUID.randomUUID()))
            .command(createCustomerCmd)
            .version(VersionVal.of(1))
            .events(javaslang.collection.List.of(CustomerCreatedEvt.builder().customerId(createCustomerCmd.customerId()).build()))
            .instant(Instant.now())
            .build()

    val activateCmd = ActivateCustomerCmd.builder().commandId(CommandIdVal.of(UUID.randomUUID()))
            .customerId(createCustomerCmd.customerId()).build()

    val uow2 = CustomerUow.builder().id(UnitOfWorkIdVal.of(UUID.randomUUID()))
            .command(activateCmd)
            .version(VersionVal.of(2L))
            .events(javaslang.collection.List.of(CustomerActivatedEvt.builder()
                    .customerId(activateCmd.customerId())
                    .date(LocalDateTime.now()).build()))
            .instant(Instant.now())
            .build()

        init {
            given("An empty journal") {
                val map: Map<CustomerSchema.CustomerId, List<Tuple2<CustomerUow, Version>>> = HashMap.empty()
                val journal = SimpleJournal<CustomerSchema.CustomerId, CustomerUow>(map)
                on("adding a new unitOfWork with version=1") {
                   val globalSeq: Long? = journal.append(createCustomerCmd.customerId(), uow1, Version.of(1))
                    it("should result in a journal with the respective entry") {
                        val expected: List<Tuple2<CustomerUow, Version>> = List.of(Tuple2(uow1, Version.of(1)))
                        val current: List<Tuple2<CustomerUow, Version>> = journal.map().get(createCustomerCmd.customerId()).get()
                        assertEquals(expected, current)
                    }
                    it("should result in a globalSequence = 1") {
                        assertEquals(globalSeq, 1)
                    }
                }
            }
         }

    //    init {
//        given("An empty journal") {
//            val journal = SimpleJournal<CustomerSchema.CustomerId, CustomerUow>(versionExtractor, mutableMapOf())
//            on("adding a new unitOfWork with version=1") {
//               val globalSeq = journal.append(createCustomerCmd.customerId, uow1)
//                it("should result in a journal with the respective entry") {
//                    val expected: MutableList<CustomerUow> = mutableListOf(uow1)
//                    val current: MutableList<CustomerUow>? = journal.map[createCustomerCmd.customerId]
//                    assertEquals(expected, current)
//                }
//                it("should result in a globalSequence = 1") {
//                    assertEquals(globalSeq, 1)
//                }
//            }
//        }
//        given("An empty journal") {
//            val journal = SimpleJournal<CustomerSchema.CustomerId, CustomerUow>(versionExtractor, mutableMapOf())
//            on("adding a new unitOfWork with version =2") {
//                it("should throw an exception since version should be 1") {
//                    try {
//                        journal.append(createCustomerCmd.customerId, uow2)
//                        assertTrue(false, "should throw IllegalArgumentException")
//                    } catch (e: IllegalArgumentException) {
//                    }
//                }
//            }
//        }
//        given("A journal with one uow with version =1") {
//            val journal = SimpleJournal<CustomerSchema.CustomerId, CustomerUow>(versionExtractor, mutableMapOf())
//            journal.globalEventSequence = 1
//            journal.map.put(createCustomerCmd.customerId, mutableListOf(uow1))
//            on("adding a new unitOfWork with version =2") {
//                val globalSeq = journal.append(createCustomerCmd.customerId, uow2)
//                it("should result in a journal with the respective first and second entries") {
//                    val expected: List<CustomerUow> = listOf(uow1, uow2)
//                    val current: List<CustomerUow>? = journal.map[createCustomerCmd.customerId]
//                    assertEquals(expected, current)
//                }
//                it("should result in a globalSequence = 2") {
//                    assertEquals(globalSeq, 2)
//                }
//            }
//        }
//    }
}
