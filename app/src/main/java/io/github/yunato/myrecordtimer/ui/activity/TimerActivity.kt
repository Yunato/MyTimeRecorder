package io.github.yunato.myrecordtimer.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.ui.fragment.EasyModeFragment

class TimerActivity : AppCompatActivity() {

    companion object {
        fun intent(context: Context): Intent = Intent(context, TimerActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        supportFragmentManager.beginTransaction().
            replace(R.id.content, EasyModeFragment.newInstance()).commit()
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
