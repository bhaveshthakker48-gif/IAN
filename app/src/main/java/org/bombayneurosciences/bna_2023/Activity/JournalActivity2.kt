package org.bombayneurosciences.bna_2023.Activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import org.bombayneurosciences.bna.Model.Journal.JournalEntry
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp

class JournalActivity2 : AppCompatActivity() {

    var intent_ach:String?=null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal2)


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

//
        // Check if it's not coming from a back press event
        if (!intent.getBooleanExtra("isBackPressed", false) && ConstanstsApp.isInternetAvailable(this)) {
            showCustomProgressDialog()
        }


        val bundle = intent.extras
        val journalEntry = bundle?.getParcelable<JournalEntry>("journalEntry")
        val isLatestSelected = intent.getBooleanExtra("isLatestSelected", true)
        intent_ach = intent.getStringExtra("Intent")
        Log.d(ConstanstsApp.tag,"archive data=>"+intent)
        val articleFile = bundle!!.getString("articleFile")
        val title = bundle.getString("title")

        val webView: WebView = findViewById(R.id.web)
        val backButton = findViewById<ImageView>(R.id.backbutton)
        if (articleFile != null) {
            // "articleFile" is not null, proceed with using it
            Log.d(ConstanstsApp.tag, "Received articleFile: $articleFile")


            if (title != null){
                Log.d(ConstanstsApp.tag, "Received title=> $title")

            }

            val eventHeaderTextView: TextView = findViewById(R.id.eventheader)
            title?.let {
                eventHeaderTextView.text = it
            }
            applyFadeInAnimation(eventHeaderTextView, 0)

            // You can use the "articleFile" as needed, for example, to load a PDF in a WebView
            val webView: WebView = findViewById(R.id.web)
            // Load the privacy policy URL
            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=$articleFile")
        } else {
            // "articleFile" is null, handle the case where it is not received
            Log.d(ConstanstsApp.tag, "No articleFile received")
            // You may want to show a message or take appropriate action
        }

        if (isOnline()) {
            webView.webViewClient = WebViewClient()
            val webSettings: WebSettings = webView.settings
            webSettings.javaScriptEnabled = true
            val baseUrl = "https://docs.google.com/gview?embedded=true&url="

            // Load the privacy policy URL
            if (journalEntry != null) {
                Log.d(
                    ConstanstsApp.tag,
                    "PDF URL: + ${journalEntry.articleFile}" + baseUrl + "https://www.telemedocket.com/BNA/public/uploads/Articles_file/" + journalEntry.articleFile
                )
                Log.d(ConstanstsApp.tag, "url in journal" + baseUrl + journalEntry.articleFile)
                webView.loadUrl(baseUrl + journalEntry.articleFile)
            }

            backButton.setOnClickListener {
               /* val destinationIntent = if (isLatestSelected) {
                    Intent(applicationContext, JournalActivity::class.java)
                } else {
                    Intent(applicationContext, JournalActiivtyyy::class.java)
                }

                // Pass the original selection information back to the destination
                destinationIntent.putExtra("isLatestSelected", isLatestSelected)

                startActivity(destinationIntent)
                finish() */


            // Finish the current activity (JournalActivity2)

                if (intent_ach.equals("archive"))
                {
                    val destinationIntent = Intent(applicationContext, JournalActiivtyyy::class.java)
                    intent.putExtra("isBackPressed", true)

                    startActivity(destinationIntent)
                    finish()
                }
                else
                {
                    val destinationIntent = Intent(applicationContext, JournalActivity::class.java)
                    intent.putExtra("isBackPressed", true)

                    startActivity(destinationIntent)
                    finish()
                }
            }
        } else {
            // Device is offline, show AlertDialog
            showNoInternetAlert("Please connect to the internet")
        }
    }

    private fun applyFadeInAnimation(view: TextView, duration: Long) {
        // Set the initial alpha to 0 (fully transparent)
        view.alpha = 0f

        // Create an ObjectAnimator for the alpha property
        val fadeInAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f)

        // Set the duration for the animation
        fadeInAnimator.duration = duration

        // Start the animation
        fadeInAnimator.start()
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

    private fun showNoInternetAlert(message:String) {
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

    private fun isOnline(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
