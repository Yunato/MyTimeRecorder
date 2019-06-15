package io.github.yunato.myrecordtimer.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myrecordtimer.R

class EasyModeFragment : Fragment() {

    override fun onCreateView( inflater: LayoutInflater,
                               container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_easy_mode, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = EasyModeFragment()
    }
}
