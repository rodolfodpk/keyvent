package kyvent

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DatabindContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver
import java.time.LocalDateTime

// https://www.thomaskeller.biz/blog/2013/09/10/custom-polymorphic-type-handling-with-jackson/

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM,
        include = JsonTypeInfo.As.PROPERTY,
        property = "customerCommand")
@JsonTypeIdResolver(CommandTypeIdResolver::class)
abstract class CustomerCommand: Command

class CommandTypeIdResolver: TypeIdResolver {
    override fun idFromBaseType(): String? {
        throw UnsupportedOperationException()
    }

    override fun init(p0: JavaType?) {
        throw UnsupportedOperationException()
    }

    override fun typeFromId(p0: String?): JavaType? {
        throw UnsupportedOperationException()
    }

    override fun typeFromId(p0: DatabindContext?, p1: String?): JavaType? {
        throw UnsupportedOperationException()
    }

    override fun getDescForKnownTypeIds(): String? {
        throw UnsupportedOperationException()
    }

    override fun idFromValueAndType(p0: Any?, p1: Class<*>?): String? {
        throw UnsupportedOperationException()
    }

    override fun getMechanism(): JsonTypeInfo.Id? {
        throw UnsupportedOperationException()
    }

    override fun idFromValue(p0: Any?): String? {
        throw UnsupportedOperationException()
    }
}

// commands


data class CreateCustomerCmd(override val commandId: CommandId,
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

