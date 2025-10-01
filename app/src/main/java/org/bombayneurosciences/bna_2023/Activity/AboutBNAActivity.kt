package org.bombayneurosciences.bna_2023.Activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import org.bombayneurosciences.bna_2023.utils.sharepreferenceAppkill

class AboutBNAActivity : AppCompatActivity() {
    private lateinit var sharedPreferencesManager1: SessionManager1
    private lateinit var SharepreferenceAppkill : sharepreferenceAppkill
    var isLogin=false
    var isKeepLogged=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_bnaactivity)

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
        sharedPreferencesManager1 = SessionManager1(this)
        SharepreferenceAppkill= sharepreferenceAppkill(this)

        // Retrieve values from the intent
        isLogin = intent.getBooleanExtra("Login", false)
        isKeepLogged = intent.getBooleanExtra("Keep_logged", false)

        // Now you can use these values in your CommetteeActivity
        Log.d(ConstanstsApp.tag, "Is Login: $isLogin, Keep Logged: $isKeepLogged")

        val eventHeaderTextView: TextView = findViewById(R.id.eventheader)
        eventHeaderTextView.alpha = 0f

        // Call your animation function
        applyFadeInAnimation(eventHeaderTextView, 0)



        backButton.setOnClickListener {
            Log.d(ConstanstsApp.tag, "getUserStatus" + sharedPreferencesManager1.getUserStatus())
            val (isLogin, isKeep) = sharedPreferencesManager1.getUserStatus()
            val intent = Intent(this, PrivacyPolicyActivity::class.java)

            intent.putExtra("Login", isLogin)
            intent.putExtra("Keep_logged", isKeep)
            startActivity(intent)
        }

        // Assuming you have defined TextViews in your layout
        val textViewAboutBNAContent = findViewById<TextView>(R.id.textViewAboutBNAContent)

        // Set the text content programmatically
        val aboutBnaContent = getString(R.string.about_bna_content)
        val spannableString = SpannableString.valueOf(aboutBnaContent)

        // Set clickable spans for email and phone number
        setClickableSpan(spannableString, "bombayneurosciences@gmail.com", ClickableEmailSpan())
        setClickableSpan(spannableString, "9869718979", ClickablePhoneSpan())
        setClickableSpan(spannableString, "9867618591", ClickablePhoneSpan())

        // Set the modified text with clickable spans
        textViewAboutBNAContent.text = spannableString
        textViewAboutBNAContent.movementMethod = LinkMovementMethod.getInstance()
        // Set the text content programmatically

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


    private fun setClickableSpan(spannableString: SpannableString, text: String, clickableSpan: ClickableSpan) {
        val start = spannableString.indexOf(text)
        val end = start + text.length
        spannableString.setSpan(clickableSpan, start, end, 0)
    }

    private inner class ClickableEmailSpan : ClickableSpan() {
        override fun onClick(widget: View) {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:bombayneurosciences@gmail.com")
            startActivity(intent)
        }
    }

    private inner class ClickablePhoneSpan : ClickableSpan() {
        override fun onClick(widget: View) {
            val phoneNumber = "9869718979" // Replace with the actual phone number
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null))
            startActivity(intent)
        }
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
