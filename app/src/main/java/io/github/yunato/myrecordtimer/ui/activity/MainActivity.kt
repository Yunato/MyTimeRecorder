package io.github.yunato.myrecordtimer.ui.activity

import android.Manifest.permission.GET_ACCOUNTS
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.stephentuso.welcome.WelcomeHelper
import io.github.yunato.myrecordtimer.model.dao.calendars.DaoFactory
import io.github.yunato.myrecordtimer.model.dao.calendars.RemoteDao
import io.github.yunato.myrecordtimer.model.usecase.AccessRemoteUseCase
import io.github.yunato.myrecordtimer.ui.fragment.MainFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {

    private var mTask: MakeRequestTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(io.github.yunato.myrecordtimer.R.layout.activity_main)

        if (DaoFactory.getRemoteDao(this).setAccountName(null)) {
            startSyncCalendar()
        }
        TutorialActivity.showIfNeeded(this, savedInstanceState)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            io.github.yunato.myrecordtimer.R.string.navigation_drawer_open,
            io.github.yunato.myrecordtimer.R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(io.github.yunato.myrecordtimer.R.id.nav_measurement)
        onNavigationItemSelected(nav_view.menu.getItem(0))
    }

    override fun onDestroy() {
        super.onDestroy()
        mTask?.cancel(true)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        if (item.itemId == io.github.yunato.myrecordtimer.R.id.nav_setting) {
            startActivity(SettingsActivity.intent(this))
        } else {
            supportFragmentManager.beginTransaction()
                .replace(io.github.yunato.myrecordtimer.R.id.content, MainFragment.newInstance()).commit()
//        when (item.itemId) {
//            R.id.nav_measurement -> {
//
//            }
//            R.id.nav_view_record -> {
//
//            }
//            R.id.nav_setting -> {
//
//            }
//        }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    @AfterPermissionGranted(AccessRemoteUseCase.REQUEST_PERMISSION_GET_ACCOUNTS)
    private fun startSyncCalendar() {
        if (EasyPermissions.hasPermissions(this, GET_ACCOUNTS)) {
            if (DaoFactory.getRemoteDao(this).setAccountName(null)) {
                if (isGooglePlayServicesUnavailable()) {
                    acquireGooglePlayServices()
                } else {
                    mTask = MakeRequestTask(
                        DaoFactory.getRemoteDao(this),
                        object : MakeRequestTask.OnShowErrorDialog{
                            override fun onShowErrorDialog(statusCode: Int) {
                                showGooglePlayServicesAvailabilityErrorDialog(statusCode)
                            }
                        },
                        object : MakeRequestTask.OnShowAuthDialog{
                            override fun onShowAuthDialog(intent: Intent) {
                                showUserRecoverableAuthDialog(intent)
                            }
                        })
                    mTask?.execute()
                }
            } else {
                startActivityForResult(
                    DaoFactory.getRemoteDao(this).getChooseAccountIntent(),
                    AccessRemoteUseCase.REQUEST_ACCOUNT_PICKER
                )
            }
        } else {
            EasyPermissions.requestPermissions(
                this, resources.getString(io.github.yunato.myrecordtimer.R.string.permission_get_account),
                AccessRemoteUseCase.REQUEST_PERMISSION_GET_ACCOUNTS, GET_ACCOUNTS
            )
        }
    }

    private fun isGooglePlayServicesUnavailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
        return ConnectionResult.SUCCESS != connectionStatusCode
    }

    private fun acquireGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
        }
    }

    private fun showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode: Int) {
        val apiAvailability = GoogleApiAvailability.getInstance()
        apiAvailability.getErrorDialog(
            this,
            connectionStatusCode,
            AccessRemoteUseCase.REQUEST_GOOGLE_PLAY_SERVICES
        ).show()
    }

    private fun showUserRecoverableAuthDialog(intent: Intent) {
        startActivityForResult(intent, AccessRemoteUseCase.REQUEST_AUTHORIZATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            AccessRemoteUseCase.REQUEST_ACCOUNT_PICKER -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
                    val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                    DaoFactory.getRemoteDao(this).setAccountName(accountName)
                    startSyncCalendar()
                }
            }
            AccessRemoteUseCase.REQUEST_AUTHORIZATION -> {
                if (resultCode == Activity.RESULT_OK) startSyncCalendar()
            }
            WelcomeHelper.DEFAULT_WELCOME_SCREEN_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    DaoFactory.getLocalDao(this).createCalendar()
                    startSyncCalendar()
                }
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {}

    class MakeRequestTask(val dao: RemoteDao, val errorListener: OnShowErrorDialog,
                          val authListener: OnShowAuthDialog) : AsyncTask<Unit, Unit, Unit>() {
        var mLastError: Exception? = null

        override fun doInBackground(vararg params: Unit?) {
            dao.setAccountName(null)
            while (true) {
                try {
                    dao.createCalendar()
                } catch (e: java.lang.Exception) {
                    mLastError = e
                    cancel(true)
                }
                Log.d("TEST", "OK")
                Thread.sleep(500)
            }
        }

        override fun onCancelled() {
            if (mLastError == null) return

            if (mLastError is GooglePlayServicesAvailabilityIOException) {
                val exception = mLastError as GooglePlayServicesAvailabilityIOException?
                errorListener.onShowErrorDialog(exception?.connectionStatusCode ?: throw IllegalStateException("exception is null"))
            } else if (mLastError is UserRecoverableAuthIOException) {
                val exception = mLastError as UserRecoverableAuthIOException?
                authListener.onShowAuthDialog(exception?.intent ?: throw IllegalStateException("exception is null"))
            }
        }

        interface OnShowErrorDialog{
            fun onShowErrorDialog(statusCode: Int)
        }

        interface OnShowAuthDialog{
            fun onShowAuthDialog(intent: Intent)
        }
    }
}
