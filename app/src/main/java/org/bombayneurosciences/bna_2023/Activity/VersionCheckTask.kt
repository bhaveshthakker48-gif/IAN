package org.bombayneurosciences.bna_2023.Activity

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.widget.Toast
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class VersionCheckTask(private val context: Context) : AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg voids: Void?): String? {
        return try {
            val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(currentVersion: String?) {
        super.onPostExecute(currentVersion)

        if (currentVersion != null) {
            PlayStoreVersionCheckTask(context, currentVersion).execute()
        }
    }
}

class PlayStoreVersionCheckTask(private val context: Context, private val currentVersion: String) :
    AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg voids: Void?): String? {
        // Use the Play Core Library to check for the latest version on the Play Store
        val appUpdateManager = AppUpdateManagerFactory.create(context)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        return try {
            val appUpdateInfo: AppUpdateInfo = appUpdateInfoTask.result
            appUpdateInfo.availableVersionCode().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(playStoreVersion: String?) {
        super.onPostExecute(playStoreVersion)

        if (playStoreVersion != null && currentVersion != playStoreVersion) {
            showUpdateMessage()
        } else {
            // If you want to do something when the version is up-to-date, you can add it here
        }
    }

    private fun showUpdateMessage() {
        // Display an update message and initiate the in-app update
        Toast.makeText(context, "A new update is available. Please update the app.", Toast.LENGTH_LONG).show()

        val appUpdateManager = AppUpdateManagerFactory.create(context)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                // Start a flexible update
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    null,  // Pass the activity or fragment, or null if using startActivityForResult
                    1  // Your custom request code
                )
            }
        }
    }
}

private fun Any.startUpdateFlowForResult(appUpdateInfo: AppUpdateInfo?, flexible: Int, nothing: Nothing?, i: Int) {

}
