package io.github.yunato.myrecordtimer.ui.adapter

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.model.entity.Record
import io.github.yunato.myrecordtimer.model.entity.SubEditState

import kotlinx.android.synthetic.main.fragment_sub_edit_record.view.*

class SubEditRecyclerViewAdapter(private val mValues: List<Record>,
                                 private val mStatus: List<SubEditState>)
    : RecyclerView.Adapter<SubEditRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_sub_edit_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIndexText.text = String.format("%d. ", position + 1)
        holder.mTitleEdit.setText("", TextView.BufferType.NORMAL)
        holder.mLenView.text = getLenString(item.end - item.start)
        holder.mCheckBox.setOnCheckedChangeListener{view, isChecked ->
            mStatus[position].isChecked = isChecked
        }
        holder.mTitleEdit.afterTextChanged{
            mStatus[position].title = it
        }
    }

    private fun getLenString(diff: Long): String{
        val time = diff / 1000L
        val sec = time % 60
        val min = (time / 60) % 60
        val hr = time / 60 / 60
        return String.format("%02d:%02d:%02d", hr, min, sec)
    }

    fun EditText.afterTextChanged(callback: (String) -> Unit){
        this.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                callback.invoke(s.toString())
            }
        })
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val mCheckBox: CheckBox = mView.item_checkbox
        val mIndexText: TextView = mView.item_index
        val mTitleEdit: EditText = mView.item_edit_text_title
        val mLenView: TextView = mView.item_text_view_length
    }
}
