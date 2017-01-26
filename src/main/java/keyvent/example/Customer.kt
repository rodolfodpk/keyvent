package keyvent.example

import com.github.kittinunf.result.Result
import com.google.inject.Injector
import keyvent.*
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

// a helper service

open class SupplierHelperService {
    open fun now() = LocalDateTime.now()
    open fun uuId() = UUID.randomUUID()
}

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

data class DeactivateCustomerCmd(override val commandId: CommandId = CommandId(),
                                 override val customerId: CustomerId) : CustomerCommand

// customer events

interface CustomerEvent : Event

data class CustomerCreated(val customerId: CustomerId) : CustomerEvent

data class CustomerActivated(val date: LocalDateTime) : CustomerEvent

data class DeactivatedCmdScheduled(override val scheduledCommand: DeactivateCustomerCmd,
                                   override val scheduledAt: LocalDateTime) : CustomerEvent, CommandSchedule

data class CustomerDeactivated(val date: LocalDateTime) : CustomerEvent

// aggregate root

data class Customer(val customerId: CustomerId?, val name: String?,
                    val active: Boolean,
                    val activatedSince: LocalDateTime?,
                    val deactivatedSince: LocalDateTime?) : AggregateRoot {

    constructor() : this(null, null, false, null, null)

    @Inject lateinit var genValService: SupplierHelperService

    // behaviour

    fun create(customerId: CustomerId): List<CustomerEvent> {
        // check if new then
        require(this.customerId == null, { "customer already exists! customerId should be null but is $this.customerId" })
        return listOf(CustomerCreated(customerId))
    }

    fun activate(): List<CustomerEvent> {
        return listOf(CustomerActivated(genValService.now()),
                DeactivatedCmdScheduled(
                        DeactivateCustomerCmd(commandId = CommandId(genValService.uuId()),
                                customerId = this.customerId!!),
                        genValService.now().plusDays(1)))
    }

    fun deactivate(): List<CustomerEvent> {
        return listOf(CustomerDeactivated(genValService.now()))
    }
}

val emptyAggregateRootFn: (Injector) -> Customer = {
    i ->
    val c = Customer();
    i.injectMembers(c);
    c
}

// events routing and execution function

val stateTransitionFn: (CustomerEvent, Customer) -> Customer = { event, state ->
    when (event) {
        is CustomerCreated -> state.copy(customerId = event.customerId)
        is CustomerActivated -> state.copy(active = true, activatedSince = event.date)
        is CustomerDeactivated -> state.copy(active = false, deactivatedSince = event.date)
        else -> state
    }
}

// commands routing and execution function

val handleCustomerCommandsFn
        : (Customer, Version, CustomerCommand, (CustomerEvent, Customer) -> Customer) ->
Result<UnitOfWork, Exception> = { aggregateRoot, version, command, stateTransitionFn ->
    Result.of {
        when (command) {
            is CreateCustomerCmd -> {
                // TODO  assertThat(version == Version(0), {"before create the instance must be version= 0"});
                UnitOfWork(command = command, version = version.nextVersion(),
                        events = aggregateRoot.create(command.customerId))
            }
            is ActivateCustomerCmd ->
                UnitOfWork(command = command, version = version.nextVersion(),
                        events = aggregateRoot.activate())
            is DeactivateCustomerCmd ->
                UnitOfWork(command = command, version = version.nextVersion(),
                        events = aggregateRoot.deactivate())
            is CreateActivatedCustomerCmd -> {
                val events = with(StateTransitionsTracker(aggregateRoot, stateTransitionFn)) {
                    apply(aggregateRoot.create(command.customerId))
                    apply(aggregateRoot.activate())
                    collectedEvents()
                }
                UnitOfWork(command = command, version = version.nextVersion(), events = events)
            }
            else -> {
                throw IllegalArgumentException("invalid command")
            }
        }
    }
}

// projections TODO


