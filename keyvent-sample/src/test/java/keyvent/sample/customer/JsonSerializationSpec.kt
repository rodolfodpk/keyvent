package keyvent.sample.customer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JSR310Module
import org.jetbrains.spek.api.Spek
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

class JsonSerializationSpec : Spek() {

    val mapper = ObjectMapper()
    val createdEvent = CustomerSchema.CustomerCreated.builder().customerId(CustomerSchema.CustomerId(UUID.randomUUID())).build()
    val activatedEvent = CustomerSchema.CustomerActivated.builder().customerId(createdEvent.getCustomerId()).date(LocalDateTime.now()).build()

    init {
        mapper.registerModules(Jdk8Module(), JSR310Module())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        given("A customerCreated event") {
            on("serializing it") {
                val asJson = mapper.writeValueAsString(createdEvent)
                it("should result in a proper json") {
                    val expected = "{\"eventType\":\"CustomerCreated\",\"customerId\":{\"uuid\":\"${createdEvent.getCustomerId().getUuid()}\"}}"
               //     assertEquals(expected, asJson)
                    println(asJson)
                }
            }
        }
//        given("A serialized customerCreated event") {
//            val asJson = "{\"eventType\":\"CustomerCreated\",\"customerId\":{\"uuid\":\"${createdEvent.getCustomerId().getUuid()}\"}}"
//            on("deserialize it") {
//                val event = mapper.readValue(asJson, CustomerSchema.CustomerCreated::class.java)
//                it("should result in a proper uuid") {
//                    assertEquals(createdEvent, event)
//                }
//            }
//        }
//        given("A customerActivated event") {
//            on("serializing it") {
//                val asJson = mapper.writeValueAsString(activatedEvent)
//                it("should result in a proper json") {
//                    val expected = """{"eventType":"CustomerActivated","date":"${activatedEvent.getDate()}","customerId":{"uuid":"${createdEvent.getCustomerId().getUuid()}"}}"""
//                    assertEquals(expected, asJson)
//                }
//            }
//        }
//        given("A serialized customerActivated event") {
//            val asJson = """{"eventType":"CustomerActivated","date":"${activatedEvent.getDate()}","customerId":{"uuid":"${createdEvent.getCustomerId().getUuid()}"}}"""
//            on("deserialize it") {
//                val event = mapper.readValue(asJson, CustomerSchema.CustomerActivated::class.java)
//                it("should result in a proper uuid") {
//                    assertEquals(activatedEvent, event)
//                }
//            }
//        }

    }
}
