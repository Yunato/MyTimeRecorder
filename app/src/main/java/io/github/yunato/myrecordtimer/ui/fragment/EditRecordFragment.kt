package io.github.yunato.myrecordtimer.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.model.entity.Record
import kotlinx.android.synthetic.main.fragment_edit_record.*

private const val ARG_RECORD = "param1"

class EditRecordFragment : Fragment() {
    private var record: Record = Record(null, 0, 0, null, null, -1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            record = it.getParcelable(ARG_RECORD)
        }
    }

    override fun onCreateView( inflater: LayoutInflater,
                               container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        record.let{
            val length = (it.end - it.start) / 1000L
            val sec = length % 60
            val min = (length / 60) % 60
            val hr = length / 60 / 60
            text_view_length.text = String.format("%02d:%02d:%02d", hr, min, sec)
        }
    }

    fun getSaveOnClickListener(): View.OnClickListener = View.OnClickListener {
        val title = if(edit_text_title.text.isBlank()) activity?.getString(R.string.edit_text_title_no)
                        else edit_text_title.text.toString()
        val memo = if(edit_text_memo.text.isBlank()) activity?.getString(R.string.edit_text_memo_no)
                        else edit_text_memo.text.toString()
        val addRecord = Record(record.id, record.start, record.end, title ?: "(タイトルなし)", memo ?: "", record.eval)
        // TODO: Save to Local Calendar
        activity?.setResult(Activity.RESULT_OK)
        activity?.finish()
    }

    companion object {
        @JvmStatic
        fun newInstance(record: Record) =
            EditRecordFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_RECORD, record)
                }
            }
    }
}
