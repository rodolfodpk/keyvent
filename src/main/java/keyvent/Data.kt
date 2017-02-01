package keyvent

import java.time.LocalDateTime
import java.util.*

// interfaces

interface Event

interface AggregateRoot

interface Command {
    val commandId: CommandId
}

interface CommandScheduling {
    val scheduledCommand: Command
    val scheduledAt: LocalDateTime
}

// data classes

data class CommandId(val uuid: UUID = UUID.randomUUID())

data class UnitOfWorkId(val uuid: UUID = UUID.randomUUID())

data class Version(val version: Long) {
    fun nextVersion(): Version {
        return Version(version.inc())
    }
}

data class UnitOfWork(val id: UnitOfWorkId = UnitOfWorkId(),
                      val command: Command,
                      val version: Version,
                      val events: List<Event>,
                      val timestamp: LocalDateTime = LocalDateTime.now())
