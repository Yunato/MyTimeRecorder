package io.github.yunato.myrecordtimer.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.other.service.TimerIntentService
import io.github.yunato.myrecordtimer.ui.view.TimerTextView
import kotlinx.android.synthetic.main.fragment_easy_mode.*

class NormalModeFragment : ModeFragment() {

    private var isFirstTime : Boolean = true
    override val resource: Int = R.layout.fragment_normal_mode

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_start_end.setOnClickListener{
            if(isFirstTime){
                startService(TimerIntentService.startActionCountUp(activity as Context, startSec))
                isFirstTime = false
                button_lap.isEnabled = true
                button_lap.setTextColor(ContextCompat.getColor(activity as Context, R.color.colorAccent))
                button_start_end.setText(R.string.button_finish)
            }else{
                stopService()
            }
        }
        button_lap.setOnClickListener{
            this.createLap()
        }
        textView_time.setParam(TimerTextView.MODE_UP)
    }

    override fun handleTimeParams(time: Long) {
        val timeSec = time / 1000L
        val sec = timeSec % 60
        val min = (timeSec / 60) % 60
        val hr = timeSec / 60 / 60
        setCountText(hr.toInt(), min.toInt(), sec.toInt())
        textView_time.updateNowTime(time)
    }

    companion object {
        @JvmStatic
        fun newInstance() = NormalModeFragment()
    }
}
