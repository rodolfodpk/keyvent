package keyvent

import java.time.LocalDateTime

interface CommandSchedule {
    //  val causeCommand : CommandId
    val scheduledCommand: Command
    val scheduledAt: LocalDateTime
}

interface CommandScheduler {
    fun schedule(commandSchedule: CommandSchedule)
}

