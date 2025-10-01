package org.bombayneurosciences.bna_2023.Activity

import android.app.Activity
import android.content.Intent
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class InAppUpdateManager(
    private val activity: Activity,
    private val myUpdateRequestCode: Int,
    private val updateCheckCallback: () -> Unit
) {

    private val appUpdateManager: AppUpdateManager =
        AppUpdateManagerFactory.create(activity)
    private val installStateUpdatedListener =
        InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                // The update has been downloaded and is ready to be installed
                showUpdateDownloadedConfirmation()
            }
        }

    init {
        appUpdateManager.registerListener(installStateUpdatedListener)
    }

    fun checkForUpdates() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                // An update is available, prompt the user
                startAppUpdateFlexible(appUpdateInfo)
            }
        }
    }

    private fun startAppUpdateFlexible(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.FLEXIBLE,
            activity,
            myUpdateRequestCode
        )
    }

    private fun showUpdateDownloadedConfirmation() {
        // Show a confirmation dialog to the user
        // You can customize this part according to your app's UI/UX
        // Prompt the user to restart the app for the update to take effect
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == myUpdateRequestCode) {
            // Check if the update was successful
            if (resultCode != Activity.RESULT_OK) {
                // If the update failed or was canceled by the user, retry it
                updateCheckCallback.invoke()
            }
        }
    }

    fun unregisterListener() {
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }
}
