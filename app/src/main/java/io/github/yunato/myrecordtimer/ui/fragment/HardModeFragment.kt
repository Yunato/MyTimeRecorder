package io.github.yunato.myrecordtimer.ui.fragment

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.PowerManager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.other.service.TimerIntentService
import io.github.yunato.myrecordtimer.other.timer.MyCountDownTimer
import io.github.yunato.myrecordtimer.ui.view.TimerTextView
import kotlinx.android.synthetic.main.fragment_hard_mode.*
import java.text.SimpleDateFormat
import java.util.*

class HardModeFragment : ModeFragment(), SensorEventListener {

    private val manager: SensorManager by lazy{
        activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val powerManager: PowerManager by lazy{
        activity?.getSystemService(Context.POWER_SERVICE) as PowerManager
    }
    private val wakeLock: PowerManager.WakeLock by lazy{
        powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "myrecordtimer:mytag")
    }
    private var isRegisteredSensor: Boolean = false
    private var isClearGravCond: Boolean = false
    private var isClearProxCond: Boolean = false
    private var isRunning: Boolean = false
    private var isFirstTime: Boolean = true
    private var timer: MyCountDownTimer? = null
    override val resource: Int = R.layout.fragment_hard_mode

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.setOnKeyListener{v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && isRunning) {
                this.createLap()
                false
            }else {
                false
            }
        }
        view?.isFocusableInTouchMode = true
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView_time.setParam(TimerTextView.MODE_UP)
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
            if(!isRunning){
                if(isClearGravCond && isClearProxCond) {
                    if(isFirstTime){
                        startService(TimerIntentService.startActionCountUp(activity as Context, startSec))
                        isFirstTime = false
                    }
                    wakeLock.acquire()
                    isRunning = true
                    timer?.cancel()
                }
            }else{
                if(!isClearGravCond) {
                    wakeLock.release()
                    isRunning = false
                    startInterruptionTimer()
                }
            }
        }
    }

    private fun startInterruptionTimer(){
        val millisInFuture = 5 * 1000L
        val interval = 200L
        timer?.cancel()
        timer = MyCountDownTimer(millisInFuture, interval)
        timer?.setOnProgressListener(object: MyCountDownTimer.OnProgressListener{
            override fun onProgress(time: Long) {
                time_view_status.text = String.format("%s%s",
                    SimpleDateFormat("s", Locale.JAPAN).format(time + 999L),
                    resources.getString(R.string.text_view_hard_mode_stop))
                if(time == 0L){
                    stopService()
                }
            }
        })
        timer?.start()
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
        fun newInstance() = HardModeFragment()
    }
}
