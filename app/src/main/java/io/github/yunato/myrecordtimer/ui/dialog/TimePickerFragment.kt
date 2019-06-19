package io.github.yunato.myrecordtimer.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import io.github.yunato.myrecordtimer.R
import kotlinx.android.synthetic.main.dialog_time_picker.view.*

class TimePickerFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val view = createPickerView()
        return AlertDialog.Builder(activity as Context).apply {
            setView(view)
            setTitle(R.string.time_picekr_title)
            setPositiveButton(android.R.string.ok){ _,_ ->
                val hour = view.findViewById<NumberPicker>(R.id.picker_hour).value
                val minute = view.findViewById<NumberPicker>(R.id.picker_minute).value
                val second = view.findViewById<NumberPicker>(R.id.picker_second).value
                getListener()?.onSetTime(hour, minute, second)
            }
            setNegativeButton(android.R.string.cancel){ _,_ ->
                activity?.finish()
            }
        }.create()
    }

    override fun onStart() {
        super.onStart()

        dialog.setCanceledOnTouchOutside(false)
        val decor: View = dialog.window.decorView
        decor.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    private fun getListener(): OnSetTimeListener?{
        return try {
            targetFragment as OnSetTimeListener
        }catch (e: ClassCastException){
            null
        }
    }

    private fun createPickerView(): View {
        return LayoutInflater.from(activity).inflate(R.layout.dialog_time_picker, null).apply{
            fun NumberPicker.setup(max: Int, key: String){
                minValue = 0
                maxValue = max
                value = getValueFromBundle(key)
                setFormatter{ String.format("%02d", it) }
            }
            picker_hour.setup(23, KEY_HOUR)
            picker_minute.setup(59, KEY_MINUTE)
            picker_second.setup(59, KEY_SECOND)
        }
    }

    private fun getValueFromBundle(key: String): Int{
        (arguments ?: Bundle()).apply{
            return if(this.containsKey(key)) this.getInt(key) else 0
        }
    }

    private fun setValueToBundle(hr: Int, min: Int, sec: Int){
        val args = arguments ?: Bundle().apply{
            putInt("hour", hr)
            putInt("minute", min)
            putInt("second", sec)
        }
        arguments = args
    }

    fun setParams(hr: Int, min: Int, sec: Int){
        setValueToBundle(hr, min, sec)
    }

    interface OnSetTimeListener{
        fun onSetTime(hr: Int, min: Int, sec: Int)
    }

    companion object {
        @JvmStatic val KEY_HOUR: String = "io.github.yunato.myrecordtimer.ui.dialog.KEY_HOUR"
        @JvmStatic val KEY_MINUTE: String = "io.github.yunato.myrecordtimer.ui.dialog.KEY_MINUTE"
        @JvmStatic val KEY_SECOND: String = "io.github.yunato.myrecordtimer.ui.dialog.KEY_SECOND"

        @JvmStatic fun newInstance(fragment: Fragment): TimePickerFragment{
            return TimePickerFragment().apply{
                setTargetFragment(fragment, 0)
            }
        }
    }
}
