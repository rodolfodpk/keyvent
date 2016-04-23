package keyvent.flows.customer

class CustomerEvtFnSpec  {

//    val applyEventFn = CustomerEvtFn()
//
//    init {
//        given("An empty Customer ") {
//            val empty = Customer.builder().build()
//            on("applying a customerCreated event") {
//                val created = CustomerCreated.builder().customerId(CustomerId(UUID.randomUUID())).build()
//                it("should result in a proper Customer instance") {
//                    val expected = Customer.builder().id(created.getCustomerId()).build()
//                    val current = applyEventFn.apply(created, empty)
//                    assertEquals(expected, current)
//                }
//            }
//        }
//        given("A created Customer ") {
//            val customer = Customer.builder().id(CustomerId(UUID.randomUUID())).build()
//            on("applying a customerActivated event") {
//                val activated = CustomerActivated.builder().customerId(customer.getId()).date(LocalDateTime.now()).build()
//                it("should result in a proper Customer instance") {
//                    val expected = Customer.builder().id(activated.getCustomerId())
//                            .isActive(true).activeSince(activated.getDate()).build()
//                    val current = applyEventFn.apply(activated, customer)
//                    assertEquals(expected, current)
//                }
//            }
//        }
//    }
}
