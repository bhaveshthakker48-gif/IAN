package org.bombayneurosciences.bna_2023.Activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.VideoView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp

class Voting_videoview : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var btnCancel: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting_videoview)


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



        val videoUrl = intent.getStringExtra("videoUrl")


        // Initialize VideoView
        videoView = findViewById(R.id.VideoView_voting)

// Initialize cancel button
        btnCancel = findViewById(R.id.btnCancel_voting)


        // Set up your VideoView as needed
        videoView.setOnCompletionListener { finish() }  // Close the activity when video finishes
        videoView.setOnErrorListener { _, _, _ -> finish(); true }

        // Set up click listener for the Cancel button
        btnCancel.setOnClickListener {
            finish() // Close the activity when the Cancel button is clicked
        }
        Log.d(ConstanstsApp.tag, "Video Path=> " +videoUrl)

        // Play the video
        playVideo(videoUrl)
    }

    private fun playVideo(videoUrl: String?) {
        if (!videoUrl.isNullOrEmpty()) {
            // Play the video
            videoView.setVideoURI(Uri.parse(videoUrl))
            videoView.start()
        } else {
            // Handle case where video URL is not provided
            finish()
        }
    }
    }
