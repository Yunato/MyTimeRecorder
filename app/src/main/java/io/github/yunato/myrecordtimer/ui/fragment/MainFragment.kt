package io.github.yunato.myrecordtimer.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myrecordtimer.databinding.FragmentMainBinding
import io.github.yunato.myrecordtimer.ui.activity.TimerActivity
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    override fun onCreateView( inflater: LayoutInflater,
                               container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        card_easy_mode_fixed.setOnClickListener{
            TimerActivity.intent(activity as Context, TimerActivity.EXTRA_MODE_EASY_FIXED).let{
                startActivity( it )
            }
        }
        card_normal_mode.setOnClickListener{
            TimerActivity.intent(activity as Context, TimerActivity.EXTRA_MODE_NORMAL).let{
                startActivity( it )
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
