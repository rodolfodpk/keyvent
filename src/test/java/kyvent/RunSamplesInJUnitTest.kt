package kyvent

import org.junit.Test as test
import org.junit.runner.JUnitCore
import org.junit.internal.RealSystem
import org.junit.internal.TextListener

class RunSamplesInJUnitTest {

    @test fun try_junit() {
        with(JUnitCore()) {
            addListener(TextListener(RealSystem()))
            run(MapJournalSpec::class.java)
        }
    }

    @test fun try_console() {
        main(arrayOf(".", "kyvent", "-f", "text"))
    }

}
