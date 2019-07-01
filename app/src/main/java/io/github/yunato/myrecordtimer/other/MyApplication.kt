package io.github.yunato.myrecordtimer.other

import android.app.Application
import io.github.yunato.myrecordtimer.R
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
            .setDefaultFontPath("fonts/font.ttf")
            .setFontAttrId(R.attr.fontPath)
            .build())
    }
}
