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

class ItemFragment : Fragment() {

    private val columnCount = 1
    private var listener: MyItemRecyclerViewAdapter.OnClickItem? = null

    init{
        listener = object : MyItemRecyclerViewAdapter.OnClickItem{
            override fun onClickItem(item: Record) {

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        if (view is RecyclerView) {
            with(view) {
                  layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyItemRecyclerViewAdapter(DaoFactory.getLocalDao(activity).getAllEventItems(), listener)
            }
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = ItemFragment
    }
}
