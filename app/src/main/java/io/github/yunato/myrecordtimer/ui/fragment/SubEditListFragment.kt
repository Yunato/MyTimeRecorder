package io.github.yunato.myrecordtimer.ui.fragment

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
import io.github.yunato.myrecordtimer.model.entity.Record
import io.github.yunato.myrecordtimer.model.entity.SubEditState
import io.github.yunato.myrecordtimer.ui.adapter.SubEditRecyclerViewAdapter
import io.github.yunato.myrecordtimer.ui.other.DividerItemDecoration

class SubEditListFragment : Fragment() {

    private var columnCount = 1
    private lateinit var records: ArrayList<Record>
    private val subEditStates: ArrayList<SubEditState> = arrayListOf()
    val statuses: ArrayList<SubEditState>
        get() = subEditStates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            records = it.getParcelableArrayList(ARG_LIST)
        }
        for(i in 0 until records.size){
            subEditStates.add(SubEditState(true, ""))
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sub_edit_list, container, false)

        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = SubEditRecyclerViewAdapter(records, subEditStates)
            }
            view.isNestedScrollingEnabled = false
            view.addItemDecoration(DividerItemDecoration(activity as Context))
        }
        return view
    }

    companion object {
        @JvmStatic val ARG_LIST = "io.github.yunato.myrecordtimer.ui.fragment.ARG_LIST"

        @JvmStatic
        fun newInstance(records: ArrayList<Record>) = SubEditListFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_LIST, records)
                }
            }
    }
}
