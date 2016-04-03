package keyvent.examples.kotlin

//
//// customer value objects
//
//data class CustomerId(val uuid: UUID = UUID.randomUUID())
//
//// customer commands
//
//@JsonTypeInfo(
//        use = JsonTypeInfo.Id.NAME,
//        include = JsonTypeInfo.As.PROPERTY,
//        property = "commandType")
//@JsonSubTypes(
//        JsonSubTypes.Type(value = CreateCustomerCmd::class, getName = "CreateCustomerCmd"),
//        JsonSubTypes.Type(value = CreateActivatedCustomerCmd::class, getName = "CreateActivatedCustomerCmd"))
//interface CustomerCommand {
//    val getCommandId: CommandId
//    val getCustomerId: CustomerId
//}
//
//// customer events
//
//@JsonTypeInfo(
//        use = JsonTypeInfo.Id.NAME,
//        include = JsonTypeInfo.As.PROPERTY,
//        property = "eventType")
//@JsonSubTypes(
//        JsonSubTypes.Type(value = CustomerCreated::class, getName = "CustomerCreated"),
//        JsonSubTypes.Type(value = CustomerActivated::class, getName = "CustomerActivated"))
//interface CustomerEvent
//
//// customer unitOfWork
//
//data class CustomerUnitOfWork(val id: UnitOfWorkId = UnitOfWorkId.of(UUID.randomUUID()),
//                              val customerCommand: CustomerCommand,
//                              val version: Version,
//                              val events: List<CustomerEvent>,
//                              val timestamp : LocalDateTime = LocalDateTime.now())
//
//// commands
//
//data class CreateCustomerCmd(override val getCommandId: CommandId = CommandId.of(UUID.randomUUID()),
//                             override val getCustomerId: CustomerId) : CustomerCommand
//
//data class ActivateCustomerCmd(override val getCommandId: CommandId = CommandId.of(UUID.randomUUID()),
//                               override val getCustomerId: CustomerId) : CustomerCommand
//
//data class CreateActivatedCustomerCmd(override val getCommandId: CommandId,
//                                      override val getCustomerId: CustomerId) : CustomerCommand
//
//// events
//
//data class CustomerCreated(val getCustomerId: CustomerId) : CustomerEvent
//
//data class CustomerActivated(val date: LocalDateTime) : CustomerEvent
//
//// aggregate root
//
//data class Customer(val getCustomerId: CustomerId?, val getName: String?, val active: Boolean, val activatedSince : LocalDateTime?) {
//
//    constructor() : this(null, null, false, null)
//
//    // behaviour
//
//    fun create(getCustomerId: CustomerId) : List<CustomerEvent> {
//        // check if new then
//        require(this.getCustomerId==null, {"customer already exists! getCustomerId should be null but is $this.getCustomerId"})
//        return listOf(CustomerCreated(getCustomerId))
//    }
//
//    fun activate() : List<CustomerEvent> {
//        return listOf(CustomerActivated(LocalDateTime.now()))
//    }
//
//}
//
//// events routing and execution function
//
//val applyEventOnCustomer : (CustomerEvent, Customer) -> Customer = { event, customer ->
//    when(event) {
//        is CustomerCreated -> customer.copy(getCustomerId = event.getCustomerId)
//        is CustomerActivated -> customer.copy(active = true, activatedSince = event.date)
//        else -> customer
//    }
//}
//
//// commands routing and execution function
//val versionExtractor = BiFunction<CustomerEvent, Customer, Customer> {
//    event, customer ->
//}
//
//val javaHandleCustomerCommands = BiFunction<CustomerEvent, Snapshot<Customer>, Snapshot<Customer>> {
//    event, customerSnapshot ->  handleCustomerCommands.invoke(customerSnapshot, )}
//
//
//val handleCustomerCommands : (Snapshot<Customer>, CustomerCommand, (CustomerEvent, Customer) -> Customer) -> CustomerUnitOfWork? = { snapshot, command, applyEventOn ->
//    when(command) {
//        is CreateCustomerCmd -> CustomerUnitOfWork(customerCommand = command, version = snapshot.nextVersionOf(),
//                events = snapshot.instance().create(command.getCustomerId))
//        is ActivateCustomerCmd -> CustomerUnitOfWork(customerCommand = command, version = snapshot.nextVersionOf(),
//                events = snapshot.instance().activate())
//        is CreateActivatedCustomerCmd -> {
//            val tracker : SimpleStateTransitionsTracker<CustomerEvent, Customer> =
//                    SimpleStateTransitionsTracker(snapshot.instance(), )
//
//            val events = with((snapshot.instance(), applyEventOn)) {
//                apply(snapshot.instance().create(command.getCustomerId))
//                apply(snapshot.instance().activate())
//                collectedEvents()
//            }
//            CustomerUnitOfWork(customerCommand = command, version = snapshot.nextVersionOf(), events = events)
//        }
//        else -> null
//    }
//}
