package keyvent.example

import com.github.kittinunf.result.Result
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import keyvent.*
import keyvent.runtimes.default.StateTransitionsTracker
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

// a helper service

open class SupplierHelperService {
    open fun now() = LocalDateTime.now()
    open fun uuId() = UUID.randomUUID()
}

// dependencies

class CustomerModule : AbstractModule() {
    override fun configure() {
        bind(SupplierHelperService::class.java)
    }
}

val injector = Guice.createInjector(CustomerModule())

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
        println(this.customerId!!)
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

val injectedAggregateRootFn: (Injector, Customer) -> Customer = {
    i, c ->
    i.injectMembers(c)
    c
}

// events routing and execution function

val stateTransitionFn: (CustomerEvent, Customer) -> Customer = { event, state ->
    when (event) {
        is CustomerCreated -> state.copy(customerId = event.customerId)
        is CustomerActivated -> state.copy(active = true, activatedSince = event.date)
        is CustomerDeactivated -> state.copy(active = false, deactivatedSince = event.date)
        is DeactivatedCmdScheduled -> state
        else -> {
            throw IllegalArgumentException("invalid event")
        }
    }
}

// commands routing and execution function

val handleCustomerCommandsFn
        : (Customer, Version, CustomerCommand, (CustomerEvent, Customer) -> Customer) ->
Result<UnitOfWork, Exception> = { aggregateRoot, version, command, stateTransitionFn ->
    Result.of {
        when (command) {
            is CreateCustomerCmd -> {
                require(version == Version(0), {"before create the instance must be version= 0"})
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
                // TODO check https://gist.github.com/cy6erGn0m/6960104
                val _events = with(StateTransitionsTracker(aggregateRoot, stateTransitionFn, injector)) { // TODO injector should be a param
                    apply(aggregateRoot.create(command.customerId))
                    apply(currentState().activate())
                    collectedEvents()
                }
                UnitOfWork(command = command, version = version.nextVersion(), events = _events)
            }
            else -> {
                throw IllegalArgumentException("invalid command")
            }
        }
    }
}
