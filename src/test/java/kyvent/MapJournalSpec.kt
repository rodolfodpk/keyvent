package kyvent

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

class MapJournalSpec: Spek() {
    init {
        given("An empty journal") {
            val journal = MapJournal<CustomerId, CustomerUnitOfWork>(versionExtractor = {uow -> uow.version})
            on("adding a new unitOfWork") {
                val cmd : CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
                val snapshot : Snapshot<Customer> = Snapshot(Customer(), Version(0))
                val uow = handleCustomerCommands(snapshot, cmd, applyEventOnCustomer)
                journal.append(cmd.customerId, uow!!)
                it("should result in a journal with the respective entry") {
                    val expected: MutableList<CustomerUnitOfWork> = mutableListOf(uow)
                    val current: MutableList<CustomerUnitOfWork>? = journal.map[cmd.customerId]
                    assertEquals(expected, current)
                }
            }
        }
    }
}