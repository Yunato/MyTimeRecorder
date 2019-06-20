package io.github.yunato.myrecordtimer.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.other.broadcastreceiver.TimerReceiver
import io.github.yunato.myrecordtimer.other.service.TimerIntentService
import io.github.yunato.myrecordtimer.ui.dialog.TimePickerFragment
import kotlinx.android.synthetic.main.fragment_easy_mode.*

class EasyModeFragment : Fragment(), TimePickerFragment.OnSetTimeListener {

    private var mode: Int = -1
    private var startSec: Long = 0
    private var isFirstTime : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            mode = it.getInt(ARG_MODE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setReceiver()
        return inflater.inflate(R.layout.fragment_easy_mode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_start_end.setOnClickListener{
            if(intent == null && isFirstTime){
                startService()
                isFirstTime = false
                button_start_end.setText(R.string.button_finish)
            }else{
                // TODO: Record
                TimerIntentService.isContinue = false
                activity?.finish()
            }
        }

        if(mode == MODE_FIXED){
            val picker = TimePickerFragment.newInstance(this)
            picker.setParams(0, 0, 30)
            picker.setStyle(DialogFragment.STYLE_NO_TITLE, 0)
            picker.show(fragmentManager, "time_picker")
        }else{
            // TODO: get param from preference
            val hr: Int = 0
            val min: Int = 5
            val sec: Int = 0
            setCountText(hr, min, sec)
            startSec = hr * 60L * 60L + min * 60L + sec
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        intent = null
        activity?.unregisterReceiver(timerReceiver)
    }

    private fun startService(){
        intent = TimerIntentService.startActionCountDown(activity as Context, startSec)
        intent.let { activity?.startService(it) }
    }

    private fun setReceiver(){
        timerReceiver = TimerReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(TimerReceiver.ACTION_UPDATE)
        activity?.registerReceiver(timerReceiver, intentFilter)
        timerReceiver.registerHandler(updateHandler)
    }

    private val updateHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            if(msg == null) return
            val bundle = msg.data
            val time = bundle.getLong(TimerReceiver.KEY_TIME_SEC)

            val sec = time % 60
            val min = (time / 60) % 60
            val hr = time / 60 / 60
            setCountText(hr.toInt(), min.toInt(), sec.toInt())

            if(time == 0L) intent = null
        }
    }

    private fun setCountText(hr: Int, min: Int, sec: Int){
        textView_time.text = String.format("%02d:%02d:%02d", hr, min, sec)
    }

    override fun onSetTime(hr: Int, min: Int, sec: Int) {
        setCountText(hr, min, sec)
        startSec = hr * 60L * 60L + min * 60L + sec
    }

    companion object {
        @JvmStatic private var intent: Intent? = null
        @JvmStatic private var timerReceiver: TimerReceiver = TimerReceiver()
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
