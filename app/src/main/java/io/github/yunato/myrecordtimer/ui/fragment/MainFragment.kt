package io.github.yunato.myrecordtimer.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myrecordtimer.databinding.FragmentMainBinding
import io.github.yunato.myrecordtimer.ui.activity.EditRecordActivity
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
            startActivityForResult(TimerActivity.intent(activity as Context, TimerActivity.EXTRA_MODE_EASY_FIXED), REQUEST_TIMER)
        }
        card_easy_mode_floated.setOnClickListener{
            startActivityForResult(TimerActivity.intent(activity as Context, TimerActivity.EXTRA_MODE_EASY_FLOATED), REQUEST_TIMER)
        }
        card_normal_mode.setOnClickListener{
            startActivityForResult(TimerActivity.intent(activity as Context, TimerActivity.EXTRA_MODE_NORMAL), REQUEST_TIMER)
        }
        card_hard_mode.setOnClickListener{
            startActivityForResult(TimerActivity.intent(activity as Context, TimerActivity.EXTRA_MODE_HARD), REQUEST_TIMER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQUEST_TIMER -> {
                if(resultCode == Activity.RESULT_OK){
                    data?.let{
                        startActivity(EditRecordActivity.intent(
                            activity as Context,
                            it.getParcelableExtra(EditRecordActivity.EXTRA_RECORD),
                            it.getParcelableArrayListExtra(EditRecordActivity.EXTRA_SUB_RECORD)))
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic val REQUEST_TIMER = 1

        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
