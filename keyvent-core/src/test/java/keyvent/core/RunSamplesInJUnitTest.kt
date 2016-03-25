package keyvent.core

import keyvent.core.test.SimpleJavaJournalSpec
import org.junit.internal.RealSystem
import org.junit.internal.TextListener
import org.junit.runner.JUnitCore
import org.junit.Test as test

class RunSamplesInJUnitTest {

    @org.junit.Test fun try_junit() {
        with(JUnitCore()) {
            addListener(TextListener(RealSystem()))
            run(SimpleJavaJournalSpec::class.java)
        }
    }

//    @test fun try_console() {
//        main(arrayOf(".", "kyvent", "-f", "text"))
//    }

}