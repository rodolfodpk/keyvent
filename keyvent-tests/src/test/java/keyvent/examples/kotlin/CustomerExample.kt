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
//        JsonSubTypes.Type(value = CreateCustomerCmd::class, name = "CreateCustomerCmd"),
//        JsonSubTypes.Type(value = CreateActivatedCustomerCmd::class, name = "CreateActivatedCustomerCmd"))
//interface CustomerCommand {
//    val commandId: CommandId
//    val customerId: CustomerId
//}
//
//// customer events
//
//@JsonTypeInfo(
//        use = JsonTypeInfo.Id.NAME,
//        include = JsonTypeInfo.As.PROPERTY,
//        property = "eventType")
//@JsonSubTypes(
//        JsonSubTypes.Type(value = CustomerCreated::class, name = "CustomerCreated"),
//        JsonSubTypes.Type(value = CustomerActivated::class, name = "CustomerActivated"))
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
//data class CreateCustomerCmd(override val commandId: CommandId = CommandId.of(UUID.randomUUID()),
//                             override val customerId: CustomerId) : CustomerCommand
//
//data class ActivateCustomerCmd(override val commandId: CommandId = CommandId.of(UUID.randomUUID()),
//                               override val customerId: CustomerId) : CustomerCommand
//
//data class CreateActivatedCustomerCmd(override val commandId: CommandId,
//                                      override val customerId: CustomerId) : CustomerCommand
//
//// events
//
//data class CustomerCreated(val customerId: CustomerId) : CustomerEvent
//
//data class CustomerActivated(val date: LocalDateTime) : CustomerEvent
//
//// aggregate root
//
//data class Customer(val customerId: CustomerId?, val name: String?, val active: Boolean, val activatedSince : LocalDateTime?) {
//
//    constructor() : this(null, null, false, null)
//
//    // behaviour
//
//    fun create(customerId: CustomerId) : List<CustomerEvent> {
//        // check if new then
//        require(this.customerId==null, {"customer already exists! customerId should be null but is $this.customerId"})
//        return listOf(CustomerCreated(customerId))
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
//        is CustomerCreated -> customer.copy(customerId = event.customerId)
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
//                events = snapshot.instance().create(command.customerId))
//        is ActivateCustomerCmd -> CustomerUnitOfWork(customerCommand = command, version = snapshot.nextVersionOf(),
//                events = snapshot.instance().activate())
//        is CreateActivatedCustomerCmd -> {
//            val tracker : SimpleStateTransitionsTracker<CustomerEvent, Customer> =
//                    SimpleStateTransitionsTracker(snapshot.instance(), )
//
//            val events = with((snapshot.instance(), applyEventOn)) {
//                apply(snapshot.instance().create(command.customerId))
//                apply(snapshot.instance().activate())
//                collectedEvents()
//            }
//            CustomerUnitOfWork(customerCommand = command, version = snapshot.nextVersionOf(), events = events)
//        }
//        else -> null
//    }
//}
