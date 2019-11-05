package mm.com.sumyat.archiecture_sample.util

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import mm.com.sumyat.archiecture_sample.TestApp

class SampleTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, TestApp::class.java.name, context)
    }
}
