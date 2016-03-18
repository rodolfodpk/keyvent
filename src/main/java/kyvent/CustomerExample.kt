package kyvent

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

data class CreateActivatedCustomerCmd(override val commandId: CommandId,
                                      override val customerId: CustomerId) : CustomerCommand

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

    fun activate() : List<CustomerEvent> {
        return listOf(CustomerActivated(LocalDateTime.now()))
    }

}

// events routing and execution function

val applyEventOnCustomer : (CustomerEvent, Customer) -> Customer = { event, customer ->
    when(event) {
        is CustomerCreated -> customer.copy(customerId = event.customerId)
        is CustomerActivated -> customer.copy(active = true, activatedSince = event.date)
        else -> customer
    }
}

// commands routing and execution function

val handleCustomerCommands : (Snapshot<Customer>, CustomerCommand, (CustomerEvent, Customer) -> Customer) -> CustomerUnitOfWork? = { snapshot, command, applyEventOn ->
    when(command) {
        is CreateCustomerCmd -> CustomerUnitOfWork(customerCommand = command, version = snapshot.nextVersion(),
                events = snapshot.eventSourced.create(command.customerId))
        is CreateActivatedCustomerCmd -> {
            val events = with(StateTransitionsTracker(snapshot.eventSourced, applyEventOn)) {
                apply(snapshot.eventSourced.create(command.customerId))
                apply(snapshot.eventSourced.activate())
                collectedEvents()
            }
            CustomerUnitOfWork(customerCommand = command, version = snapshot.nextVersion(), events = events)
        }
        else -> null
    }
}
