package org.bombayneurosciences.bna_2023.Activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import org.bombayneurosciences.bna_2023.Data.SharedPreferencesManager
import org.bombayneurosciences.bna_2023.Model.Notification.Data
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.Roomdb.AppDatabase
import org.bombayneurosciences.bna_2023.Roomdb.NotificationEntity
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManagerSingleton
import java.io.File

class NotificationActivity2 : AppCompatActivity() {

    private lateinit var contentContainer: RelativeLayout
    private lateinit var appDatabase: AppDatabase
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification2)


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


        val backButton = findViewById<ImageView>(R.id.backbutton1)
        contentContainer = findViewById<RelativeLayout>(R.id.container)
        appDatabase = AppDatabase.getDatabase(this) // Initialize your Room database
        // Initialize SharedPreferencesManager
        sharedPreferencesManager = SharedPreferencesManager(this)
        if (ConstanstsApp.isInternetAvailable(this)) {
            showCustomProgressDialog()
            // Your other logic here
        }

        val bundle = intent.extras
        val notificationTitle = intent.getStringExtra("notificationTitle")
        val notificationDescription = intent.getStringExtra("notificationDescription")

        val notificationData = bundle?.getParcelable<Data>("notificationdata")
        Log.d(ConstanstsApp.tag, "pdf => $notificationData")

        if (notificationData != null) {
            val attachment = notificationData.attachment
            val fileExtension = getFileExtension(attachment)
            Log.d(ConstanstsApp.tag, "fileExtension => $fileExtension")

            backButton.setOnClickListener {
                val intent = Intent(applicationContext, NotificationActivity::class.java)
                startActivity(intent)
                finish()
            }

            when (fileExtension) {
                "jpg", "png" ,"jpeg"-> {
                    layoutInflater.inflate(R.layout.jpg_png_layout, contentContainer)

                    if (!isNetworkAvailable()) {
                        // Show an alert dialog when the user is offline
                        showAlert("Please connect to the internet",)
                    }


                    val imageView = findViewById<ImageView>(R.id.imageView)

                    val pdfWebView = findViewById<WebView>(R.id.pdfwebview_pdf)

                    imageView.visibility = View.VISIBLE

                    // Load the image into the ImageView using Glide
                    Glide.with(this)
                        .load(ConstanstsApp.IMAGE_URL + attachment)
                        .into(imageView)

                }

                "pdf" -> {
                    layoutInflater.inflate(R.layout.pdf_content_layout, contentContainer)

                    if (!isNetworkAvailable()) {
                        // Show an alert dialog when the user is offline
                        showAlert("Please connect to the internet",)
                    }
                }
                "mp4" -> {
                    layoutInflater.inflate(R.layout.pdf_content_layout, contentContainer)

                    if (!isNetworkAvailable()) {
                        // Show an alert dialog when the user is offline
                        showAlert("Please connect to the internet",)
                    }
                }
                "gif" -> {
                    layoutInflater.inflate(R.layout.pdf_content_layout, contentContainer)

                    if (!isNetworkAvailable()) {
                        // Show an alert dialog when the user is offline
                        showAlert("Please connect to the internet",)
                    }
                }
            }

            if (isNetworkAvailable() && fileExtension == "pdf") {
                // Online mode
                val pdfWebView = findViewById<WebView>(R.id.pdfwebview_pdf)

                pdfWebView.visibility = View.VISIBLE

                pdfWebView.requestFocus()
                pdfWebView.settings.javaScriptEnabled = true
                pdfWebView.settings.setSupportZoom(true)
                pdfWebView.settings.builtInZoomControls = true
                pdfWebView.settings.displayZoomControls = true
                pdfWebView.settings.pluginState = WebSettings.PluginState.ON
                pdfWebView.settings.allowFileAccess = true

                val baseUrl = "https://docs.google.com/gview?embedded=true&url"

                pdfWebView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        url: String?
                    ): Boolean {
                        if (url?.endsWith(".pdf") == true) {
                            downloadPDF(url, "sample.pdf")
                            return true
                        }
                        return false
                    }
                }

                val url = ConstanstsApp.IMAGE_URL + attachment
                val googleDocsUrl = baseUrl + "=" + url

                pdfWebView.loadUrl(googleDocsUrl)

            }
            if (isNetworkAvailable() && fileExtension == "mp4") {
                // Online mode
                val pdfWebView = findViewById<WebView>(R.id.pdfwebview_pdf)

                pdfWebView.visibility = View.VISIBLE

                pdfWebView.requestFocus()
                pdfWebView.settings.javaScriptEnabled = true
                pdfWebView.settings.setSupportZoom(true)
                pdfWebView.settings.builtInZoomControls = true
                pdfWebView.settings.displayZoomControls = true
                pdfWebView.settings.pluginState = WebSettings.PluginState.ON
                pdfWebView.settings.allowFileAccess = true

                val baseUrl = "https://docs.google.com/gview?embedded=true&url"

                pdfWebView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        url: String?
                    ): Boolean {
                        if (url?.endsWith(".pdf") == true) {
                            downloadPDF(url, "sample.pdf")
                            return true
                        }
                        return false
                    }
                }

                val url = ConstanstsApp.IMAGE_URL + attachment
