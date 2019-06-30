package io.github.yunato.myrecordtimer.ui.fragment

import android.app.AlertDialog
import android.content.Context
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
import io.github.yunato.myrecordtimer.model.dao.sqlite.DatabaseOpenHelper
import io.github.yunato.myrecordtimer.model.dao.sqlite.RecordDBAdapter
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
    private var longListener: RecordRecyclerViewAdapter.OnLongClickItem? = null

    init{
        listener = object : RecordRecyclerViewAdapter.OnClickItem{
            override fun onClickItem(item: Record) {
                if(item.id == null) return
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.content, SubRecordListFragment.newInstance(item))
                    ?.addToBackStack(TRANSITION_ID)?.commit()
            }
        }
        longListener = object : RecordRecyclerViewAdapter.OnLongClickItem{
            override fun onLongClickItem(item: Record, position: Int) {
                if(item.id == null) return
                AlertDialog.Builder(activity)
                    .setMessage(activity?.resources?.getString(R.string.click_delete_record_message))
                    .setPositiveButton(activity?.resources?.getString(R.string.button_ok)){_, _ ->
                        val year = getDateParam(item, Calendar.YEAR)
                        val month = getDateParam(item, Calendar.MONTH)
                        val dayOfMonth = getDateParam(item, Calendar.DAY_OF_MONTH)
                        val records = extractSubRecord(item, DaoFactory.getLocalDao(activity).getEventItemsOnDay(year, month, dayOfMonth))
                        records.add(0, item)
                        val lnEventIds: MutableList<Long> = mutableListOf()
                        for(record in records){
                            DaoFactory.getLocalDao(activity).deleteEventItem((record.id ?: "-1").toLong())
                            lnEventIds.add((record.id ?: "-1").toLong())
                        }
                        RecordDBAdapter(activity as Context).addOperations(DatabaseOpenHelper.OPE_DELETE, lnEventIds)
                        val mAdapter = list.adapter as RecordRecyclerViewAdapter
                        reloadList(position)
                        var existFormer = true
                        var existLatter = true
                        if(position < mAdapter.mValues.size){
                            if(mAdapter.mValues[position].id == null)
                                existLatter = false
                        }else{
                            existLatter = false
                        }
                        if(mAdapter.mValues[position - 1].id == null){
                            existFormer = false
                        }
                        if(!existFormer && !existLatter) reloadList(position - 1)
                    }
                    .setNegativeButton(activity?.resources?.getString(R.string.button_cancel), null)
                    .show()
            }
        }
    }

    private fun reloadList(position: Int){
        (list.adapter as RecordRecyclerViewAdapter).mValues.removeAt(position)
        list.adapter?.notifyItemRemoved(position)
        list.adapter?.notifyItemRangeRemoved(position, (list.adapter as RecordRecyclerViewAdapter).mValues.size)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mode = it.getInt(ARG_MODE)
            if(mode == MODE_SUB){
                mRecord = it.getParcelable(ARG_RECORD)
                year = getDateParam(mRecord, Calendar.YEAR)
                month = getDateParam(mRecord, Calendar.MONTH)
                dayOfMonth = getDateParam(mRecord, Calendar.DAY_OF_MONTH)
            }
        }
    }

    private fun getDateParam(record: Record, field: Int): Int{
        val c = Calendar.getInstance()
        c.time = Date().also {
            it.time = record.start
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
                            doPreProcessForListItems(DaoFactory.getLocalDao(activity).getAllEventItems()).toMutableList(),
                            listener, longListener)
                    MODE_SUB ->
                        adapter = RecordRecyclerViewAdapter(
                            extractSubRecord(mRecord, DaoFactory.getLocalDao(activity).getEventItemsOnDay(year, month, dayOfMonth)).toMutableList(),
                            listener, longListener)
                }
            }
        }
        return view
    }

    fun setList(year: Int, month: Int, dayOfMonth: Int){
        if(mode == MODE_MAIN){
            list.adapter = RecordRecyclerViewAdapter(
                doPreProcessForListItems(DaoFactory.getLocalDao(activity).getEventItemsOnDay(year, month, dayOfMonth)).toMutableList(),
                listener, longListener)
        }
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
        if(tmp == null && records.isNotEmpty()) rtnList.add(records[records.size - 1])
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

    private fun extractSubRecord(record: Record, records: List<Record>): MutableList<Record>{
        val rtnList = mutableListOf<Record>()
        val rId: Long = record.id?.toLong() ?: -1
        for(i in 0 until records.size){
            val id: Long = records[i].id?.toLong() ?: -1
            if(rId < 0 || id < 0 || rId >= id) continue
            if(record.start <= records[i].start && record.end >= records[i].end){
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
