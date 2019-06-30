package io.github.yunato.myrecordtimer.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.model.entity.Record
import kotlinx.android.synthetic.main.fragment_sub_record_list.*
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_RECORD = "io.github.yunato.myrecordtimer.ui.fragment.ARG_RECORD"

class SubRecordListFragment : Fragment() {
    private var record: Record = Record(null, 0, 0, null, null, -1, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            record = it.getParcelable(ARG_RECORD)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sub_record_list, container, false)
        view.isFocusableInTouchMode = true
        view.setOnKeyListener{_, keyCode, event->
            if(keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN){
                activity?.supportFragmentManager?.popBackStack(RecordListFragment.TRANSITION_ID, 0)
                true
            }else
                false
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_view_title.text = record.title
        text_view_date.text = String.format("%s/%s/%s", getDateParam(Calendar.YEAR), getDateParam(Calendar.MONTH) + 1, getDateParam(Calendar.DAY_OF_MONTH))
        text_view_length.text = getTimeStr((record.end - record.start) / 1000L)
        text_view_start.text = String.format("%s", getTimeFromDate(Date().apply{
            this.time = time
        }))
        if(record.memo.isNullOrBlank()) {
            layout_memo.visibility = View.GONE
        }else{
            text_view_memo.text = record.memo
        }

        val fragment = RecordListFragment.newInstance(record)
        childFragmentManager.beginTransaction().replace(R.id.content, fragment).commit()
    }

    private fun getDateParam(field: Int): Int{
        val c = Calendar.getInstance()
        c.time = Date().also {
            it.time = record.start
        }
        return c.get(field)
    }

    private fun getTimeFromDate(date: Date): String = SimpleDateFormat("HH:mm:ss", Locale.JAPAN).format(date)

    private fun getTimeStr(diffSec: Long): String{
        val sec = diffSec % 60
        val min = (diffSec / 60) % 60
        val hr = diffSec / 60 / 60
        return String.format("%02d:%02d:%02d", hr, min, sec)
    }

    companion object {
        @JvmStatic
        fun newInstance(record: Record) =
            SubRecordListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_RECORD, record)
                }
            }
    }
}
