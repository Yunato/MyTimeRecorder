package io.github.yunato.myrecordtimer.ui.fragment

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.preference.PreferenceManager
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.view.View
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.other.service.TimerIntentService
import io.github.yunato.myrecordtimer.ui.dialog.TimePickerFragment
import io.github.yunato.myrecordtimer.ui.view.TimerTextView
import kotlinx.android.synthetic.main.fragment_easy_mode.*

class EasyModeFragment : ModeFragment(), TimePickerFragment.OnSetTimeListener {

    private var mode: Int = -1
    private var isFirstTime : Boolean = true
    override val resource: Int = R.layout.fragment_easy_mode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            mode = it.getInt(ARG_MODE)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_start_end.setOnClickListener{
            if(isFirstTime){
                startService(TimerIntentService.startActionCountDown(activity as Context, startSec))
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

        if(mode == MODE_FIXED){
            val picker = TimePickerFragment.newInstance(this)
            picker.setParams(0, 0, 30)
            picker.setStyle(DialogFragment.STYLE_NO_TITLE, 0)
            picker.isCancelable = false
            picker.show(fragmentManager, "time_picker")
        }else{
            val sp = PreferenceManager.getDefaultSharedPreferences(activity)
            val hr = sp.getInt(TimePickerFragment.KEY_HOUR, 0)
            val min = sp.getInt(TimePickerFragment.KEY_MINUTE, 25)
            val sec = sp.getInt(TimePickerFragment.KEY_SECOND, 0)
            setCountText(hr, min, sec)
            startSec = hr * 60L * 60L + min * 60L + sec
            textView_time.setParam(TimerTextView.MODE_DOWN, startSec * 1000L)
        }
    }

    override fun handleTimeParams(time: Long) {
        val timeSec = time / 1000L
        val sec = timeSec % 60
        val min = (timeSec / 60) % 60
        val hr = timeSec / 60 / 60
        setCountText(hr.toInt(), min.toInt(), sec.toInt())
        textView_time.updateNowTime(time)
        if(timeSec == 0L) {
            stopService()
            val vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(longArrayOf(0, 500, 250, 500), -1)
        }
    }

    override fun onSetTime(hr: Int, min: Int, sec: Int) {
        setCountText(hr, min, sec)
        startSec = hr * 60L * 60L + min * 60L + sec
        textView_time.setParam(TimerTextView.MODE_DOWN, startSec * 1000L)
    }

    companion object {
        @JvmStatic private val ARG_MODE = "io.github.yunato.myrecordtimer.ui.fragment.ARG_MODE"
        @JvmStatic val MODE_FIXED: Int = 0
        @JvmStatic val MODE_FLOATED: Int = 1

        @JvmStatic
        fun newInstance(mode: Int): EasyModeFragment {
            return EasyModeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_MODE, mode)
                }
            }
        }
    }
}
