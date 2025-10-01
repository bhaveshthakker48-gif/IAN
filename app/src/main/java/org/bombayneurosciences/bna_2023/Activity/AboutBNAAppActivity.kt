package org.bombayneurosciences.bna_2023.Activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import org.bombayneurosciences.bna_2023.utils.sharepreferenceAppkill

class AboutBNAAppActivity : AppCompatActivity() {
    private lateinit var sharedPreferencesManager1: SessionManager1
    private lateinit var SharepreferenceAppkill : sharepreferenceAppkill
    var isLogin=false
    var isKeepLogged=false
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_bnaapp)


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


        val backButton = findViewById<ImageView>(R.id.backbutton)
       // val textViewAboutBNAAppContent = findViewById<TextView>(R.id.AboutBNAApp)
        sharedPreferencesManager1 = SessionManager1(applicationContext)
        SharepreferenceAppkill= sharepreferenceAppkill(this)

        // Retrieve values from the intent
        isLogin = intent.getBooleanExtra("Login", false)
        isKeepLogged = intent.getBooleanExtra("Keep_logged", false)

        val eventHeaderTextView: TextView = findViewById(R.id.eventheader)
        eventHeaderTextView.alpha = 0f

        // Call your animation function
        applyFadeInAnimation(eventHeaderTextView, 0)


        // Now you can use these values in your CommetteeActivity
        Log.d(ConstanstsApp.tag, "Is Login: $isLogin, Keep Logged: $isKeepLogged")
        backButton.setOnClickListener {
            Log.d(ConstanstsApp.tag, "getUserStatus" + sharedPreferencesManager1.getUserStatus())
            val (isLogin, isKeep) = sharedPreferencesManager1.getUserStatus()
            val intent = Intent(this, PrivacyPolicyActivity::class.java)

            intent.putExtra("Login", isLogin)
            intent.putExtra("Keep_logged", isKeep)
            startActivity(intent)
        }

       /* // Set the text content programmatically
        val aboutBNAAppContent = getString(R.string.about_bna_app_content)
        val spannedText = Html.fromHtml(aboutBNAAppContent, Html.FROM_HTML_MODE_LEGACY)

        val textViewAboutBNAContent = findViewById<TextView>(R.id.AboutBNAContent)
        textViewAboutBNAContent.text = spannedText
        textViewAboutBNAContent.movementMethod = LinkMovementMethod.getInstance()*/

        // Make email address clickable
       /* val emailTextView = findViewById<TextView>(R.id.AboutBNAContent)
        emailTextView.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:your.email@example.com")
            startActivity(emailIntent)
        }*/


        val vertion1: TextView = findViewById<TextView>(R.id.vertion1)




        try {
            // ✅ App का version name और code get करना
            val pInfo: PackageInfo = getPackageManager().getPackageInfo(getPackageName(), 0)
            val versionName = pInfo.versionName // e.g. "1.2.3"
            val versionCode =
                pInfo.versionCode // e.g. 12 (deprecated in API 28+, use getLongVersionCode())

          /*  // ✅ Text bold सिर्फ version name पर लगाएँ
            val text =
                "You are currently using version <b>$versionName</b> of the BNA App."

            // ✅ TextView पर set करें
            vertion1.text = Html.fromHtml(text)*/


            // यहाँ सिर्फ़ version name को gray + bold किया
            val grayColor = ContextCompat.getColor(this, android.R.color.darker_gray)
            val text =
                "You are currently using version <b><font color='#${Integer.toHexString(grayColor).substring(2)}'>$versionName</font></b> of the BNA App."

            vertion1.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
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
