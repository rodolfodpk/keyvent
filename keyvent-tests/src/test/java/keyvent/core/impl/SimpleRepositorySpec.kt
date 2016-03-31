package keyvent.core.impl

import javaslang.Tuple2
import javaslang.collection.HashMap
import javaslang.collection.List
import javaslang.collection.Map
import javaslang.control.Option
import keyvent.core.data.CommandId
import keyvent.core.data.UnitOfWorkId
import keyvent.core.data.Version
import keyvent.sample.customer.*
import org.jetbrains.spek.api.Spek
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

class SimpleRepositorySpec : Spek() {

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
        given("An empty event repo") {
            val map: Map<CustomerSchema.CustomerId, List<Tuple2<CustomerUow, Version>>> = HashMap.empty()
            val eventRepo = SimpleEventRepository<CustomerSchema.CustomerId, CustomerUow>(map)
            on("querying for a non existent id ") {
                val customerId = CustomerIdVal.of(UUID.randomUUID())
                val uowList = eventRepo.eventsAfter(customerId, Version(0), Int.MAX_VALUE)
                it("should result in an empty list") {
                    assertEquals(uowList, List.empty())
                }
            }
        }
        given("An event repo with one uow") {
            val map: Map<CustomerSchema.CustomerId, List<Tuple2<CustomerUow, Version>>> =
                    HashMap.of(createCustomerCmd.customerId(), List.of(Tuple2(uow1, Version(1))))
            val eventRepo = SimpleEventRepository<CustomerSchema.CustomerId, CustomerUow>(map)
            on("querying for an existent id ") {
                val current:List<CustomerUow> = eventRepo.eventsAfter(createCustomerCmd.customerId(), Version(0))
                it("should result in a list with the respective uow") {
                    val expected: List<CustomerUow> = List.of(uow1)
                    assertEquals(expected, current)
                }
            }
        }
        given("An event repo with a couple of uow versioned as 1 and 2") {
            val map: Map<CustomerSchema.CustomerId, List<Tuple2<CustomerUow, Version>>> =
                    HashMap.of(createCustomerCmd.customerId(),
                            List.of(Tuple2(uow1, Version(1)), Tuple2(uow2, Version(2))))
            val eventRepo = SimpleEventRepository<CustomerSchema.CustomerId, CustomerUow>(map)
            on("querying for an existent id with version greater than 1") {
                val current: List<CustomerUow> = eventRepo.eventsAfter(createCustomerCmd.customerId(), Version(1))
                it("should result in a list with the uow 2") {
                    val expected: List<CustomerUow> = List.of(uow2)
                    assertEquals(expected, current)
                }
            }
            on("querying for an existent id with version greater than 0 and limit =1") {
                val current: List<CustomerUow> = eventRepo.eventsAfter(createCustomerCmd.customerId(), Version(0), 1)
                it("should result in a list with the uow 1") {
                    val expected: List<CustomerUow> = List.of(uow1)
                    assertEquals(expected, current)
                }
            }
            on("querying for lastVersion") {
                val lastVersion: Option<Version> = eventRepo.lastVersion(createCustomerCmd.customerId())
                it("should result in 2") {
                    assertEquals(lastVersion.get(), Version(2))
                }
            }
        }
    }

}
