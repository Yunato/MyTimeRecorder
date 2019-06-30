package io.github.yunato.myrecordtimer.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
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
import io.github.yunato.myrecordtimer.model.entity.Record
import io.github.yunato.myrecordtimer.other.broadcastreceiver.TimerReceiver
import io.github.yunato.myrecordtimer.other.service.TimerIntentService
import io.github.yunato.myrecordtimer.ui.activity.EditRecordActivity
import kotlinx.android.synthetic.main.fragment_easy_mode.*
import java.util.*

abstract class ModeFragment : Fragment() {

    abstract val resource: Int
    private var startTime: Long = 0L
    protected var startSec: Long = 0L
    protected lateinit var fragment: TempListFragment
    var isMeasuring: Boolean = false


    override fun onCreateView( inflater: LayoutInflater,
                               container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {
        setReceiver()
        return inflater.inflate(resource, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(timerReceiver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment = TempListFragment.newInstance()
        childFragmentManager.beginTransaction().replace(R.id.content, fragment).commit()
    }

    protected fun startService(intent: Intent){
        activity?.startService(intent)
        startTime = Date().time
        isMeasuring = true
    }

    protected fun stopService(){
        TimerIntentService.isContinue = false
        isMeasuring = false
        val endTime = Date().time
        val intent = Intent()
        intent.putExtra(EditRecordActivity.EXTRA_RECORD, Record(null, startTime, endTime, null, null, -1, 0))
        intent.putExtra(EditRecordActivity.EXTRA_SUB_RECORD, ArrayList(fragment.records))
        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
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
            handleTimeParams(bundle.getLong(TimerReceiver.KEY_TIME_SEC))
        }
    }

    abstract fun handleTimeParams(time: Long)

    protected fun setCountText(hr: Int, min: Int, sec: Int){
        textView_time.text = String.format("%02d:%02d:%02d", hr, min, sec)
    }

    protected fun createLap(){
        if(fragment.records.size == 0){
            val now = Date().time
            val record = Record(null, startTime, now, null, null, -1, 0)
            fragment.addRecord(record)
        }
        val now = Date().time
        val record = Record(null, now, now, null, null, -1, 0)
        fragment.addRecord(record)
    }

    companion object {
        @JvmStatic private var timerReceiver: TimerReceiver = TimerReceiver()
    }
}
