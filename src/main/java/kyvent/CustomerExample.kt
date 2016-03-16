package kyvent

import java.time.LocalDateTime

// commands

data class CreateCustomerCmd(override val commandId: CommandId,
                             override val eventSourcedId: EventSourcedId) : Command

data class CreateAncActivateCustomerCmd(override val commandId: CommandId,
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
        is CreateAncActivateCustomerCmd -> {
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

