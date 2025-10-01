package org.bombayneurosciences.bna_2023.utils

import android.os.AsyncTask
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadTask : AsyncTask<String, Void, String>() {
    override fun doInBackground(vararg params: String?): String? {
        val url = params[0]
        val localPath = params[1]

        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connect()
            val inputStream = connection.inputStream
            val outputStream = FileOutputStream(localPath)

            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.close()
            inputStream.close()
            return localPath
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    override fun onPostExecute(result: String?) {
        if (!result.isNullOrEmpty()) {
            // File downloaded successfully, you can now use the local file
        } else {
            // Handle the error
        }
    }
}
