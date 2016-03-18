package kyvent

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

class MapJournalSpec: Spek() {
    init {
        given("An empty journal") {
            val journal = MapJournal<CustomerId, CustomerUnitOfWork>(versionExtractor = { uow -> uow.version })
            on("adding a new unitOfWork") {
                val cmd: CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
                val snapshot: Snapshot<Customer> = Snapshot(Customer(), Version(0))
                val uow = handleCustomerCommands(snapshot, cmd, applyEventOnCustomer)
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
            val snapshot: Snapshot<Customer> = Snapshot(Customer(), Version(0))
            val uow1 = handleCustomerCommands(snapshot, createCustomerCmd, applyEventOnCustomer)
            val expected: MutableList<CustomerUnitOfWork> = mutableListOf(uow1!!)
            val journal = MapJournal(map = mutableMapOf(Pair(createCustomerCmd.customerId, expected)),
                    versionExtractor = { uow -> uow.version })
            on("adding a new unitOfWork") {
                val activateCmd: ActivateCustomerCmd = ActivateCustomerCmd(CommandId(), createCustomerCmd.customerId)
                val snapshot = Snapshot(Customer(customerId = createCustomerCmd.customerId, name = null,
                        active = false, activatedSince = null), Version(1))
                val uow2 = handleCustomerCommands(snapshot, activateCmd, applyEventOnCustomer)
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
