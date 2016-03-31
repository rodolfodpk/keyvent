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
    val createdEvent = CustomerCreatedEvt.builder().customerId(CustomerIdVal.of(UUID.randomUUID())).build()
    val activatedEvent = CustomerActivatedEvt.builder().customerId(createdEvent.customerId()).date(LocalDateTime.now()).build()

    init {
        mapper.registerModules(Jdk8Module(), JSR310Module())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        given("A customerCreated event") {
            on("serializing it") {
                val asJson = mapper.writeValueAsString(createdEvent)
                it("should result in a proper json") {
                    val expected = "{\"eventType\":\"CustomerCreatedEvt\",\"customerId\":{\"value\":\"${createdEvent.customerId().value()}\"}}"
                    assertEquals(expected, asJson)
                }
            }
        }
        given("A serialized customerCreated event") {
            val asJson = "{\"eventType\":\"CustomerCreatedEvt\",\"customerId\":{\"value\":\"${createdEvent.customerId().value()}\"}}"
            on("deserialize it") {
                val event = mapper.readValue(asJson, CustomerSchema.CustomerCreated::class.java)
                it("should result in a proper value") {
                    assertEquals(createdEvent, event)
                }
            }
        }
        given("A customerActivated event") {
            on("serializing it") {
                val asJson = mapper.writeValueAsString(activatedEvent)
                it("should result in a proper json") {
                    val expected = """{"eventType":"CustomerActivatedEvt","date":"${activatedEvent.date()}","customerId":{"value":"${createdEvent.customerId().value()}"}}"""
                    assertEquals(expected, asJson)
                }
            }
        }
        given("A serialized customerActivated event") {
            val asJson = """{"eventType":"CustomerActivatedEvt","date":"${activatedEvent.date()}","customerId":{"value":"${createdEvent.customerId().value()}"}}"""
            on("deserialize it") {
                val event = mapper.readValue(asJson, CustomerSchema.CustomerActivated::class.java)
                it("should result in a proper value") {
                    assertEquals(activatedEvent, event)
                }
            }
        }

    }
}