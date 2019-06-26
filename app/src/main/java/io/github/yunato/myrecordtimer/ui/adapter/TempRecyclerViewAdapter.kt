package io.github.yunato.myrecordtimer.ui.adapter

import android.annotation.SuppressLint
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.model.entity.Record
import kotlinx.android.synthetic.main.fragment_temp.view.*
import java.text.SimpleDateFormat
import java.util.*

class TempRecyclerViewAdapter(private val mValues: MutableList<Record>,
                              private val longClickListener: OnLongClickItem?)
    : RecyclerView.Adapter<TempRecyclerViewAdapter.ViewHolder>() {

    private val mOnLongClickListener: View.OnLongClickListener
    private val handler: Handler = Handler()
    private var runnable: Runnable? = null
    private val period: Long = 100

    init {
        mOnLongClickListener = View.OnLongClickListener { v ->
            val item = v.tag as Record
            longClickListener?.onLongClickItem(item)
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_temp, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdText.text = (mValues.size - position).toString() 
        holder.mTimeText.text = getDateString(item.start)

        if(position == 0){
            runnable = Runnable {
                val now = Date().time
                mValues[0] = Record(null, mValues[0].start, now, null, null, -1)
                holder.mDiffText.text = getLenString(now - item.start)
                handler.postDelayed(runnable, period)
            }
            handler.post(runnable)
        }else{
            holder.mDiffText.text = getLenString(item.end - item.start)
        }

        with(holder.mView) {
            tag = item
            setOnLongClickListener{
                true
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateString(time: Long): String{
        return SimpleDateFormat("HH:mm:ss").format(Date().apply{
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

    fun finishTimer() = handler.removeCallbacks(runnable)

    interface OnLongClickItem {
        fun onLongClickItem(item: Record)
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdText: TextView = mView.item_id
        val mTimeText: TextView = mView.item_time
        val mDiffText: TextView = mView.item_diff
    }
}
