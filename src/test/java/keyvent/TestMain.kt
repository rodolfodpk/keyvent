package keyvent

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.time.LocalDateTime

fun main(args : Array<String>) {
       test1()
     jacksonTest()
    println("hi !")

}

fun test1() {
    val cmd : CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
    val state = Customer()
    val version = Version(0)
    val uow = handleCustomerCommands(state, version, cmd, stateTransitionFn)
}

fun uow2(): CustomerUnitOfWork? {
    val cmd : CreateActivatedCustomerCmd = CreateActivatedCustomerCmd(CommandId(), CustomerId(), LocalDateTime.now())
    val state = Customer()
    val version = Version(1)
    return handleCustomerCommands(state, version, cmd, stateTransitionFn)
}

fun jacksonTest() {

    val mapper = ObjectMapper().registerModules(Jdk8Module(), KotlinModule(), JavaTimeModule())
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    val uow = uow2()
    val uowAsJson = mapper.writeValueAsString(uow)

    val jsonHelper = JsonHelper<CustomerUnitOfWork>(mapper)
    val uowFromJson = jsonHelper.fromJson(uowAsJson, CustomerUnitOfWork::class.java)

    val jsonHelper2 = JsonHelper<List<CustomerEvent>>(mapper)
    val onlyTheEvents = jsonHelper2.fromJsonAt("/events", uowAsJson, List::class.java)

}
