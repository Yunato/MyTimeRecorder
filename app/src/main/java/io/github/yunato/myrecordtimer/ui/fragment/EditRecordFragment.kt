package io.github.yunato.myrecordtimer.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.model.entity.Record

private const val ARG_RECORD = "param1"

class EditRecordFragment : Fragment() {
    private var record: Record? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            record = it.getParcelable(ARG_RECORD)
        }
    }

    override fun onCreateView( inflater: LayoutInflater,
                               container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_record, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(record: Record) =
            EditRecordFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_RECORD, record)
                }
            }
    }
}
