package mm.com.sumyat.archiecture_sample.cache

import android.content.Context
import android.content.SharedPreferences

open class PreferencesHelper(context: Context) {

    companion object {
        private val PREF_BUFFER_PACKAGE_NAME = "mm.com.sumyat.archiecture_sample"

        private val PREF_KEY_NEXT_PAGE = "next_page"
    }

    private val bufferPref: SharedPreferences

    init {
        bufferPref = context.getSharedPreferences(PREF_BUFFER_PACKAGE_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Store and retrieve the last time data was cached
     */
    var nextPage: Int
        get() = bufferPref.getInt(PREF_KEY_NEXT_PAGE, 1)
        set(nextPage) = bufferPref.edit().putInt(PREF_KEY_NEXT_PAGE, nextPage).apply()

}