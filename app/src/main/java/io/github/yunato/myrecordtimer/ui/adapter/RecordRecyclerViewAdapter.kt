package io.github.yunato.myrecordtimer.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.model.entity.Record
import kotlinx.android.synthetic.main.fragment_record.view.*
import java.text.SimpleDateFormat
import java.util.*

class RecordRecyclerViewAdapter(val mValues: MutableList<Record>,
                                private val mListener: OnClickItem?,
                                private val mLongListener: OnLongClickItem?)
    : RecyclerView.Adapter<RecordRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private val mOnLongClickListener: View.OnLongClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val pos = v.tag as Int
            val item = mValues[pos]
            mListener?.onClickItem(item)
        }
        mOnLongClickListener = View.OnLongClickListener { v ->
            val pos = v.tag as Int
            val item = mValues[pos]
            mLongListener?.onLongClickItem(item, pos)
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        if(item.id != null){
            holder.mDateView.text = getTimeString(item.start)
            holder.mLenView.text = getLenString(item.end - item.start)
            holder.mTitleView.text = item.title

            with(holder.mView) {
                tag = position
                setOnClickListener(mOnClickListener)
                setOnLongClickListener(mOnLongClickListener)
            }
        }else{
            holder.mDateView.text = getDateString(item.start)
        }
    }

    private fun getDateString(time: Long): String{
        return SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN).format(Date().apply{
            this.time = time
        })
    }

    private fun getTimeString(time: Long): String{
        return SimpleDateFormat("HH:mm", Locale.JAPAN).format(Date().apply{
            this.time = time
        })
    }

    private fun getLenString(diff: Long): String{
        val time = diff / 1000L
        val sec = time % 60
        val min = (time / 60) % 60
        val hr = time / 60 / 60
        return String.format("%02d:%02d:%02d", hr, min, sec)
    }

    override fun getItemCount(): Int = mValues.size

    interface OnClickItem {
        fun onClickItem(item: Record)
    }

    interface OnLongClickItem {
        fun onLongClickItem(item: Record, position: Int)
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mDateView: TextView = mView.item_date
        val mLenView: TextView = mView.item_len
        val mTitleView: TextView = mView.item_title
    }
}
