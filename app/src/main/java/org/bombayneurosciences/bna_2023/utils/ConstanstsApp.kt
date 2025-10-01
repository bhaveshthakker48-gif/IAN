package org.bombayneurosciences.bna_2023.utils

import android.os.Build
import android.util.Log
import android.content.Context
import android.telephony.TelephonyManager
import android.content.Context.TELEPHONY_SERVICE
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import org.bombayneurosciences.bna.Model.Journal.JournalEntry
import org.bombayneurosciences.bna_2023.R
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class ConstanstsApp {

    companion object {
        const val tag = "mytag"
        const val IMAGE_URL = "https://www.telemedocket.com/BNA/public/uploads/notifi_uploads/"
        const val journal = "https://www.telemedocket.com/BNA/public/uploads/Articles_file/"
        const val BASE_URL="https://telemedocket.com/BNA/public/"


        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
        fun checkInternetConenction(mContext: Context): Boolean {
            val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = cm.activeNetworkInfo
            return info != null && info.isConnected
        }



        fun sortJournalEntriesByMonth(entries: ArrayList<JournalEntry>): ArrayList<JournalEntry> {
          /*  val customMonthOrder = arrayOf(
                "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            )*/
             val customMonthOrder = arrayOf("January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )

            return entries.sortedWith(compareBy { customMonthOrder.indexOf(it.month) }).toCollection(ArrayList())
        }

        fun getIMEI(context: Context): String {
            val telephonyManager = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telephonyManager.imei ?: ""
            } else {
                // Handle versions below O as needed
                ""
            }
        }

        fun convertHtmlToPlainText(html: String): String {
            val document: Document = Jsoup.parse(html)
            val elements: Elements = document.select("body")
            return elements.text()
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun calculateDaysBetween(startDate: String?, endDate: String?): Long {
            // Check for null or empty strings
            if (startDate.isNullOrEmpty() || endDate.isNullOrEmpty() || startDate.equals("null", ignoreCase = true) || endDate.equals("null", ignoreCase = true)) {
                Log.e(tag, "Invalid date strings: startDate=$startDate, endDate=$endDate")
                return -1 // or handle the error as appropriate for your use case
            }

            // Try to parse the date strings
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return try {
                val startLocalDate = LocalDate.parse(startDate, formatter)
                val endLocalDate = LocalDate.parse(endDate, formatter)
                java.time.temporal.ChronoUnit.DAYS.between(startLocalDate, endLocalDate)
            } catch (e: Exception) {
                Log.e(tag, "Error parsing date strings: startDate=$startDate, endDate=$endDate", e)
                -1 // or handle the error as appropriate for your use case
            }
        }


        private fun getCurrentDate(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return dateFormat.format(Date())
        }

        fun isEventPast(endDate: String): Boolean {
            val currentDate = getCurrentDate() // Implement getCurrentDate() function
            // Compare the end date with the current date
            return endDate < currentDate
        }

        fun showCustomToast(context: Context, message: String) {
            val inflater = LayoutInflater.from(context)
            val layout: View = inflater.inflate(R.layout.custom_toast, null)

            val text: TextView = layout.findViewById(R.id.toast_text)
            text.text = message

            val toast = Toast(context)
            toast.duration = Toast.LENGTH_LONG
            toast.view = layout
            toast.setGravity(Gravity.BOTTOM or Gravity.BOTTOM, 0, 100)  // Adjust the yOffset as needed
            toast.show()

            val closeBtn: ImageView = layout.findViewById(R.id.toast_close)
            closeBtn.setOnClickListener {
                toast.cancel()
            }
        }
        fun getFileNameFromUrl(url: String): String {
            val lastSlashIndex = url.lastIndexOf('/')
            return if (lastSlashIndex != -1 && lastSlashIndex < url.length - 1) {
                url.substring(lastSlashIndex + 1)
            } else {
                ""
            }
        }
        fun getFilePathPDF(fileName: String): String {
            // Construct the full file path based on the known directory structure
            val filePath = "/storage/emulated/0/Android/data/org.bombayneurosciences.bna_2023/files/BNA_App_PDF/$fileName"
            return if (File(filePath).exists()) {
                filePath
            } else {
                throw FileNotFoundException("File not found at path: $filePath")
            }
        }





    }
}
