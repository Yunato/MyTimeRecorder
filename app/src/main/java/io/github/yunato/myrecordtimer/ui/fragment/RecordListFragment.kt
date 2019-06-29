package io.github.yunato.myrecordtimer.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.model.dao.calendars.DaoFactory
import io.github.yunato.myrecordtimer.model.entity.Record
import io.github.yunato.myrecordtimer.ui.adapter.RecordRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_record_list.*
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_MODE = "io.github.yunato.myrecordtimer.ui.fragment.ARG_MODE"
private const val ARG_RECORD = "io.github.yunato.myrecordtimer.ui.fragment.ARG_RECORD"

class RecordListFragment : Fragment() {

    private var mode: Int = 0
    private var mRecord: Record = Record(null, 0, 0, null, null, -1, -1)
    private var year: Int = 0
    private var month: Int = 0
    private var dayOfMonth: Int = 0
    private val columnCount = 1
    private var listener: RecordRecyclerViewAdapter.OnClickItem? = null

    init{
        listener = object : RecordRecyclerViewAdapter.OnClickItem{
            override fun onClickItem(item: Record) {
                if(item.id == null) return
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.content, SubRecordListFragment.newInstance(item))
                    ?.addToBackStack(TRANSITION_ID)?.commit()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mode = it.getInt(ARG_MODE)
            if(mode == MODE_SUB){
                mRecord = it.getParcelable(ARG_RECORD)
                year = getDateParam(Calendar.YEAR)
                month = getDateParam(Calendar.MONDAY)
                dayOfMonth = getDateParam(Calendar.DAY_OF_MONTH)
            }
        }
    }

    private fun getDateParam(field: Int): Int{
        val c = Calendar.getInstance()
        c.time = Date().also {
            it.time = mRecord.start
        }
        return c.get(field)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_record_list, container, false)

        if (view is RecyclerView) {
            with(view) {
                  layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                when(mode){
                    MODE_MAIN ->
                        adapter = RecordRecyclerViewAdapter(
                            doPreProcessForListItems(DaoFactory.getLocalDao(activity).getAllEventItems()),
                            listener)
                    MODE_SUB ->
                        adapter = RecordRecyclerViewAdapter(
                            extractSubRecord(DaoFactory.getLocalDao(activity).getEventItemsOnDay(year, month, dayOfMonth)),
                            listener
                        )
                }
            }
        }
        return view
    }

    fun setList(year: Int, month: Int, dayOfMonth: Int){
        list.adapter = RecordRecyclerViewAdapter(
            doPreProcessForListItems(DaoFactory.getLocalDao(activity).getEventItemsOnDay(year, month, dayOfMonth)),
            listener
        )
    }

    private fun doPreProcessForListItems(records: List<Record>): List<Record> = addCategories(excludeSubRecord(records))

    private fun excludeSubRecord(records: List<Record>): List<Record>{
        val rtnList = mutableListOf<Record>()
        var tmp: Record? = null
        for(i in 0 until records.size - 1){
            val record = tmp ?: records[i]
            if(tmp == null) rtnList.add(record)
            if(record.start <= records[i + 1].start && record.end >= records[i + 1].end) {
                tmp = record
                tmp.subs += 1
            }else{
                tmp = null
            }
        }
        if(tmp == null) rtnList.add(records[records.size - 1])
        return rtnList
    }

    private fun addCategories(records: List<Record>): List<Record>{
        val rtnList = mutableListOf<Record>()
        var i = 0
        var dateStr = ""
        while(i < records.size){
            val str = SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN).format(Date().apply{
                this.time = records[i].start
            })
            if(str != dateStr){
                dateStr = str
                rtnList.add(Record(null, records[i].start, 0L, null, null, -1, -1))
            }
            rtnList.add(records[i])
            i += 1
        }
        return rtnList
    }

    private fun extractSubRecord(records: List<Record>): List<Record>{
        val rtnList = mutableListOf<Record>()
        if(mode == MODE_MAIN) return rtnList
        val rId: Long = mRecord.id?.toLong() ?: -1
        for(i in 0 until records.size){
            val id: Long = records[i].id?.toLong() ?: -1
            if(rId < 0 || id < 0 || rId >= id) continue
            if(mRecord.start <= records[i].start && mRecord.end >= records[i].end){
                rtnList.add(records[i])
            }else{
                break
            }
        }
        return rtnList
    }

    companion object {
        @JvmStatic val TRANSITION_ID: String = "io.github.yunato.myrecordtimer.ui.fragment.TRANSITION_ID"
        @JvmStatic val MODE_MAIN: Int = 0
        @JvmStatic val MODE_SUB: Int = 1

        @JvmStatic
        fun newInstance() =
            RecordListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_MODE, MODE_MAIN)
                }
            }

        @JvmStatic
        fun newInstance(record: Record) =
            RecordListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_MODE, MODE_SUB)
                    putParcelable(ARG_RECORD, record)
                }
            }
    }
}
