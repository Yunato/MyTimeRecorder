package io.github.yunato.myrecordtimer.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.other.broadcastreceiver.TimerReceiver
import io.github.yunato.myrecordtimer.other.service.TimerIntentService
import kotlinx.android.synthetic.main.fragment_easy_mode.*

class EasyModeFragment : Fragment() {

    private var intent: Intent? = null

    override fun onCreateView( inflater: LayoutInflater,
                               container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_easy_mode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_start_end.setOnClickListener{
            intent = TimerIntentService.startActionCountDown(activity as Context, "hoge", "fuga")
            intent.let{ activity?.startService(it) }
            setReceiver()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        intent?.let{ activity?.stopService(it) }
    }

    private fun setReceiver(){
        val timerReceiver = TimerReceiver()
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
            val time = bundle.getLong(TimerReceiver.KEY_TIME)
            if(time == 0L) {
                intent?.let { activity?.stopService(it) }
            }else{
                val sec = (time / 1000) % 60
                val min = (time / 1000 / 60) % 60
                val hr = time / 1000 / 60 / 60
                setCountText(hr, min, sec)
            }
        }
    }

    private fun setCountText(hr: Long, min: Long, sec: Long){
        textView_time.text = String.format("%02d:%02d:%02d", hr, min, sec)
    }

    companion object {
        @JvmStatic
        fun newInstance() = EasyModeFragment()
    }
}
