package keyvent

import org.jetbrains.spek.api.Spek
import java.time.LocalDateTime
import kotlin.test.assertEquals

class EventRepositorySpec: Spek() {
    init {
        given("An empty event repo") {
            val eventRepo = MapEventRepository<CustomerId, CustomerUnitOfWork>(
                    versionExtractor = { uow -> uow.version})
            on("querying for a non existent id ") {
                val customerId = CustomerId()
                val uowList = eventRepo.eventsAfter(customerId, Version(0))
                it("should result in an empty list") {
                    assertEquals(uowList, mutableListOf())
                }
            }
        }
        given("An event repo with one uow") {
            val createCustomerCmd: CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
            val uow1 = CustomerUnitOfWork(customerCommand= createCustomerCmd,
                                          version = Version(1),
                                          events = listOf(CustomerCreated(createCustomerCmd.customerId)))
            val eventRepo : MapEventRepository<CustomerId, CustomerUnitOfWork> =
                    MapEventRepository(map = mutableMapOf(Pair(createCustomerCmd.customerId, mutableListOf(uow1))),
                    versionExtractor = { uow -> uow.version })
            on("querying for an existent id ") {
                it("should result in a list with the respective uow") {
                    val expected: MutableList<CustomerUnitOfWork> = mutableListOf(uow1)
                    val current: List<CustomerUnitOfWork> = eventRepo.eventsAfter(createCustomerCmd.customerId, Version(0))
                    assertEquals(expected, current)
                }
            }
        }
        given("An event repo with a couple of uow versioned as 1 and 2") {
            val createCustomerCmd: CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
            val uow1 = CustomerUnitOfWork(customerCommand= createCustomerCmd,
                    version = Version(1),
                    events = listOf(CustomerCreated(createCustomerCmd.customerId)))
            val activateCmd: ActivateCustomerCmd = ActivateCustomerCmd(CommandId(), createCustomerCmd.customerId, LocalDateTime.now())
            val uow2 = CustomerUnitOfWork(customerCommand = activateCmd, version = Version(2), events = listOf(CustomerActivated(LocalDateTime.now())))
            val eventRepo : MapEventRepository<CustomerId, CustomerUnitOfWork> =
                    MapEventRepository(map = mutableMapOf(Pair(createCustomerCmd.customerId, mutableListOf(uow1, uow2))),
                            versionExtractor = { uow -> uow.version })
            on("querying for an existent id with version greater than 1") {
                it("should result in a list with the uow 2") {
                    val expected: MutableList<CustomerUnitOfWork> = mutableListOf(uow2)
                    val current: List<CustomerUnitOfWork> = eventRepo.eventsAfter(createCustomerCmd.customerId, Version(1))
                    assertEquals(expected, current)
                }
            }
        }
    }
}
