package io.github.yunato.myrecordtimer.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.yunato.myrecordtimer.R

class EasyModeFixedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_easy_mode_fixed)
    }

    override fun onResume() {
        super.onResume()

        val decor: android.view.View = window.decorView
        decor.systemUiVisibility =
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    android.view.View.SYSTEM_UI_FLAG_FULLSCREEN or
                    android.view.View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
}
