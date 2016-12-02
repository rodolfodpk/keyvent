package keyvent

import io.kotlintest.specs.BehaviorSpec
import keyvent.example.*
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MapJournalSpec: BehaviorSpec() {
    init {
        Given("an empty journal") {
            val journal = MapJournal<CustomerId>(versionExtractor = { uow -> uow.version })
            When("adding a new unitOfWork with version=1") {
                val cmd: CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
                val uow = UnitOfWork(command = cmd, version = Version(1), events = listOf(CustomerCreated(cmd.customerId)))
                journal.append(cmd.customerId, uow)
                Then("should result in a journal with the respective entry") {
                    val expected: MutableList<UnitOfWork> = mutableListOf(uow)
                    val current: MutableList<UnitOfWork>? = journal.map[cmd.customerId]
                    assertEquals(expected, current)
                }
            }
        }
        Given("an empty journal") {
            val journal = MapJournal<CustomerId>(versionExtractor = { uow -> uow.version })
            When("adding a new unitOfWork with version =2") {
                val cmd: CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
                val uow = UnitOfWork(command = cmd, version = Version(2), events = listOf(CustomerCreated(cmd.customerId)))
                Then("should throw an exception since version should be 1") {
                    try {
                        journal.append(cmd.customerId, uow)
                        assertTrue(false, "should throw IllegalArgumentException")
                    } catch (e: IllegalArgumentException) {
                    }
                }
            }
        }
        Given("a journal with one uow with version =1") {
            val createCustomerCmd: CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
            val uow1 = UnitOfWork(command= createCustomerCmd,
                                          version = Version(1),
                                          events = listOf(CustomerCreated(createCustomerCmd.customerId)))
            val journal = MapJournal(map = mutableMapOf(Pair(createCustomerCmd.customerId, mutableListOf(uow1))),
                    versionExtractor = { uow -> uow.version })
            When("adding a new unitOfWork with version =2") {
                val localDatTime = LocalDateTime.now()
                val activateCmd: ActivateCustomerCmd = ActivateCustomerCmd(CommandId(), createCustomerCmd.customerId, date = localDatTime)
                val uow2 = UnitOfWork(command = activateCmd, version = Version(2), events = listOf(CustomerActivated(LocalDateTime.now())))
                journal.append(createCustomerCmd.customerId, uow2)
                Then("should result in a journal with the respective first and second entries") {
                    val expected: MutableList<UnitOfWork> = mutableListOf(uow1, uow2)
                    val current: MutableList<UnitOfWork>? = journal.map[createCustomerCmd.customerId]
                    assertEquals(expected, current)
                }
            }
        }
    }
}
