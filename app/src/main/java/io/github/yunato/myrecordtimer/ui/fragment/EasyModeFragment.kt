package io.github.yunato.myrecordtimer.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.other.service.TimerIntentService
import kotlinx.android.synthetic.main.fragment_easy_mode.*

class EasyModeFragment : Fragment() {

    override fun onCreateView( inflater: LayoutInflater,
                               container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_easy_mode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_start_end.setOnClickListener{
            TimerIntentService.startActionCountDown(activity as Context, "hoge", "fuga")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = EasyModeFragment()
    }
}
