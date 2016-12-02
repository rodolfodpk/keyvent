package keyvent.example

import com.google.gson.GsonBuilder
import keyvent.*
import keyvent.example.*
import keyvent.helpers.RuntimeTypeAdapterFactory
import net.dongliu.gson.GsonJava8TypeAdapterFactory
import java.time.LocalDateTime
import kotlin.test.assertEquals

fun main(args : Array<String>) {
    test1()
    gsonTest()
    println("hi !")

}

fun test1() {
    val cmd : CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
    val state = Customer()
    val version = Version(0)
    val uow = handleCustomerCommands(state, version, cmd, stateTransitionFn)
}

fun uow2(): UnitOfWork? {
    val cmd : CreateActivatedCustomerCmd = CreateActivatedCustomerCmd(CommandId(), CustomerId(), LocalDateTime.now())
    val state = Customer()
    val version = Version(1)
    return handleCustomerCommands(state, version, cmd, stateTransitionFn).get()
}

fun gsonTest() {

    val rtaCommand : RuntimeTypeAdapterFactory<Command> = RuntimeTypeAdapterFactory.of(Command::class.java)
            .registerSubtype(CreateCustomerCmd::class.java)
            .registerSubtype(ActivateCustomerCmd::class.java)
            .registerSubtype(CreateActivatedCustomerCmd::class.java)

    val rtaEvents : RuntimeTypeAdapterFactory<Event> = RuntimeTypeAdapterFactory.of(Event::class.java)
            .registerSubtype(CustomerCreated::class.java)
            .registerSubtype(CustomerActivated::class.java)

    val gsonBuilder = GsonBuilder()

    gsonBuilder.setPrettyPrinting()
    gsonBuilder.registerTypeAdapterFactory(GsonJava8TypeAdapterFactory())
    gsonBuilder.registerTypeAdapterFactory(rtaCommand)
    gsonBuilder.registerTypeAdapterFactory(rtaEvents)

    val gson = gsonBuilder.create()

    val uow = uow2()
    val uowAsJson = gson.toJson(uow)

    println(uowAsJson)

    val fromJson = gson.fromJson(uowAsJson, UnitOfWork::class.java)

    assertEquals(fromJson, uow)
}