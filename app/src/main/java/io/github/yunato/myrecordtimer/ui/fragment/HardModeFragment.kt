package io.github.yunato.myrecordtimer.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import kotlinx.android.synthetic.main.fragment_hard_mode.*

class HardModeFragment : Fragment(), SensorEventListener {

    private val manager: SensorManager by lazy{
        activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private var isRegisteredSensor: Boolean = false
    private var isClearGravCond: Boolean = false
    private var isClearProxCond: Boolean = false
    private var isFirstTime: Boolean = true

    override fun onCreateView( inflater: LayoutInflater,
                               container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {
        setReceiver()
        return inflater.inflate(R.layout.fragment_hard_mode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        isRegisteredSensor = false
        val sensorLists = mutableListOf(manager.getSensorList(Sensor.TYPE_GRAVITY), manager.getSensorList(Sensor.TYPE_PROXIMITY))
        for(sensorList in sensorLists){
            if(sensorList.size > 0){
                val sensor = sensorList[0]
                isRegisteredSensor = manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        intent = null
        activity?.unregisterReceiver(timerReceiver)
        if(isRegisteredSensor){
            manager.unregisterListener(this)
            isRegisteredSensor = false
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let{
            if(it.sensor?.type == Sensor.TYPE_GRAVITY){
                isClearGravCond = it.values[2] < -9.75
            }else if(it.sensor?.type == Sensor.TYPE_PROXIMITY){
                isClearProxCond = it.values[0] < 1.0
            }
            if(isClearGravCond && isClearProxCond && isFirstTime) {
                startService()
                isFirstTime = false
            }else if(!isClearGravCond && !isFirstTime){
                TimerIntentService.isContinue = false
            }
        }
    }

    private fun startService(){
        intent = TimerIntentService.startActionCountUp(activity as Context)
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
        }
    }

    private fun setCountText(hr: Int, min: Int, sec: Int){
        textView_time.text = String.format("%02d:%02d:%02d", hr, min, sec)
    }

    companion object {
        @JvmStatic private var intent: Intent? = null
        @JvmStatic private var timerReceiver: TimerReceiver = TimerReceiver()

        @JvmStatic
        fun newInstance() = HardModeFragment()
    }
}
