package keyvent

import io.kotlintest.specs.BehaviorSpec
import keyvent.example.*
import java.time.LocalDateTime
import kotlin.test.assertEquals

class EventRepositorySpec: BehaviorSpec() {
    init {
        Given("An empty event repo") {
            val eventRepo = MapEventRepository<CustomerId, UnitOfWork>(
                    versionExtractor = { uow -> uow.version})
            When("querying for a non existent id ") {
                val customerId = CustomerId()
                val uowList = eventRepo.eventsAfter(customerId, Version(0))
                Then("should result in an empty list") {
                    assertEquals(uowList, mutableListOf())
                }
            }
        }
        Given("An event repo with one uow") {
            val createCustomerCmd: CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
            val uow1 = UnitOfWork(command = createCustomerCmd,
                                          version = Version(1),
                                          events = listOf(CustomerCreated(createCustomerCmd.customerId)))
            val eventRepo : MapEventRepository<CustomerId, UnitOfWork> =
                    MapEventRepository(map = mutableMapOf(Pair(createCustomerCmd.customerId, mutableListOf(uow1))),
                    versionExtractor = { uow -> uow.version })
            When("querying for an existent id ") {
                Then("should result in a list with the respective uow") {
                    val expected: MutableList<UnitOfWork> = mutableListOf(uow1)
                    val current: List<UnitOfWork> = eventRepo.eventsAfter(createCustomerCmd.customerId, Version(0))
                    assertEquals(expected, current)
                }
            }
        }
        Given("An event repo with a couple of uow versioned as 1 and 2") {
            val createCustomerCmd: CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
            val uow1 = UnitOfWork(command= createCustomerCmd,
                    version = Version(1),
                    events = listOf(CustomerCreated(createCustomerCmd.customerId)))
            val activateCmd: ActivateCustomerCmd = ActivateCustomerCmd(CommandId(), createCustomerCmd.customerId, LocalDateTime.now())
            val uow2 = UnitOfWork(command = activateCmd, version = Version(2), events = listOf(CustomerActivated(LocalDateTime.now())))
            val eventRepo : MapEventRepository<CustomerId, UnitOfWork> =
                    MapEventRepository(map = mutableMapOf(Pair(createCustomerCmd.customerId, mutableListOf(uow1, uow2))),
                            versionExtractor = { uow -> uow.version })
            When("querying for an existent id with version greater than 1") {
                Then("should result in a list with the uow 2") {
                    val expected: MutableList<UnitOfWork> = mutableListOf(uow2)
                    val current: List<UnitOfWork> = eventRepo.eventsAfter(createCustomerCmd.customerId, Version(1))
                    assertEquals(expected, current)
                }
            }
        }
    }
}
