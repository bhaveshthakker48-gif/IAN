package org.bombayneurosciences.bna_2023.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.*
import org.bombayneurosciences.bna_2023.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadFileActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CreateFolderActivity"
        private const val REQUEST_CODE_PERMISSIONS = 101
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false
            val readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false

            if (writePermissionGranted && readPermissionGranted) {
                downloadFile()
            } else {
                Log.e(TAG, "Permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_folder)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = true
        window.statusBarColor = Color.WHITE

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply padding to the activity content (this handles all root layouts properly)
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            requestStoragePermissions()
        } else {
            downloadFile() // Directly download file for API 30+
        }
    }

    private fun requestStoragePermissions() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                downloadFile()
            }
            else -> {
                requestPermissionLauncher.launch(arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ))
            }
        }
    }

  /*  private fun downloadFile() {
        val url = "https://telemedocket.com/BNA/public/uploads/Articles_file/article_20240418081148.pdf"
        val fileName = "article_20240418081148.pdf"
        val folderName = "MyAppFolder"
        val appFolder: File = File(getExternalFilesDir(null), folderName)

        if (!appFolder.exists()) {
            val folderCreated = appFolder.mkdirs()
            if (!folderCreated) {
                Log.e(TAG, "Failed to create folder: $appFolder")
                return
            } else {
                Log.d(TAG, "Folder created: $appFolder")
            }
        } else {
            Log.d(TAG, "Folder already exists: $appFolder")
        }

        // Start a coroutine in IO dispatcher
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val outputFile = File(appFolder, fileName)
                val urlConnection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
                urlConnection.connect()

                if (urlConnection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw IOException("Server returned HTTP ${urlConnection.responseCode} ${urlConnection.responseMessage}")
                }

                val inputStream: InputStream = urlConnection.inputStream
                val outputStream = FileOutputStream(outputFile)
                val buffer = ByteArray(1024)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                outputStream.close()
                inputStream.close()

                withContext(Dispatchers.Main) {
                    Log.d(TAG, "File downloaded and saved successfully: $outputFile")
                    // Update UI or notify user of success
                }

            } catch (e: IOException) {
                Log.e(TAG, "Error downloading file: ${e.message}", e)
            }
        }
    }*/

    private fun downloadFile() {
        val url = "https://telemedocket.com/BNA/public/uploads/Articles_file/article_20240418081148.pdf"
        val fileName = "article_20240418081148.pdf"

        // Get the public downloads directory
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        // Ensure the directory exists
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        val outputFile = File(storageDir, fileName)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val urlConnection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
                urlConnection.connect()

                if (urlConnection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw IOException("Server returned HTTP ${urlConnection.responseCode} ${urlConnection.responseMessage}")
                }

                val inputStream: InputStream = urlConnection.inputStream
                val outputStream = FileOutputStream(outputFile)
                val buffer = ByteArray(1024)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                outputStream.close()
                inputStream.close()

                withContext(Dispatchers.Main) {
                    Log.d(TAG, "File downloaded and saved successfully: $outputFile")
                    // Update UI or notify user of success
                }

            } catch (e: IOException) {
                Log.e(TAG, "Error downloading file: ${e.message}", e)
            }
        }
    }

}
