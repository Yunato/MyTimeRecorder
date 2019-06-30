package io.github.yunato.myrecordtimer.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.model.dao.calendars.DaoFactory
import io.github.yunato.myrecordtimer.model.dao.sqlite.DatabaseOpenHelper
import io.github.yunato.myrecordtimer.model.dao.sqlite.RecordDBAdapter
import io.github.yunato.myrecordtimer.model.entity.Record
import kotlinx.android.synthetic.main.fragment_edit_record.*

private const val ARG_RECORD = "io.github.yunato.myrecordtimer.ui.fragment.ARG_RECORD"
private const val ARG_RECORDS = "io.github.yunato.myrecordtimer.ui.fragment.ARG_RECORDS"

class EditRecordFragment : Fragment() {
    private var record: Record = Record(null, 0, 0, null, null, -1, 0)
    private var records: ArrayList<Record> = arrayListOf()
    private lateinit var fragment: SubEditListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            record = it.getParcelable(ARG_RECORD)
            records = it.getParcelableArrayList(ARG_RECORDS)
        }
    }

    override fun onCreateView( inflater: LayoutInflater,
                               container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment = SubEditListFragment.newInstance(records)
        childFragmentManager.beginTransaction().replace(R.id.content, fragment).commit()

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
        val addRecords: MutableList<Record> = mutableListOf(Record(record.id, record.start, record.end, title ?: "(タイトルなし)", memo ?: "", record.eval, record.subs))

        if(fragment.statuses.size != 0){
            for(i in 0 until fragment.statuses.size){
                if(fragment.statuses[i].isChecked){
                    addRecords.add(Record(records[i].id, records[i].start, records[i].end, fragment.statuses[i].title, "", -1, 0))
                }
            }
        }

        val strEventIds = DaoFactory.getLocalDao(activity).insertEventItems(addRecords)
        val lnEventIds: MutableList<Long> = mutableListOf()
        for(str in strEventIds){
            lnEventIds.add(str.toLong())
        }
        RecordDBAdapter(activity as Context).addOperations(DatabaseOpenHelper.OPE_ADD, lnEventIds)
        activity?.setResult(Activity.RESULT_OK)
        activity?.finish()
    }

    companion object {
        @JvmStatic
        fun newInstance(record: Record, records: ArrayList<Record>) =
            EditRecordFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_RECORD, record)
                    putParcelableArrayList(ARG_RECORDS, records)
                }
            }
    }
}
