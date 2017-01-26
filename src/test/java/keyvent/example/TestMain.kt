package keyvent.example

import com.google.gson.GsonBuilder
import com.google.inject.AbstractModule
import com.google.inject.Guice
import keyvent.*
import keyvent.helpers.RuntimeTypeAdapterFactory
import net.dongliu.gson.GsonJava8TypeAdapterFactory
import kotlin.test.assertEquals

fun main(args: Array<String>) {
    //gsonTest()
    println(uow2())
    //uow2()
    println("hi !")
}

fun uow1(): UnitOfWork {
    val injector = Guice.createInjector(CustomerModule())
    val cmd: CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
    val customer = emptyAggregateRootFn(injector)
    val version = Version(0)
    return handleCustomerCommandsFn(customer, version, cmd, stateTransitionFn).get()
}

fun uow2(): UnitOfWork {
    val injector = Guice.createInjector(CustomerModule())
    val cmd: CreateActivatedCustomerCmd = CreateActivatedCustomerCmd(CommandId(), CustomerId())
    val customer = emptyAggregateRootFn(injector)
    val version = Version(0)
    return handleCustomerCommandsFn(customer, version, cmd, stateTransitionFn).get()
}

fun gsonTest() {

    val rtaCommand: RuntimeTypeAdapterFactory<Command> = RuntimeTypeAdapterFactory.of(Command::class.java)
            .registerSubtype(CreateCustomerCmd::class.java)
            .registerSubtype(ActivateCustomerCmd::class.java)
            .registerSubtype(CreateActivatedCustomerCmd::class.java)

    val rtaEvents: RuntimeTypeAdapterFactory<Event> = RuntimeTypeAdapterFactory.of(Event::class.java)
            .registerSubtype(CustomerCreated::class.java)
            .registerSubtype(CustomerActivated::class.java)

    val gsonBuilder = GsonBuilder()

    gsonBuilder.setPrettyPrinting()
    gsonBuilder.registerTypeAdapterFactory(GsonJava8TypeAdapterFactory())
    gsonBuilder.registerTypeAdapterFactory(rtaCommand)
    gsonBuilder.registerTypeAdapterFactory(rtaEvents)

    val gson = gsonBuilder.create()

    val uow1 = uow1()
    val uowAsJson1 = gson.toJson(uow1)
    // println(uowAsJson1)
    val fromJsonUow1 = gson.fromJson(uowAsJson1, UnitOfWork::class.java)
    assertEquals(fromJsonUow1, uow1)

    val uow2 = uow2()
    val uowAsJson2 = gson.toJson(uow2)
    println(uowAsJson2)
    val fromJsonUow2 = gson.fromJson(uowAsJson2, UnitOfWork::class.java)
    assertEquals(fromJsonUow2, uow2)

}

class CustomerModule : AbstractModule() {

    override fun configure() {
        bind(SupplierHelperService::class.java)
    }
}