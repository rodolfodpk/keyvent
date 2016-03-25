package keyvent.core.impl.mem

import keyvent.core.kotlin.*
import org.jetbrains.spek.api.Spek
import java.time.LocalDateTime
import java.util.function.Function
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SimpleJournalSpec : Spek() {

    val createCustomerCmd: CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
    val uow1 = CustomerUnitOfWork(customerCommand = createCustomerCmd,
            version = Version(1),
            events = listOf(CustomerCreated(createCustomerCmd.customerId)))

    val activateCmd: ActivateCustomerCmd = ActivateCustomerCmd(CommandId(), createCustomerCmd.customerId)
    val uow2 = CustomerUnitOfWork(customerCommand = activateCmd, version = Version(2), events = listOf(CustomerActivated(LocalDateTime.now())))

    val versionExtractor = Function<CustomerUnitOfWork, Long> { uow -> uow.version.version }

    init {
        given("An empty journal") {
            val journal = SimpleJournal<CustomerId, CustomerUnitOfWork>(versionExtractor, mutableMapOf())
            on("adding a new unitOfWork with version=1") {
               val globalSeq = journal.append(createCustomerCmd.customerId, uow1)
                it("should result in a journal with the respective entry") {
                    val expected: MutableList<CustomerUnitOfWork> = mutableListOf(uow1)
                    val current: MutableList<CustomerUnitOfWork>? = journal.map[createCustomerCmd.customerId]
                    assertEquals(expected, current)
                }
                it("should result in a globalSequence = 1") {
                    assertEquals(globalSeq, 1)
                }
            }
        }
        given("An empty journal") {
            val journal = SimpleJournal<CustomerId, CustomerUnitOfWork>(versionExtractor, mutableMapOf())
            on("adding a new unitOfWork with version =2") {
                it("should throw an exception since version should be 1") {
                    try {
                        journal.append(createCustomerCmd.customerId, uow2)
                        assertTrue(false, "should throw IllegalArgumentException")
                    } catch (e: IllegalArgumentException) {
                    }
                }
            }
        }
        given("A journal with one uow with version =1") {
            val journal = SimpleJournal<CustomerId, CustomerUnitOfWork>(versionExtractor, mutableMapOf())
            journal.globalEventSequence = 1
            journal.map.put(createCustomerCmd.customerId, mutableListOf(uow1))
            on("adding a new unitOfWork with version =2") {
                val globalSeq = journal.append(createCustomerCmd.customerId, uow2)
                it("should result in a journal with the respective first and second entries") {
                    val expected: List<CustomerUnitOfWork> = listOf(uow1, uow2)
                    val current: List<CustomerUnitOfWork>? = journal.map[createCustomerCmd.customerId]
                    assertEquals(expected, current)
                }
                it("should result in a globalSequence = 2") {
                    assertEquals(globalSeq, 2)
                }
            }
        }
    }
}
