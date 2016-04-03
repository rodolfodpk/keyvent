package keyvent.flows.commands

import javaslang.Tuple2
import javaslang.collection.HashMap.empty
import javaslang.collection.List
import javaslang.collection.Map
import javaslang.control.Option
import keyvent.sample.customer.CustomerSchema
import keyvent.sample.customer.CustomerSchema.*
import org.jetbrains.spek.api.Spek
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

class SimpleRepositorySpec : Spek() {

    val createCustomerCmd: CustomerSchema.CreateCustomer = CreateCustomer.builder()
            .commandId(CommandId(UUID.randomUUID()))
            .customerId(CustomerId(UUID.randomUUID())).build()

    val uow1 = CustomerUow.builder()
            .id(UnitOfWorkId())
            .command(createCustomerCmd)
            .version(1)
            .events(List.of(CustomerCreated.builder().customerId(createCustomerCmd.getCustomerId()).build()))
            .instant(Instant.now())
            .build()

    val activateCmd = ActivateCustomer.builder().commandId(CommandId(UUID.randomUUID()))
            .customerId(createCustomerCmd.getCustomerId()).build()

    val uow2 = CustomerUow.builder().id(UnitOfWorkId())
            .command(activateCmd)
            .version(2)
            .events(List.of(CustomerActivated.builder()
                    .customerId(activateCmd.getCustomerId())
                    .date(LocalDateTime.now()).build()))
            .instant(Instant.now())
            .build()

    init {
        given("An empty event repo") {
            val map: Map<CustomerSchema.CustomerId, List<Tuple2<CustomerSchema.CustomerUow, Long>>> = empty()
            val eventRepo = SimpleEventRepository<CustomerSchema.CustomerId, CustomerSchema.CustomerUow>(map)
            on("querying for a non existent id ") {
                val customerId = CustomerId(UUID.randomUUID())
                val uowList = eventRepo.eventsAfter(customerId, 0, Int.MAX_VALUE)
                it("should result in an empty list") {
                    assertEquals(uowList, List.empty())
                }
            }
        }
        given("An event repo with one uow") {
            val map: Map<CustomerSchema.CustomerId, List<Tuple2<CustomerUow, Long>>> = empty()
            val eventRepo = SimpleEventRepository<CustomerSchema.CustomerId, CustomerUow>(
                    map.put(createCustomerCmd.getCustomerId(), List.of(Tuple2(uow1, 1L))))
            on("querying for an existent id ") {
                val current: List<CustomerUow> = eventRepo.eventsAfter(createCustomerCmd.getCustomerId(), 0L)
                it("should result in a list with the respective uow") {
                    val expected: List<CustomerUow> = List.of(uow1)
                    assertEquals(expected, current)
                }
            }
        }
        given("An event repo with a couple of uow versioned as 1 and 2") {
            val map: Map<CustomerSchema.CustomerId, List<Tuple2<CustomerUow, Long>>> = empty()
            val eventRepo = SimpleEventRepository<CustomerSchema.CustomerId, CustomerUow>(
                    map.put(createCustomerCmd.getCustomerId(), List.of(Tuple2(uow1, 1L), Tuple2(uow2, 2L))))
            on("querying for an existent id with version greater than 1") {
                val current: List<CustomerUow> = eventRepo.eventsAfter(createCustomerCmd.getCustomerId(), 1L)
                it("should result in a list with the uow 2") {
                    val expected: List<CustomerUow> = List.of(uow2)
                    assertEquals(expected, current)
                }
            }
            on("querying for an existent id with version greater than 0 and limit =1") {
                val current: List<CustomerUow> = eventRepo.eventsAfter(createCustomerCmd.getCustomerId(), 0L, 1)
                it("should result in a list with the uow 1") {
                    val expected: List<CustomerUow> = List.of(uow1)
                    assertEquals(expected, current)
                }
            }
            on("querying for lastLong") {
                val lastLong: Option<Long> = eventRepo.lastVersion(createCustomerCmd.getCustomerId())
                it("should result in 2") {
                    assertEquals(lastLong.get(), 2L)
                }
            }
        }
    }

}
