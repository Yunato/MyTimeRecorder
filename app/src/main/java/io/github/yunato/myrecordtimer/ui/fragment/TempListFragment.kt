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
import io.github.yunato.myrecordtimer.model.entity.Record
import io.github.yunato.myrecordtimer.ui.adapter.TempRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_temp_list.*

class TempListFragment : Fragment() {

    val records: MutableList<Record> = mutableListOf()
    private var columnCount = 1
    private var longClickListener: TempRecyclerViewAdapter.OnLongClickItem? = null
    private lateinit var recyclerView: RecyclerView

    init{
        longClickListener = object : TempRecyclerViewAdapter.OnLongClickItem{
            override fun onLongClickItem(item: Record) {

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_temp_list, container, false)

        if (view is RecyclerView) {
            recyclerView = view
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter =
                    TempRecyclerViewAdapter(records, longClickListener)
            }
        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        (recyclerView.adapter as TempRecyclerViewAdapter).finishTimer()
    }

    fun addRecord(record: Record){
        records.add(0, record)
        (recyclerView.adapter as TempRecyclerViewAdapter).finishTimer()
        list.adapter = TempRecyclerViewAdapter(records, longClickListener)
    }

    companion object {
        @JvmStatic
        fun newInstance() = TempListFragment()
    }
}
