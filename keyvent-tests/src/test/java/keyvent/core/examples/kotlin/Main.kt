package keyvent.core.examples.kotlin

fun main(args : Array<String>) {
    //   test1()
    //  test2()
    // jacksonTest()
    println("hi !")

}
//
//fun test1() {
//    val cmd : CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
//    val snapshot : Snapshot<Customer> = Snapshot(Customer(), Version(0))
//    val uow = handleCustomerCommands(snapshot, cmd, applyEventOnCustomer)
//}
//
//fun uow2(): CustomerUnitOfWork? {
//    val cmd : CreateActivatedCustomerCmd = CreateActivatedCustomerCmd(CommandId(), CustomerId())
//    val snapshot : Snapshot<Customer> = Snapshot(Customer(), Version(1))
//    return handleCustomerCommands(snapshot, cmd, applyEventOnCustomer)
//}
//
//fun jacksonTest() {
//
//    val mapper = ObjectMapper().registerModules(Jdk8Module(), KotlinModule(), JavaTimeModule())
//    mapper.enable(SerializationFeature.INDENT_OUTPUT);
//    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//
//    val uow = uow2()
//    val uowAsJson = mapper.writeValueAsString(uow)
//
//    val jsonHelper = JsonHelper<CustomerUnitOfWork>(mapper)
//    val uowFromJson = jsonHelper.fromJson(uowAsJson, CustomerUnitOfWork::class.java)
//
//    val jsonHelper2 = JsonHelper<List<CustomerEvent>>(mapper)
//    val onlyTheEvents = jsonHelper2.fromJsonAt("/events", uowAsJson, List::class.java)
//
//
//}
