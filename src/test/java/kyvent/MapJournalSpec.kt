package kyvent

import org.jetbrains.spek.api.Spek
import java.time.LocalDateTime
import kotlin.test.assertEquals

class MapJournalSpec: Spek() {
    init {
        given("An empty journal") {
            val journal = MapJournal<CustomerId, CustomerUnitOfWork>(versionExtractor = { uow -> uow.version })
            on("adding a new unitOfWork") {
                val cmd: CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
                val uow = CustomerUnitOfWork(customerCommand = cmd, version = Version(1), events = listOf(CustomerCreated(cmd.customerId)))
                journal.append(cmd.customerId, uow!!)
                it("should result in a journal with the respective entry") {
                    val expected: MutableList<CustomerUnitOfWork> = mutableListOf(uow)
                    val current: MutableList<CustomerUnitOfWork>? = journal.map[cmd.customerId]
                    assertEquals(expected, current)
                }
            }
        }
        given("A journal with one uow") {
            val createCustomerCmd: CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
            val uow1 = CustomerUnitOfWork(customerCommand= createCustomerCmd,
                                          version = Version(1),
                                          events = listOf(CustomerCreated(createCustomerCmd.customerId)))
            val journal = MapJournal(map = mutableMapOf(Pair(createCustomerCmd.customerId, mutableListOf(uow1!!))),
                    versionExtractor = { uow -> uow.version })
            on("adding a new unitOfWork") {
                val activateCmd: ActivateCustomerCmd = ActivateCustomerCmd(CommandId(), createCustomerCmd.customerId)
                val uow2 = CustomerUnitOfWork(customerCommand = activateCmd, version = Version(2), events = listOf(CustomerActivated(LocalDateTime.now())))
                journal.append(createCustomerCmd.customerId, uow2!!)
                it("should result in a journal with the respective first and second entries") {
                    val expected: MutableList<CustomerUnitOfWork> = mutableListOf(uow1, uow2)
                    val current: MutableList<CustomerUnitOfWork>? = journal.map[createCustomerCmd.customerId]
                    assertEquals(expected, current)
                }
            }
        }
    }
}
