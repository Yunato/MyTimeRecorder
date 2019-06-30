package io.github.yunato.myrecordtimer.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.databinding.FragmentMainBinding
import io.github.yunato.myrecordtimer.ui.activity.EditRecordActivity
import io.github.yunato.myrecordtimer.ui.activity.MainActivity
import io.github.yunato.myrecordtimer.ui.activity.TimerActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    override fun onCreateView( inflater: LayoutInflater,
                               container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).nav_view.menu.getItem(0).isChecked = true
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
                    if(getPermissionsStatus().isEmpty()){
                        data?.let{
                            startActivity(
                                EditRecordActivity.intent(
                                    activity as Context,
                                    it.getParcelableExtra(EditRecordActivity.EXTRA_RECORD),
                                    it.getParcelableArrayListExtra(EditRecordActivity.EXTRA_SUB_RECORD)))
                        }
                    }else{
                        Toast.makeText(activity, activity?.resources?.getString(R.string.toast_message_permission), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun getPermissionsStatus(): ArrayList<String>{
        val permissionExtStorage = ContextCompat.checkSelfPermission(
            activity ?: throw IllegalStateException("Activity is Null"), Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permissionReadCalendar = ContextCompat.checkSelfPermission(
            activity ?: throw IllegalStateException("Activity is Null"), Manifest.permission.READ_CALENDAR
        )
        val permissionWriteCalendar = ContextCompat.checkSelfPermission(
            activity ?: throw IllegalStateException("Activity is Null"), Manifest.permission.WRITE_CALENDAR
        )

        val reqPermissions = ArrayList<String>()
        if (PackageManager.PERMISSION_GRANTED != permissionExtStorage) {
            reqPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (PackageManager.PERMISSION_GRANTED != permissionReadCalendar) {
            reqPermissions.add(Manifest.permission.READ_CALENDAR)
        }
        if (PackageManager.PERMISSION_GRANTED != permissionWriteCalendar) {
            reqPermissions.add(Manifest.permission.WRITE_CALENDAR)
        }
        return reqPermissions
    }

    companion object {
        @JvmStatic val REQUEST_TIMER = 1

        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
