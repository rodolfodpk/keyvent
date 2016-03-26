package keyvent.core.impl

import keyvent.core.kotlin.*
import keyvent.core.kotlin.SimpleEventRepository
import org.jetbrains.spek.api.Spek
import java.time.LocalDateTime
import java.util.function.Function
import kotlin.test.assertEquals

class SimpleRepositorySpec : Spek() {

    val createCustomerCmd: CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
    val uow1 = CustomerUnitOfWork(customerCommand = createCustomerCmd,
            version = Version(1),
            events = listOf(CustomerCreated(createCustomerCmd.customerId)))

    val activateCmd: ActivateCustomerCmd = ActivateCustomerCmd(CommandId(), createCustomerCmd.customerId)
    val uow2 = CustomerUnitOfWork(customerCommand = activateCmd, version = Version(2), events = listOf(CustomerActivated(LocalDateTime.now())))

    val versionExtractor = Function<CustomerUnitOfWork, Long> { uow -> uow.version.version }

    init {
        given("An empty event repo") {
            val eventRepo = keyvent.core.kotlin.SimpleEventRepository<CustomerId, CustomerUnitOfWork>(versionExtractor, mutableMapOf())
            on("querying for a non existent id ") {
                val customerId = CustomerId()
                val uowList = eventRepo.eventsAfter(customerId, 0, Int.MAX_VALUE)
                it("should result in an empty list") {
                    assertEquals(uowList, mutableListOf())
                }
            }
        }
        given("An event repo with one uow") {
            val eventRepo = keyvent.core.kotlin.SimpleEventRepository<CustomerId, CustomerUnitOfWork>(versionExtractor, mutableMapOf())
            eventRepo.map.put(createCustomerCmd.customerId, mutableListOf(uow1))
            on("querying for an existent id ") {
                val current: List<CustomerUnitOfWork> = eventRepo.eventsAfter(createCustomerCmd.customerId, 0)
                it("should result in a list with the respective uow") {
                    val expected: MutableList<CustomerUnitOfWork> = mutableListOf(uow1)
                    assertEquals(expected, current)
                }
            }
        }
        given("An event repo with a couple of uow versioned as 1 and 2") {
            val eventRepo = keyvent.core.kotlin.SimpleEventRepository<CustomerId, CustomerUnitOfWork>(versionExtractor, mutableMapOf())
            eventRepo.map.put(createCustomerCmd.customerId, mutableListOf(uow1, uow2))
            on("querying for an existent id with version greater than 1") {
                val current: List<CustomerUnitOfWork> = eventRepo.eventsAfter(createCustomerCmd.customerId, 1)
                it("should result in a list with the uow 2") {
                    val expected: MutableList<CustomerUnitOfWork> = mutableListOf(uow2)
                    assertEquals(expected, current)
                }
            }
        }
        given("An event repo with a couple of uow versioned as 1 and 2") {
            val eventRepo = keyvent.core.kotlin.SimpleEventRepository<CustomerId, CustomerUnitOfWork>(versionExtractor, mutableMapOf())
            eventRepo.map.put(createCustomerCmd.customerId, mutableListOf(uow1, uow2))
            on("querying for an existent id with version greater than 0 and limit =1") {
                val current: List<CustomerUnitOfWork> = eventRepo.eventsAfter(createCustomerCmd.customerId, 0, 1)
                it("should result in a list with the uow '") {
                    val expected: MutableList<CustomerUnitOfWork> = mutableListOf(uow1)
                    assertEquals(expected, current)
                }
            }
        }
        given("An event repo with a couple of uow versioned as 1 and 2") {
            val eventRepo = keyvent.core.kotlin.SimpleEventRepository<CustomerId, CustomerUnitOfWork>(versionExtractor, mutableMapOf())
            eventRepo.map.put(createCustomerCmd.customerId, mutableListOf(uow1, uow2))
            on("querying for lastVersion") {
                val lastVersion = eventRepo.lastVersion(createCustomerCmd.customerId)
                it("should result in 2") {
                    assertEquals(lastVersion, 2)
                }
            }
        }

    }

}
