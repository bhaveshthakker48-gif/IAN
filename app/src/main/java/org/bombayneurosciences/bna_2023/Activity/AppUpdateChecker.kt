package org.bombayneurosciences.bna_2023.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import org.jsoup.Jsoup
import java.io.IOException
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp

class AppUpdateChecker(private val context: Context) {

    companion object {
        private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=org.bombayneurosciences.bna_2023"
    }

    fun checkForUpdate() {
        CheckUpdateTask().execute()
    }

    private inner class CheckUpdateTask : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg voids: Void): Boolean {
            try {
                // Get the version code of the app available on the Play Store
                val playStoreVersionCode = getPlayStoreAppVersionCode()

                // Get the package info for the installed app
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

                // Get the version code of your app
                val installedVersionCode = packageInfo.versionCode

                // Print version codes to log using ConstantsApp.TAG
                Log.d(ConstanstsApp.tag, "Play Store Version Code:" + playStoreVersionCode)
                Log.d(ConstanstsApp.tag, "Installed Version Code: "+installedVersionCode)

                // Compare the version codes
                return playStoreVersionCode > 0 && isUpdateAvailable(playStoreVersionCode)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        override fun onPostExecute(updateAvailable: Boolean) {
            super.onPostExecute(updateAvailable)

            if (updateAvailable) {
                showUpdateDialog()
            } else {
                Toast.makeText(context, "App is up to date.", Toast.LENGTH_SHORT).show()
            }
        }

        private fun showUpdateDialog() {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Update Available")
            alertDialogBuilder.setMessage("A new version of the app is available. Do you want to update?")

            // Set positive button with default color
            alertDialogBuilder.setPositiveButton("Update") { _, _ ->
                openPlayStore()
            }

            // Set negative button with grey color
            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                // User clicked Cancel
                dialog.dismiss()
            }

            // Create and show the dialog
            val alertDialog = alertDialogBuilder.create()

            alertDialog.setOnShowListener {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    ?.setTextColor(ContextCompat.getColor(context, R.color.gray))
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    ?.setTextColor(ContextCompat.getColor(context, R.color.gray))
            }

            alertDialog.show()
        }

        private fun openPlayStore() {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URL))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun getPlayStoreAppVersionCode(): Int {
            try {
                val doc = Jsoup.connect(PLAY_STORE_URL).get()
                val versionString = doc?.select("span[class=htlgb]")?.get(3)?.text()
                return versionString?.toIntOrNull() ?: -1
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return -1
        }

        private fun isUpdateAvailable(playStoreVersionCode: Int): Boolean {
            try {
                // Get the package info for the installed app
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

                // Get the version code of the installed app
                val installedVersionCode = packageInfo.versionCode

                // Compare the version codes
                return installedVersionCode < playStoreVersionCode
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }


    }
}
