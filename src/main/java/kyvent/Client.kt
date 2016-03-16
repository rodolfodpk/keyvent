package kyvent

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule


fun main(args : Array<String>) {

    test1()
    test2()

}

fun test1() {

    println("single step")

    val cmd : CreateCustomerCmd = CreateCustomerCmd(CommandId(), EventSourcedId())

    val snapshot : Snapshot<Customer> = Snapshot(Customer(), Version(0))

    val uow = handleCustomerCommands(snapshot, cmd, applyEventOnCustomer)

    println(uow)

    val mapper = ObjectMapper().registerModule(KotlinModule())

    println(mapper.writeValueAsString(uow))


}

fun test2() {

    println("multiple step")

    val cmd : CreateAncActivateCustomerCmd = CreateAncActivateCustomerCmd(CommandId(), EventSourcedId())

    val snapshot : Snapshot<Customer> = Snapshot(Customer(), Version(1))

    val uow = handleCustomerCommands(snapshot, cmd, applyEventOnCustomer)

    println(uow)
}
