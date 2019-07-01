package io.github.yunato.myrecordtimer.ui.activity

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import com.stephentuso.welcome.*
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.ui.fragment.TutorialFragment
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class TutorialActivity : WelcomeActivity() {

    override fun configuration(): WelcomeConfiguration {
        return WelcomeConfiguration.Builder(this)
            .defaultBackgroundColor(BackgroundColor(Color.WHITE))
            .page(object : FragmentWelcomePage(){
                override fun fragment(): Fragment {
                    return TutorialFragment.newInstance(R.layout.fragment_tutorial_abstract)
                }
            })
            .page(object : FragmentWelcomePage(){
                override fun fragment(): Fragment {
                    return TutorialFragment.newInstance(R.layout.fragment_tutorial_method)
                }
            })
            .page(object : FragmentWelcomePage(){
                override fun fragment(): Fragment {
                    return TutorialFragment.newInstance(R.layout.fragment_tutorial_permission)
                }
            })
            .swipeToDismiss(false)
            .canSkip(false)
            .useCustomDoneButton(true)
            .build()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    companion object {
        fun showIfNeeded(activity: Activity, savedInstanceState: Bundle?) {
            WelcomeHelper(activity, TutorialActivity::class.java).show(savedInstanceState)
        }
    }
}
