package keyvent.sample.customer

import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

class CustomerEvtFnSpec : org.jetbrains.spek.api.Spek() {

    val applyEventFn = CustomerEvtFn()

    init {
        given("An empty Customer ") {
            val empty = CustomerAgg.builder().build()
            on("applying a customerCreated event") {
                val created = CustomerCreatedEvt.builder().customerId(CustomerIdVal.of(UUID.randomUUID())).build()
                it("should result in a proper Customer instance") {
                    val expected = CustomerAgg.builder().id(created.customerId()).build()
                    val current = applyEventFn.apply(created, empty)
                    assertEquals(expected, current)
                }
            }
        }
        given("A created Customer ") {
            val customer = CustomerAgg.builder().id(CustomerIdVal.of(UUID.randomUUID())).build()
            on("applying a customerActivated event") {
                val activated = CustomerActivatedEvt.builder().customerId(customer.id()).date(LocalDateTime.now()).build()
                it("should result in a proper Customer instance") {
                    val expected = CustomerAgg.builder().id(activated.customerId())
                            .isActive(true).activeSince(activated.date()).build()
                    val current = applyEventFn.apply(activated, customer)
                    assertEquals(expected, current)
                }
            }
        }
    }
}
