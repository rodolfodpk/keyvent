package keyvent

import keyvent.flows.commands.SimpleJournalSpec
import keyvent.flows.commands.SimpleRepositorySpec
import keyvent.flows.commands.SimpleStateTransitionsTracker
import org.junit.internal.RealSystem
import org.junit.internal.TextListener
import org.junit.runner.JUnitCore
import org.junit.Test as test

class RunSamplesInJUnitTest {

    @org.junit.Test fun try_junit() {
        with(JUnitCore()) {
            addListener(TextListener(RealSystem()))
            run(SimpleJournalSpec::class.java, SimpleRepositorySpec::class.java, SimpleStateTransitionsTracker::class.java)
        }
    }

//    @test fun try_console() {
//        main(arrayOf(".", "kyvent", "-f", "text"))
//    }

}
