package mm.com.sumyat.archiecture_sample.util

import mm.com.sumyat.archiecture_sample.AppExecutors
import java.util.concurrent.Executor

class InstantAppExecutors : AppExecutors(instant, instant, instant) {
    companion object {
        private val instant = Executor { it.run() }
    }
}