package io.github.yunato.myrecordtimer.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.other.service.TimerIntentService
import io.github.yunato.myrecordtimer.ui.dialog.TimePickerFragment
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
                button_start_end.setText(R.string.button_finish)
            }else{
                stopService()
            }
        }

        if(mode == MODE_FIXED){
            val picker = TimePickerFragment.newInstance(this)
            picker.setParams(0, 0, 30)
            picker.setStyle(DialogFragment.STYLE_NO_TITLE, 0)
            picker.show(fragmentManager, "time_picker")
        }else{
            // TODO: get param from preference
            val hr = 0
            val min = 0
            val sec = 5
            setCountText(hr, min, sec)
            startSec = hr * 60L * 60L + min * 60L + sec
        }
    }

    override fun handleTimeParams(time: Long) {
        val sec = time % 60
        val min = (time / 60) % 60
        val hr = time / 60 / 60
        setCountText(hr.toInt(), min.toInt(), sec.toInt())
        if(time == 0L) stopService()
    }

    override fun onSetTime(hr: Int, min: Int, sec: Int) {
        setCountText(hr, min, sec)
        startSec = hr * 60L * 60L + min * 60L + sec
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
