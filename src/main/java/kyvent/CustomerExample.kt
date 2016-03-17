package kyvent

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDateTime

// https://www.thomaskeller.biz/blog/2013/09/10/custom-polymorphic-type-handling-with-jackson/


// commands

data class CreateCustomerCmd(override val commandId: CommandId = CommandId(),
                             override val eventSourcedId: EventSourcedId) : Command

data class CreateActivatedCustomerCmd(override val commandId: CommandId,
                                      override val eventSourcedId: EventSourcedId) : Command

// events

data class CustomerCreated(val customerId: EventSourcedId) : Event

data class CustomerActivated(val date: LocalDateTime) : Event

// aggregate root

data class Customer(val customerId: EventSourcedId?, val name: String?, val active: Boolean, val activatedSince : LocalDateTime?) : AggregateRoot {

    constructor() : this(null, null, false, null)

    // behaviour

    fun create(customerId: EventSourcedId) : List<Event> {
        // check if new then
        require(this.customerId==null, {"customer already exists! customerId should be null but is $this.customerId"})
        return listOf(CustomerCreated(customerId))
    }

    fun activate() : List<Event> {
        return listOf(CustomerActivated(LocalDateTime.now()))
    }

}

// events routing and execution function

val applyEventOnCustomer : (Event, Customer) -> Customer = { event, customer ->
    when(event) {
        is CustomerCreated -> customer.copy(customerId = event.customerId)
        is CustomerActivated -> customer.copy(active = true, activatedSince = event.date)
        else -> customer
    }
}

// commands routing and execution function

val handleCustomerCommands : (Snapshot<Customer>, Command, (Event, Customer) -> Customer) -> UnitOfWork? = { snapshot, command, applyEventOn ->
    when(command) {
        is CreateCustomerCmd -> UnitOfWork(command = command, version = snapshot.nextVersion(),
                events = snapshot.eventSourced.create(command.eventSourcedId))
        is CreateActivatedCustomerCmd -> {
            val events = with(StateTransitionsTracker(snapshot.eventSourced, applyEventOn)) {
                apply(snapshot.eventSourced.create(command.eventSourcedId))
                apply(snapshot.eventSourced.activate())
                collectedEvents()
            }
            UnitOfWork(command = command, version = snapshot.nextVersion(), events = events)
        }
        else -> null
    }
}

// customer commands

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "commandType")
@JsonSubTypes(
        JsonSubTypes.Type(value = CreateCustomerCmd::class, name = "CreateCustomerCmd"),
        JsonSubTypes.Type(value = CreateActivatedCustomerCmd::class, name = "CreateActivatedCustomerCmd"))
interface Command {
    val commandId: CommandId
    val eventSourcedId : EventSourcedId
}

// customer events

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "eventType")
@JsonSubTypes(
        JsonSubTypes.Type(value = CustomerCreated::class, name = "CustomerCreated"),
        JsonSubTypes.Type(value = CustomerActivated::class, name = "CustomerActivated"))
interface Event

