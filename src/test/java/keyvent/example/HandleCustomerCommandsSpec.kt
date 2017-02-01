package keyvent.example

import com.nhaarman.mockito_kotlin.*
import io.kotlintest.specs.BehaviorSpec
import keyvent.CommandId
import keyvent.UnitOfWork
import keyvent.Version
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

class HandleCustomerCommandsSpec : BehaviorSpec() {
    init {
        Given("an empty Customer with version 0") {
            val customer = Customer()
            val version = Version(0)
            When("a createCommand is issued") {
                val cmd = CreateCustomerCmd(customerId = CustomerId())
                val result = handleCustomerCommandsFn.invoke(customer, version, cmd, stateTransitionFn)
                val uow: UnitOfWork = result.get()
                Then("a proper UnitOfWork is generated") {
                    assertEquals(uow.command, cmd)
                    assertEquals(uow.version, Version(1))
                    assertEquals(uow.events.first(), CustomerCreated(cmd.customerId))
                }
            }
        }
        Given("a non active Customer with version 1") {
            // just a service mock
            val activatedOn = LocalDateTime.now()
            val uuid = UUID.randomUUID()
            val serviceMock = mock<SupplierHelperService> {
                on { now() } doReturn activatedOn
                on { uuId() } doReturn uuid
            }
            val customer = Customer(customerId = CustomerId(uuid), name = "customer1", active = false,
                    activatedSince = null, deactivatedSince = null)
            customer.genValService = serviceMock
            val version = Version(1)
            When("an activateCommand is issued") {
                val cmd = ActivateCustomerCmd(CommandId(uuid), customerId = customer.customerId!!)
                val result = handleCustomerCommandsFn.invoke(customer, version, cmd, stateTransitionFn)
                val uow: UnitOfWork = result.get()
                Then("a proper UnitOfWork is generated") {
                    val expectedCmd = DeactivateCustomerCmd(commandId = CommandId(uuid), customerId = CustomerId(uuid))
                    assertEquals(uow.command, cmd)
                    assertEquals(uow.version, Version(2))
                    assertEquals(uow.events.first(), CustomerActivated(activatedOn))
                    assertEquals(uow.events.last(), DeactivatedCmdScheduled(expectedCmd, activatedOn.plusDays(1)))
                }
                Then("now() is called on  serviceMock") {
                    verify(serviceMock, times(2)).now() // one for activate other for deactivate schedule date
                }
            }
        }
        Given("a non active Customer with version 1") {
            // just a service mock
            val activatedOn = LocalDateTime.now()
            val serviceMock = mock<SupplierHelperService> {
                on { now() } doReturn activatedOn
            }
            val customer = Customer(customerId = CustomerId(), name = "customer1", active = false,
                    activatedSince = null, deactivatedSince = null)
            customer.genValService = serviceMock
            val version = Version(1)
            When("a createCommand with same customerId is issued") {
                val cmd = CreateCustomerCmd(customerId = customer.customerId!!)
                val result = handleCustomerCommandsFn.invoke(customer, version, cmd, stateTransitionFn)
                Then("result must be an error with an IllegalArgumentException") {
                    val exception = result.component2()
                    assertEquals(exception!!.javaClass.name, IllegalArgumentException::class.java.name)
                }
                Then("nothing is called on serviceMock") {
                    verifyNoMoreInteractions(serviceMock)
                }
            }
        }
        Given("an empty Customer with version 1") {
            val customer = Customer()
            val version = Version(1)
            val cmd = CreateCustomerCmd(customerId = CustomerId())
            When("a createCommand is issued") {
                val result = handleCustomerCommandsFn.invoke(customer, version, cmd, stateTransitionFn)
                Then("result must be an error with an IllegalArgumentException") {
                    val exception = result.component2()
                    assertEquals(exception!!.javaClass.name, IllegalArgumentException::class.java.name)
                }
            }
        }
        Given("an empty Customer with version 0") {
            val customer = Customer()
            val version = Version(0)
            val cmd = ForeignCommand(commandId = CommandId(), customerId = CustomerId())
            When("an a foreign Command is issued") {
                val result = handleCustomerCommandsFn.invoke(customer, version, cmd, stateTransitionFn)
                Then("result must be an error with an IllegalArgumentException") {
                    val exception = result.component2()
                    assertEquals(exception!!.javaClass.name, IllegalArgumentException::class.java.name)
                }
            }
        }


    }
}

data class ForeignCommand(override val commandId: CommandId,
                          override val customerId: CustomerId) : CustomerCommand