//                val googleDocsUrl = baseUrl + "=" + url

                pdfWebView.loadUrl(url)

            }
            if (isNetworkAvailable() && fileExtension == "gif") {
                // Online mode
                val pdfWebView = findViewById<WebView>(R.id.pdfwebview_pdf)

                pdfWebView.visibility = View.VISIBLE

                pdfWebView.requestFocus()
                pdfWebView.settings.javaScriptEnabled = true
                pdfWebView.settings.setSupportZoom(true)
                pdfWebView.settings.builtInZoomControls = true
                pdfWebView.settings.displayZoomControls = true
                pdfWebView.settings.pluginState = WebSettings.PluginState.ON
                pdfWebView.settings.allowFileAccess = true

                val baseUrl = "https://docs.google.com/gview?embedded=true&url"

                pdfWebView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        url: String?
                    ): Boolean {
                        if (url?.endsWith(".pdf") == true) {
                            downloadPDF(url, "sample.pdf")
                            return true
                        }
                        return false
                    }
                }

                val url = ConstanstsApp.IMAGE_URL + attachment
//                val googleDocsUrl = baseUrl + "=" + url

                pdfWebView.loadUrl(url)

            }
        }
    }

    private fun showCustomProgressDialog() {
        val progressDialog = Dialog(this)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set layout parameters to center the dialog
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(progressDialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER

        progressDialog.window?.attributes = layoutParams

        progressDialog.setContentView(R.layout.dialog_progress)
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Example: Dismiss the dialog after a delay (simulating a task completion)
        val imageViewLoading: ImageView = progressDialog.findViewById(R.id.imageViewLoading)

        Glide.with(this)
            .asGif()
            .load(R.raw.loderbna)
            .into(imageViewLoading)

        imageViewLoading.postDelayed({
            progressDialog.dismiss()
        }, 2000)
    }


    private fun showAlert(message: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.intenet_alert)

        // Find views in the custom dialog layout
        val notificationTitle: TextView = dialog.findViewById(R.id.notificationTitle)
        val textViewLogoutConfirmation: TextView = dialog.findViewById(R.id.textViewLogoutConfirmation)
        val buttonYes: Button = dialog.findViewById(R.id.buttonYes)
        val buttonNo: Button = dialog.findViewById(R.id.buttonNo)

        // Set notification title and confirmation text
        notificationTitle.text = "Connectivity Issue"
        textViewLogoutConfirmation.text = message

        // Set click listener for the Yes button
        buttonYes.setOnClickListener {
            // Handle the Yes button click event
            dialog.dismiss()
        }

        // Set click listener for the No button
        buttonNo.setOnClickListener {
            // Handle the No button click event
            dialog.dismiss()
        }

        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
    }

    private fun getLocalFilePathForPDF(fileName: String): Any {
        val directory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val localFilePath = File(directory, fileName).absolutePath
        Log.d(ConstanstsApp.tag, "Local PDF File Path: $localFilePath")
        return localFilePath
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun downloadPDF(pdfUrl: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(pdfUrl))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle(fileName)
        request.setDescription("Downloading PDF")
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS, fileName
        )

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    private fun getFileExtension(filename: String): String {
        return filename.substring(filename.lastIndexOf(".") + 1)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        //   sharedPreferencesManager1.setBackState(true)
        val sessionManager = SessionManagerSingleton.getSessionManager(this)

    //    Log.d(ConstanstsApp.tag,"getUserStatus=>"+sharedPreferencesManager1.getUserStatus())
        Log.d(ConstanstsApp.tag,"getUserStatus1=>"+sessionManager.getUserStatus())

        val intent = Intent(this, NotificationActivity::class.java)
        startActivity(intent)
        finish()
    }
}
