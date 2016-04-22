package keyvent.flows.commands

class SimpleJournalSpec {
//
//    val createCustomerCmd: CustomerSchema.CreateCustomer = CreateCustomer.builder()
//            .commandId(CommandId(UUID.randomUUID()))
//            .customerId(CustomerId(UUID.randomUUID()))
//            .name("alice")
//            .age(35)
//            .build()
//
//    val uow1 = CustomerUow.builder()
//            .id(UnitOfWorkId())
//            .command(createCustomerCmd)
//            .version(1)
//            .events(List.of(CustomerCreated.builder().customerId(createCustomerCmd.getCustomerId()).build()))
//            .instant(Instant.now())
//            .build()
//
//    val activateCmd = ActivateCustomer.builder().commandId(CommandId(UUID.randomUUID()))
//            .customerId(createCustomerCmd.getCustomerId()).build()
//
//    val uow2 = CustomerUow.builder().id(UnitOfWorkId())
//            .command(activateCmd)
//            .version(2)
//            .events(List.of(CustomerActivated.builder()
//                    .customerId(activateCmd.getCustomerId())
//                    .date(LocalDateTime.now()).build()))
//            .instant(Instant.now())
//            .build()
//
//    init {
//        given("An empty journal") {
//            val map: Map<CustomerSchema.CustomerId, List<Tuple2<CustomerUow, Version>>> = javaslang.collection.HashMap.empty()
//            val journal = SimpleJournal<CustomerSchema.CustomerId, CustomerSchema.CustomerUow>(map)
//            on("adding a new unitOfWork with version=1") {
//                val globalSeq: GlobalEventSeq = journal.append(createCustomerCmd.getCustomerId(), uow1, Version(1L))
//                it("should result in a journal with the respective entry") {
//                    val expected: List<Tuple2<CustomerSchema.CustomerUow, Version>> = List.of(Tuple2(uow1, Version(1L)))
//                    val current: List<Tuple2<CustomerSchema.CustomerUow, Version>> = journal.map().get(createCustomerCmd.getCustomerId()).get()
//                    assertEquals(expected, current)
//                }
//                it("should result in a globalSequence = 1") {
//                    assertEquals(globalSeq, GlobalEventSeq(1))
//                }
//            }
//        }
//        given("An empty journal") {
//            val map: Map<CustomerSchema.CustomerId, List<Tuple2<CustomerSchema.CustomerUow, Version>>> = javaslang.collection.HashMap.empty()
//            val journal = SimpleJournal<CustomerSchema.CustomerId, CustomerSchema.CustomerUow>(map)
//            on("adding a new unitOfWork with version =2") {
//                it("should throw an exception since version should be 1") {
//                    try {
//                        journal.append(createCustomerCmd.getCustomerId(), uow2, Version(2))
//                        assertTrue(false, "should throw IllegalArgumentException")
//                    } catch (e: IllegalArgumentException) {
//                    }
//                }
//            }
//        }
//        given("A journal with one uow with version =1") {
//            val map: Map<CustomerSchema.CustomerId, List<Tuple2<CustomerUow, Version>>> =
//                    javaslang.collection.HashMap.of(createCustomerCmd.getCustomerId(), List.of(Tuple2(uow1, Version(1L))))
//            val journal = SimpleJournal<CustomerSchema.CustomerId, CustomerUow>(map)
//            on("adding a new unitOfWork with version =2") {
//                val globalSeq = journal.append(createCustomerCmd.getCustomerId(), uow2, Version(2))
//                it("should result in a journal with the respective first and second entries") {
//                    val expected: List<Tuple2<CustomerUow, Version>> = List.of(Tuple2(uow1, Version(1L)), Tuple2(uow2, Version(2L)))
//                    val current: List<Tuple2<CustomerUow, Version>> = journal.map()[createCustomerCmd.getCustomerId()].get()
//                    assertEquals(expected, current)
//                }
//                it("should result in a globalSequence = 2") {
//                    assertEquals(globalSeq, GlobalEventSeq((2)))
//                }
//            }
//        }
//    }
}
