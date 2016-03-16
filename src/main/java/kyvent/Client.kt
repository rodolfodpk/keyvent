package kyvent

import java.util.*


fun main(args : Array<String>) {

    test2()

}

fun test1() {

    println("single step")

    val cmd : CreateCustomerCmd = CreateCustomerCmd(CommandId(UUID.randomUUID()), EventSourcedId(UUID.randomUUID()))

    val snapshot : Snapshot<Customer> = Snapshot(Customer(), Version(0))

    val uow = handleCustomerCommands(snapshot, cmd, applyEventOnCustomer)

    println(uow)

}

fun test2() {

    println("multiple step")

    val cmd : CreateAncActivateCustomerCmd = CreateAncActivateCustomerCmd(CommandId(UUID.randomUUID()), EventSourcedId(UUID.randomUUID()))

    val snapshot : Snapshot<Customer> = Snapshot(Customer(), Version(1))

    val uow = handleCustomerCommands(snapshot, cmd, applyEventOnCustomer)

    println(uow)
}
