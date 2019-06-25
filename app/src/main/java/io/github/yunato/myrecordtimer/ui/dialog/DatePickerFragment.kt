package io.github.yunato.myrecordtimer.ui.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.widget.DatePicker
import java.util.*

class DatePickerFragment: DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONDAY)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog((activity as Context), this, year, month, day)
    }

    interface OnSetDateListener{
        fun onSetDate(year: Int, month: Int, dayOfMonth: Int)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        getListener()?.onSetDate(year, month, dayOfMonth)
    }

    private fun getListener(): OnSetDateListener?{
        return try {
            targetFragment as OnSetDateListener
        }catch (e: ClassCastException){
            null
        }
    }

    companion object {
        @JvmStatic val KEY_YEAR: String = "io.github.yunato.myrecordtimer.ui.dialog.KEY_YEAR"
        @JvmStatic val KEY_MONTH: String = "io.github.yunato.myrecordtimer.ui.dialog.KEY_MONTH"
        @JvmStatic val KEY_DAY: String = "io.github.yunato.myrecordtimer.ui.dialog.KEY_DAY"

        @JvmStatic fun newInstance(fragment: Fragment): DatePickerFragment{
            return DatePickerFragment().apply{
                setTargetFragment(fragment, 0)
            }
        }
    }
}
