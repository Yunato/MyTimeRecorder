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

class RecordListFragment : Fragment() {

    private val columnCount = 1
    private var listener: RecordRecyclerViewAdapter.OnClickItem? = null

    init{
        listener = object : RecordRecyclerViewAdapter.OnClickItem{
            override fun onClickItem(item: Record) {

            }
        }
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
                adapter = RecordRecyclerViewAdapter(
                    doPreProcessForListItems(DaoFactory.getLocalDao(activity).getAllEventItems()),
                    listener
                )
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
            if(record.start <= records[i + 1].start && record.end >= records[i + 1].end)
                tmp = record
            else
                tmp = null
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
                rtnList.add(Record(null, records[i].start, 0L, null, null, -1))
            }
            rtnList.add(records[i])
            i += 1
        }
        return rtnList
    }

    companion object {
        @JvmStatic
        fun newInstance() = RecordListFragment()
    }
}
