package keyvent.example

import keyvent.Command
import keyvent.CommandId
import keyvent.CommandScheduling
import keyvent.Event
import java.time.LocalDateTime
import java.util.*

// customer value objects

data class CustomerId(val uuid: UUID = UUID.randomUUID())

// customer commands

interface CustomerCommand : Command {
    val customerId: CustomerId
}

data class CreateCustomerCmd(override val commandId: CommandId = CommandId(),
                             override val customerId: CustomerId) : CustomerCommand

data class ActivateCustomerCmd(override val commandId: CommandId = CommandId(),
                               override val customerId: CustomerId) : CustomerCommand

data class CreateActivatedCustomerCmd(override val commandId: CommandId,
                                      override val customerId: CustomerId) : CustomerCommand

data class DeactivateCustomerCmd(override val commandId: CommandId = CommandId(),
                                 override val customerId: CustomerId) : CustomerCommand

// customer events

interface CustomerEvent : Event

data class CustomerCreated(val customerId: CustomerId) : CustomerEvent

data class CustomerActivated(val date: LocalDateTime) : CustomerEvent

data class DeactivatedCmdScheduled(override val scheduledCommand: DeactivateCustomerCmd,
                                   override val scheduledAt: LocalDateTime) : CustomerEvent, CommandScheduling

data class CustomerDeactivated(val date: LocalDateTime) : CustomerEvent


// views TODO


