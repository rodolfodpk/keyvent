package keyvent.example

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import io.kotlintest.specs.BehaviorSpec
import keyvent.CommandId
import keyvent.ValuesService
import keyvent.UnitOfWork
import keyvent.Version
import java.time.LocalDateTime
import kotlin.test.assertEquals

class HandleCustomerCommandsSpec : BehaviorSpec() {
    init {
        Given("an empty Customer with version 0") {
            val state = Customer()
            val version = Version(0)
            When("a createCommand is issued") {
                val cmd = CreateCustomerCmd(customerId = CustomerId())
                val result = handleCustomerCommands.invoke(state, version, cmd, stateTransitionFn)
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
            val serviceMock = mock<ValuesService> {
                on { now() } doReturn activatedOn
            }
            val state = Customer(serviceMock, customerId = CustomerId(), name="customer1", active = false, activatedSince = null)
            val version = Version(1)
            When("an activateCommand is issued") {
                val cmd = ActivateCustomerCmd(CommandId(), customerId = state.customerId!!)
                val result = handleCustomerCommands.invoke(state, version, cmd, stateTransitionFn)
                val uow: UnitOfWork = result.get()
                Then("a proper UnitOfWork is generated") {
                    assertEquals(uow.command, cmd)
                    assertEquals(uow.version, Version(2))
                    assertEquals(uow.events.first(), CustomerActivated(activatedOn))
                    verify(serviceMock).now()
                }
            }
        }
        Given("a non active Customer with version 1") {
            // just a service mock
            val activatedOn = LocalDateTime.now()
            val serviceMock = mock<ValuesService> {
                on { now() } doReturn activatedOn
            }
            val state = Customer(serviceMock, customerId = CustomerId(), name="customer1", active = false, activatedSince = null)
            val version = Version(1)
            When("a createCommand with same customerId is issued") {
                val cmd = CreateCustomerCmd(customerId = state.customerId!!)
                val result = handleCustomerCommands.invoke(state, version, cmd, stateTransitionFn)
                Then("result must be an error with an IllegalArgumentException") {
                    val exception = result.component2()
                    assertEquals(exception!!.javaClass.name, IllegalArgumentException::class.java.name)
                    verifyNoMoreInteractions(serviceMock)
                }

            }
        }

    }
}
