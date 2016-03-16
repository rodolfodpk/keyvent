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
