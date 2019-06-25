package io.github.yunato.myrecordtimer.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.ui.dialog.DatePickerFragment
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment(), DatePickerFragment.OnSetDateListener {

    private lateinit var fragment: RecordListFragment

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment = RecordListFragment.newInstance()
        childFragmentManager.beginTransaction().replace(R.id.content, fragment).commit()
        fab.setOnClickListener{
            val picker = DatePickerFragment.newInstance(this)
            picker.show(fragmentManager, "date_picker")

        }
    }

    override fun onSetDate(year: Int, month: Int, dayOfMonth: Int) {
        fragment.setList(year, month, dayOfMonth)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HistoryFragment()
    }
}
