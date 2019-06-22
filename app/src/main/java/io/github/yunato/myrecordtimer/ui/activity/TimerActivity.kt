package io.github.yunato.myrecordtimer.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.other.service.TimerIntentService
import io.github.yunato.myrecordtimer.ui.fragment.EasyModeFragment
import io.github.yunato.myrecordtimer.ui.fragment.HardModeFragment
import io.github.yunato.myrecordtimer.ui.fragment.NormalModeFragment

class TimerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        val mode = intent.getIntExtra(EXTRA_MODE, 0)
        if(mode == 0) finish()
        val fragment: Fragment = when(mode){
            EXTRA_MODE_EASY_FIXED -> EasyModeFragment.newInstance(EasyModeFragment.MODE_FIXED)
            EXTRA_MODE_EASY_FLOATED -> EasyModeFragment.newInstance(EasyModeFragment.MODE_FLOATED)
            EXTRA_MODE_NORMAL -> NormalModeFragment.newInstance()
            EXTRA_MODE_HARD -> HardModeFragment.newInstance()
            else -> throw IllegalStateException("Mode is not correct")
        }
        supportFragmentManager.beginTransaction().replace(R.id.content, fragment).commit()
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

    override fun onBackPressed() {
        super.onBackPressed()
        TimerIntentService.isContinue = false
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    companion object {
        @JvmStatic val EXTRA_MODE = "io.github.yunato.myrecordtimer.ui.activity.EXTRA_TYPE"
        @JvmStatic val EXTRA_MODE_EASY_FIXED = 1
        @JvmStatic val EXTRA_MODE_EASY_FLOATED = 2
        @JvmStatic val EXTRA_MODE_NORMAL = 3
        @JvmStatic val EXTRA_MODE_HARD = 4

        fun intent(context: Context, mode: Int): Intent {
            return Intent(context, TimerActivity::class.java).apply{
                putExtra(EXTRA_MODE, mode)
            }
        }
    }
}
