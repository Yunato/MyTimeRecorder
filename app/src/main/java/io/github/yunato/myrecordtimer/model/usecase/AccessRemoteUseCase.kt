package io.github.yunato.myrecordtimer.model.usecase

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class AccessRemoteUseCase(val context: Context) {

    fun run() {
        if (isGooglePlayServicesUnavailable()) {
            acquireGooglePlayServices()
        } else {
            callGoogleApi()
        }
    }

    fun callGoogleApi(){

    }

    private fun isGooglePlayServicesUnavailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context)
        return ConnectionResult.SUCCESS != connectionStatusCode
    }

    private fun acquireGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context)
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
        }
    }

    fun showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode: Int) {
        val apiAvailability = GoogleApiAvailability.getInstance()
        apiAvailability.getErrorDialog(
            context as Activity,
            connectionStatusCode,
            REQUEST_GOOGLE_PLAY_SERVICES
        ).show()
    }

    fun showUserRecoverableAuthDialog(intent: Intent) {
        (context as Activity).startActivityForResult(intent, REQUEST_AUTHORIZATION)
    }

    companion object{
        const val REQUEST_ACCOUNT_PICKER = 1000
        const val REQUEST_AUTHORIZATION = 1001
        const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
        const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003
    }
}
