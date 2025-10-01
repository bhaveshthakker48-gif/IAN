package org.bombayneurosciences.bna_2023.Activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import org.bombayneurosciences.bna_2023.ALodingDialog
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import org.bombayneurosciences.bna_2023.utils.sharepreferenceAppkill
import pl.droidsonroids.gif.GifImageView

class PrivacyPolicyActivity : AppCompatActivity() {
    private lateinit var aLodingDialog: ALodingDialog

    private lateinit var sharedPreferencesManager1: SessionManager1
    private lateinit var SharepreferenceAppkill : sharepreferenceAppkill
    var isLogin=false
    var isKeepLogged=false
    private lateinit var gifImageView: GifImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

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


        //    val webView: WebView = findViewById(R.id.webview)
        val backButton = findViewById<ImageView>(R.id.backbutton_privacypolicy)
        val cardViewShareApp = findViewById<TextView>(R.id.cardViewShareApp)
        val cardViewAppHelp = findViewById<TextView>(R.id.AppHelp)

        sharedPreferencesManager1 = SessionManager1(this)
        SharepreferenceAppkill= sharepreferenceAppkill(this)
        gifImageView = findViewById(R.id.gifImageView)

        // Retrieve values from the intent
        isLogin = intent.getBooleanExtra("Login", false)
        isKeepLogged = intent.getBooleanExtra("Keep_logged", false)


        // Now you can use these values in your CommetteeActivity
        Log.d(ConstanstsApp.tag, "Is Login: $isLogin, Keep Logged: $isKeepLogged")

        // Assuming you have defined card views in your layout
        val cardViewAboutBNA = findViewById<TextView>(R.id.cardViewAboutBNA)
        val cardViewAboutBNAApp = findViewById<TextView>(R.id.cardViewAboutBNAApp)
        val cardViewprivacy = findViewById<TextView>(R.id.cardViewprivacyApp)






        backButton.setOnClickListener {

            Log.d(ConstanstsApp.tag, "getUserStatus" + sharedPreferencesManager1.getUserStatus())
            val (isLogin, isKeep) = sharedPreferencesManager1.getUserStatus()
            val intent = Intent(this, MainActivity::class.java)

            intent.putExtra("Login", isLogin)
            intent.putExtra("Keep_logged", isKeep)
            startActivity(intent)
            finish()
//           val intent = Intent(applicationContext, MainActivity::class.java)
//           startActivity(intent)
//            finish()
//         //   sharedPreferencesManager1.setBackState(true)
//            sharedPreferencesManager1.getUserStatus()
//            sharedPreferencesManager1.setLogin(isLogin,isKeepLogged)
//
//            Log.d(ConstanstsApp.tag, "getLogin in commitee=>"+sharedPreferencesManager1.getUserStatus())

        }
        // Setting click listener for About BNA CardView
        cardViewAboutBNA.setOnClickListener {
            Log.d(ConstanstsApp.tag, "getUserStatus" + sharedPreferencesManager1.getUserStatus())
            val (isLogin, isKeep) = sharedPreferencesManager1.getUserStatus()
            val intent = Intent(this, AboutBNAActivity::class.java)

            intent.putExtra("Login", isLogin)
            intent.putExtra("Keep_logged", isKeep)
            startActivity(intent)
        }
        // Setting click listener for About BNA CardView
        cardViewAppHelp.setOnClickListener {
            val intent = Intent(this, AppHelpActivity::class.java)
            startActivity(intent)

        }

        // Setting click listener for About BNA App CardView
        cardViewAboutBNAApp.setOnClickListener {
            Log.d(ConstanstsApp.tag, "getUserStatus" + sharedPreferencesManager1.getUserStatus())
            val (isLogin, isKeep) = sharedPreferencesManager1.getUserStatus()
            val intent = Intent(this, AboutBNAAppActivity::class.java)

            intent.putExtra("Login", isLogin)
            intent.putExtra("Keep_logged", isKeep)
            startActivity(intent)
        }
        // Setting click listener for About BNA App CardView
        cardViewprivacy.setOnClickListener {
            Log.d(ConstanstsApp.tag, "getUserStatus" + sharedPreferencesManager1.getUserStatus())
            val (isLogin, isKeep) = sharedPreferencesManager1.getUserStatus()
            val intent = Intent(this, PrivacyActivity::class.java)

            intent.putExtra("Login", isLogin)
            intent.putExtra("Keep_logged", isKeep)
            startActivity(intent)
        }


        cardViewShareApp.setOnClickListener {
            shareAppLink()


//        aLodingDialog = ALodingDialog(this)
//// Show the loading dialog immediately after the activity is created
//        aLodingDialog.show()
//
//        // Simulate some background task that takes 3 seconds
//        Handler().postDelayed({
//            // Dismiss the loading dialog after 3 seconds (simulating the completion of some task)
//            aLodingDialog.dismiss()
//        }, 1000)
//
//        webView.webViewClient = WebViewClient()
//
//        val webSettings: WebSettings = webView.settings
//        webSettings.javaScriptEnabled = true
//
//
//        // Load the privacy policy URL
//        webView.loadUrl("https://telemedocket.com/BNA/public/privacy-policy")

//            backButton.setOnClickListener {
//                val intent = Intent(applicationContext, MainActivity::class.java)
//                startActivity(intent)
//
//
//            }
//


        }
    }

    private fun shareAppLink() {
        val appLink = "Download the Bombay Neurosciences Association (BNA) Android App : https://play.google.com/store/apps/details?id=org.bombayneurosciences.bna_2023"+
                "\n"+"\n Download the Bombay Neurosciences Association (BNA) iOS App : https://apps.apple.com/in/app/bna-bombay-neurosciences-assn/id1458721670"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out the BNA Android App!")
        shareIntent.putExtra(Intent.EXTRA_TEXT, appLink)

        startActivity(Intent.createChooser(shareIntent, "Share BNA App"))
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        sharedPreferencesManager1.setBackState(true)
//
//        sharedPreferencesManager1.setLogin(isLogin,isKeepLogged)
//
//        sharedPreferencesManager1.getUserStatus()
//        Log.d(ConstanstsApp.tag, "getLogin in commitee=>"+sharedPreferencesManager1.getUserStatus())

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }

}

