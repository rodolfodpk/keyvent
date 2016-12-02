package keyvent.example

import com.github.kittinunf.result.Result
import keyvent.*
import java.time.LocalDateTime
import java.util.*

// customer value objects

data class CustomerId(val uuid: UUID = UUID.randomUUID())

// customer commands

interface CustomerCommand : Command {
    val customerId: CustomerId
}

data class CreateCustomerCmd(override val commandId: CommandId = CommandId(),
                             override val customerId: CustomerId) : CustomerCommand

data class ActivateCustomerCmd(override val commandId: CommandId = CommandId(),
                               override val customerId: CustomerId) : CustomerCommand

data class CreateActivatedCustomerCmd(override val commandId: CommandId,
                                      override val customerId: CustomerId) : CustomerCommand

// customer events

interface CustomerEvent : Event

data class CustomerCreated(val customerId: CustomerId) : CustomerEvent

data class CustomerActivated(val date: LocalDateTime) : CustomerEvent

// aggregate root

data class Customer(var genValService: GeneratedValuesService, val customerId: CustomerId?, val name: String?, val active: Boolean, val activatedSince : LocalDateTime?) : AggregateRoot {

    constructor() : this(GeneratedValuesService(), null, null, false, null)

    // behaviour

    fun create(customerId: CustomerId) : List<CustomerEvent> {
        // check if new then
        require(this.customerId==null, {"customer already exists! customerId should be null but is $this.customerId"})
        return listOf(CustomerCreated(customerId))
    }

    fun activate() : List<CustomerEvent> {
        return listOf(CustomerActivated(genValService.now()))
    }

}

// commands routing and execution function

val handleCustomerCommands : (Customer, Version, CustomerCommand, (CustomerEvent, Customer) -> Customer) ->
        Result<UnitOfWork, Exception> = { aggregateRoot, version, command, stateTransitionFn ->
    when(command) {
        is CreateCustomerCmd ->
            Result.of { UnitOfWork(command = command, version = version.nextVersion(),
                    events = aggregateRoot.create(command.customerId)) }
        is ActivateCustomerCmd -> Result.of { UnitOfWork(command = command, version = version.nextVersion(),
                events = aggregateRoot.activate())}
        is CreateActivatedCustomerCmd -> {
            val events = with(StateTransitionsTracker(aggregateRoot, stateTransitionFn)) {
                apply(aggregateRoot.create(command.customerId))
                apply(aggregateRoot.activate())
                collectedEvents()
            }
            Result.of { UnitOfWork(command = command, version = version.nextVersion(), events = events) }

        }
        else -> Result.error(IllegalArgumentException("Unknown command " + command.javaClass.simpleName))
    }
}


// events routing and execution function

val stateTransitionFn: (CustomerEvent, Customer) -> Customer = { event, state ->
    when(event) {
        is CustomerCreated -> state.copy(customerId = event.customerId)
        is CustomerActivated -> state.copy(active = true, activatedSince = event.date)
        else -> state
    }
}

// projection TODO