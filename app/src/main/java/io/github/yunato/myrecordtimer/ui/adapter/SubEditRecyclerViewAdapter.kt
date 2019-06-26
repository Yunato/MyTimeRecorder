package io.github.yunato.myrecordtimer.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.model.entity.Record

import kotlinx.android.synthetic.main.fragment_sub_edit_record.view.*

class SubEditRecyclerViewAdapter(private val mValues: List<Record?>)
    : RecyclerView.Adapter<SubEditRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_sub_edit_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        item?.let{
            holder.mTitleView.text = ""
            holder.mLenView.text = getLenString(item.end - item.start)
        }
    }

    private fun getLenString(diff: Long): String{
        val time = diff / 1000L
        val sec = time % 60
        val min = (time / 60) % 60
        val hr = time / 60 / 60
        return String.format("%02d:%02d:%02d", hr, min, sec)
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val mCheckBox: CheckBox = mView.item_checkbox
        val mTitleView: TextView = mView.item_edit_text_title
        val mLenView: TextView = mView.item_text_view_length
    }
}
