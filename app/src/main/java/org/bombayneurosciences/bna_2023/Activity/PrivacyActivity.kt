package org.bombayneurosciences.bna_2023.Activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import org.bombayneurosciences.bna_2023.utils.sharepreferenceAppkill

class PrivacyActivity : AppCompatActivity() {
    private lateinit var sharedPreferencesManager1: SessionManager1
    private lateinit var SharepreferenceAppkill : sharepreferenceAppkill
    var isLogin=false
    var isKeepLogged=false
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)


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


        sharedPreferencesManager1 = SessionManager1(this)
        SharepreferenceAppkill= sharepreferenceAppkill(this)

        // Retrieve values from the intent
        isLogin = intent.getBooleanExtra("Login", false)
        isKeepLogged = intent.getBooleanExtra("Keep_logged", false)

        // Now you can use these values in your CommetteeActivity
        Log.d(ConstanstsApp.tag, "Is Login: $isLogin, Keep Logged: $isKeepLogged")
        val backButton = findViewById<ImageView>(R.id.backbutton)

           val webView: WebView = findViewById(R.id.webview)

        webView.webViewClient = WebViewClient()

        val webSettings: WebSettings = webView.settings
      webSettings.javaScriptEnabled = true
        val eventHeaderTextView: TextView = findViewById(R.id.eventheader)
        eventHeaderTextView.alpha = 0f

        // Call your animation function
        applyFadeInAnimation(eventHeaderTextView, 0)


        // Load the privacy policy URL
        if (isOnline()) {
            // Internet connection is available, load the privacy policy URL
            webView.loadUrl("https://telemedocket.com/BNA/public/privacy-policy")
        } else {
            // No internet connection, show alert
            showNoInternetAlert("Please connect to the internet")
        }


        backButton.setOnClickListener {
            Log.d(ConstanstsApp.tag, "getUserStatus" + sharedPreferencesManager1.getUserStatus())
            val (isLogin, isKeep) = sharedPreferencesManager1.getUserStatus()
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            intent.putExtra("Login", isLogin)
            intent.putExtra("Keep_logged", isKeep)
            startActivity(intent)
        }
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
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
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


    override fun onBackPressed() {
        super.onBackPressed()
        Log.d(ConstanstsApp.tag, "getUserStatus" + sharedPreferencesManager1.getUserStatus())
        val (isLogin, isKeep) = sharedPreferencesManager1.getUserStatus()
        val intent = Intent(this, PrivacyPolicyActivity::class.java)
        intent.putExtra("Login", isLogin)
        intent.putExtra("Keep_logged", isKeep)
        startActivity(intent)
    }
}