package kyvent

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

val mapper = ObjectMapper().registerModules(Jdk8Module(), KotlinModule(), JavaTimeModule())

fun main(args : Array<String>) {

    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    test1()
    test2()

}

fun test1() {

    println("---> single step")

    val cmd : CreateCustomerCmd = CreateCustomerCmd(CommandId(), EventSourcedId())

    val snapshot : Snapshot<Customer> = Snapshot(Customer(), Version(0))

    val uow = handleCustomerCommands(snapshot, cmd, applyEventOnCustomer)

    println(uow)

    println(mapper.writeValueAsString(uow))


}

fun test2() {

    println("---> multiple step")

    val cmd : CreateAncActivateCustomerCmd = CreateAncActivateCustomerCmd(CommandId(), EventSourcedId())

    val snapshot : Snapshot<Customer> = Snapshot(Customer(), Version(1))

    val uow = handleCustomerCommands(snapshot, cmd, applyEventOnCustomer)

    println(uow)

    println(mapper.writeValueAsString(uow))

}
