package org.bombayneurosciences.bna_2023.Activity


import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import org.bombayneurosciences.bna_2023.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


/*class CreateFolderActivity : AppCompatActivity() {

    private val REQUEST_PERMISSION_CODE = 100
    private val URL_TO_DOWNLOAD =
        "https://telemedocket.com/BNA/public/uploads/Articles_file/article_20240418081148.pdf"
    private val FILE_NAME = "article_20240418081148.pdf"
    private val FOLDER_NAME = "BNA_media"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request storage permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )
        } else {
            // Permission already granted, continue with download
            downloadFile()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, continue with download
                downloadFile()
            } else {
                // Permission denied
                Log.e("CreateFolderActivity", "Storage permission denied")
            }
        }
    }

    private fun downloadFile() {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(URL_TO_DOWNLOAD)

        val request = DownloadManager.Request(uri)
            .setTitle(FILE_NAME)
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalFilesDir(this, FOLDER_NAME, FILE_NAME)

        // Ensure the folder exists before enqueueing the download
        val folder = File(getExternalFilesDir(null), FOLDER_NAME)
        if (!folder.exists()) {
            folder.mkdirs()
        }

        // Get content URI for the file using FileProvider
        val contentUri = FileProvider.getUriForFile(
            this,
            "org.bombayneurosciences.bna_2023.fileprovider",
            File(folder, FILE_NAME)
        )

        // Grant temporary permission to access the content URI
        request.addRequestHeader("Authorization", "Bearer " + 3)
        request.setDestinationUri(contentUri)

        // Enqueue the download
        downloadManager.enqueue(request)
    }
}*/

/*
class CreateFolderActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "mytag"
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false
            val readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false

            if (writePermissionGranted || readPermissionGranted) {
                createFolderAndSaveFile()
            } else {
                Log.e(TAG, "Permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_folder)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            requestStoragePermissions()
        } else {
            createFolderAndSaveFile() // Directly create folder and save file for API 30+
        }
    }

    private fun requestStoragePermissions() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                createFolderAndSaveFile()
            }
            else -> {
                requestPermissionLauncher.launch(arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ))
            }
        }
    }

    private fun createFolderAndSaveFile() {
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

        // Save the file
        val fileName = "example.txt"
        val fileContent = "Hello, this is a sample file content."

        val file = File(appFolder, fileName)
        try {
            FileOutputStream(file).use { fos ->
                fos.write(fileContent.toByteArray())
                fos.flush()
                Log.d(TAG, "File saved successfully: $file")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error saving file: ${e.message}", e)
        }
    }
}*/

class CreateFolderActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CreateFolderActivity"
        private const val REQUEST_CODE_PERMISSIONS = 101
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val writePermissionGranted =
                permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false
            val readPermissionGranted =
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false

            if (writePermissionGranted && readPermissionGranted) {
                downloadAndSaveFile()
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
            downloadAndSaveFile() // Directly download and save file for API 30+
        }
    }

    private fun requestStoragePermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                downloadAndSaveFile()
            }

            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }

    private fun downloadAndSaveFile() {
        val url =
            "https://telemedocket.com/BNA/public/uploads/Articles_file/article_20240418081148.pdf"
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

        val outputFile = File(appFolder, fileName)

        try {
            val urlConnection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
            urlConnection.connect()

            if (urlConnection.responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(
                    TAG,
                    "Server returned HTTP ${urlConnection.responseCode} ${urlConnection.responseMessage}"
                )
                return
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

            Log.d(TAG, "File downloaded and saved successfully: $outputFile")

        } catch (e: IOException) {
            Log.e(TAG, "Error downloading file: ${e.message}", e)
        }
    }
}



/*class CreateFolderActivity : AppCompatActivity() {

    private lateinit var folderNameEditText: EditText
    private lateinit var createFolderButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_folder)

        folderNameEditText = findViewById(R.id.folderName)
        createFolderButton = findViewById(R.id.createButton)

        createFolderButton.setOnClickListener {
            val folderName = folderNameEditText.text.toString().trim()
            if (checkPermissions()) {
                createDirectory(folderName)
            } else {
                requestPermissions()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val writeExternalStoragePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val readExternalStoragePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED &&
                readExternalStoragePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        requestMultiplePermissions.launch(permissions)
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true &&
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
                val folderName = folderNameEditText.text.toString().trim()
                createDirectory(folderName)
            } else {
                Toast.makeText(this@CreateFolderActivity, "Permissions Denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun createDirectory(folderName: String) {
        if (folderName.isBlank()) {
            Toast.makeText(this, "Please enter a valid folder name", Toast.LENGTH_SHORT).show()
            return
        }

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val folder = File(storageDir, folderName)

        if (folder.exists()) {
            Toast.makeText(this, "Folder already exists", Toast.LENGTH_SHORT).show()
        } else {
            if (folder.mkdir()) {
                Toast.makeText(this, "Folder created successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to create folder", Toast.LENGTH_SHORT).show()
            }
        }
    }
}*/
