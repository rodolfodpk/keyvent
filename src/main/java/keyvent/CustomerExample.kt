package keyvent

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDateTime
import java.util.*

// customer value objects

data class CustomerId(val uuid: UUID = UUID.randomUUID())

// customer commands

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "commandType")
@JsonSubTypes(
        JsonSubTypes.Type(value = CreateCustomerCmd::class, name = "CreateCustomerCmd"),
        JsonSubTypes.Type(value = CreateActivatedCustomerCmd::class, name = "CreateActivatedCustomerCmd"))
interface CustomerCommand {
    val commandId: CommandId
    val customerId: CustomerId
}

// customer events

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "eventType")
@JsonSubTypes(
        JsonSubTypes.Type(value = CustomerCreated::class, name = "CustomerCreated"),
        JsonSubTypes.Type(value = CustomerActivated::class, name = "CustomerActivated"))
interface CustomerEvent

// customer unitOfWork

data class CustomerUnitOfWork(val id: UnitOfWorkId = UnitOfWorkId(),
                              val customerCommand: CustomerCommand,
                              val version: Version,
                              val events: List<CustomerEvent>,
                              val timestamp : LocalDateTime = LocalDateTime.now())

// commands

data class CreateCustomerCmd(override val commandId: CommandId = CommandId(),
                             override val customerId: CustomerId) : CustomerCommand

data class ActivateCustomerCmd(override val commandId: CommandId = CommandId(),
                              override val customerId: CustomerId,
                               val date: LocalDateTime) : CustomerCommand

data class CreateActivatedCustomerCmd(override val commandId: CommandId,
                                      override val customerId: CustomerId,
                                      val date: LocalDateTime) : CustomerCommand

// events

data class CustomerCreated(val customerId: CustomerId) : CustomerEvent

data class CustomerActivated(val date: LocalDateTime) : CustomerEvent

// aggregate root

data class Customer(val customerId: CustomerId?, val name: String?, val active: Boolean, val activatedSince : LocalDateTime?) {

    constructor() : this(null, null, false, null)

    // behaviour

    fun create(customerId: CustomerId) : List<CustomerEvent> {
        // check if new then
        require(this.customerId==null, {"customer already exists! customerId should be null but is $this.customerId"})
        return listOf(CustomerCreated(customerId))
    }

    fun activate(datetime: LocalDateTime) : List<CustomerEvent> {
        return listOf(CustomerActivated(datetime))
    }

}

// commands routing and execution function

val handleCustomerCommands : (Customer, Version, CustomerCommand, (CustomerEvent, Customer) -> Customer) ->
                        CustomerUnitOfWork? = { aggregateRoot, version, command, stateTransitionFn ->
    when(command) {
        is CreateCustomerCmd -> CustomerUnitOfWork(customerCommand = command, version = version.nextVersion(),
                events = aggregateRoot.create(command.customerId))
        is ActivateCustomerCmd -> CustomerUnitOfWork(customerCommand = command, version = version.nextVersion(),
                events = aggregateRoot.activate(LocalDateTime.now()))
        is CreateActivatedCustomerCmd -> {
            val events = with(StateTransitionsTracker(aggregateRoot, stateTransitionFn)) {
                apply(aggregateRoot.create(command.customerId))
                apply(aggregateRoot.activate(LocalDateTime.now()))
                collectedEvents()
            }
            CustomerUnitOfWork(customerCommand = command, version = version.nextVersion(), events = events)
        }
        else -> null
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