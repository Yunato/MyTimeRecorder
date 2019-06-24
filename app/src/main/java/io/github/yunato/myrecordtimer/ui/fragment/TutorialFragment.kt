package io.github.yunato.myrecordtimer.ui.fragment

import android.Manifest.permission.*
import android.accounts.AccountManager
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stephentuso.welcome.WelcomeFinisher
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.model.dao.DaoFactory
import io.github.yunato.myrecordtimer.model.usecase.AccessRemoteUseCase
import kotlinx.android.synthetic.main.fragment_tutorial.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class TutorialFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView( inflater: LayoutInflater,
                               container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_done.setOnClickListener{
            val reqPermissions = getPermissionsStatus()
            if(reqPermissions.isNotEmpty()){
                requestPermissions(reqPermissions.toArray(arrayOfNulls(reqPermissions.size)), REQUEST_MULTI_PERMISSIONS)
                checkGooglePermissions()
            }else{
                WelcomeFinisher(this).finish()
            }
        }
    }

    @AfterPermissionGranted(REQUEST_MULTI_PERMISSIONS)
    private fun checkGooglePermissions() = chooseAccount()

    private fun getPermissionsStatus(): ArrayList<String>{
        val permissionExtStorage = ContextCompat.checkSelfPermission(
            activity ?: throw IllegalStateException("Activity is Null"), WRITE_EXTERNAL_STORAGE)
        val permissionReadCalendar = ContextCompat.checkSelfPermission(
            activity ?: throw IllegalStateException("Activity is Null"), READ_CALENDAR)
        val permissionWriteCalendar = ContextCompat.checkSelfPermission(
            activity ?: throw IllegalStateException("Activity is Null"), WRITE_CALENDAR)

        val reqPermissions = ArrayList<String>()
        if (PackageManager.PERMISSION_GRANTED != permissionExtStorage) {
            reqPermissions.add(WRITE_EXTERNAL_STORAGE)
        }
        if (PackageManager.PERMISSION_GRANTED != permissionReadCalendar) {
            reqPermissions.add(READ_CALENDAR)
        }
        if (PackageManager.PERMISSION_GRANTED != permissionWriteCalendar) {
            reqPermissions.add(WRITE_CALENDAR)
        }
        return reqPermissions
    }

    @AfterPermissionGranted(AccessRemoteUseCase.REQUEST_PERMISSION_GET_ACCOUNTS)
    private fun chooseAccount(){
        if(EasyPermissions.hasPermissions(activity, GET_ACCOUNTS)){
            if(DaoFactory.getRemoteDao(activity).setAccountName(null)){
                WelcomeFinisher(this).finish()
            }else{
                startActivityForResult(DaoFactory.getRemoteDao(activity).getChooseAccountIntent(),
                    AccessRemoteUseCase.REQUEST_ACCOUNT_PICKER)
            }
        }else{
            EasyPermissions.requestPermissions(this, activity?.resources?.getString(R.string.permission_get_account),
                AccessRemoteUseCase.REQUEST_PERMISSION_GET_ACCOUNTS, GET_ACCOUNTS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode != AccessRemoteUseCase.REQUEST_PERMISSION_GET_ACCOUNTS && requestCode != REQUEST_MULTI_PERMISSIONS) return

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == AccessRemoteUseCase.REQUEST_ACCOUNT_PICKER){
            if(resultCode == RESULT_OK && data != null && data.extras != null){
                val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                DaoFactory.getRemoteDao(activity).setAccountName(accountName)
                checkGooglePermissions()
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {}

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {}

    companion object {
        private const val REQUEST_MULTI_PERMISSIONS: Int = 1

        @JvmStatic
        fun newInstance() = TutorialFragment()
    }
}
