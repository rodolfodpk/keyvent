package keyvent

import com.github.kittinunf.result.Result
import com.google.inject.Injector
import java.time.LocalDateTime


// persistence

interface EventRepository<ID> {
    fun eventsAfter(id: ID, afterVersion: Version): List<UnitOfWork>
}

interface Journal<ID> {
    fun append(targetId: ID, unitOfWork: UnitOfWork)
}

// scheduling

interface CommandScheduler {
    fun schedule(causeCommand : CommandId, commandScheduling: CommandScheduling)
}

// exploratory

interface CommandReceiver {

    val aggregateRoot: String

    val submit: (Command) -> Result<Unit, Exception>

}

interface CommandProcessor<AR, ID> {

    val aggregateRoot: String

    val lastSnapshot: (ID) -> Pair<AR, Version>

    val latestVersion: (ID) -> Version

    val eventsAfter: (ID, Version) -> List<UnitOfWork>

    val initialInstance: (Injector) -> AR

    val stateTransition: (AR, Event) -> AR

    val handleCommand: (AR, Version, Command, (Event, AR) -> AR) -> Result<UnitOfWork, Exception>

    val append: (ID, UnitOfWork) -> Result<Unit, Exception>

}

// TODO EventsProjector
