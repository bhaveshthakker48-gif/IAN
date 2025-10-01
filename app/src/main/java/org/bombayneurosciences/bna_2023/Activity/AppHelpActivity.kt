package org.bombayneurosciences.bna_2023.Activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.VideoView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import org.bombayneurosciences.bna_2023.R


class AppHelpActivity : AppCompatActivity() {

    private lateinit var btnCancel: ImageButton

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_help)

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



        val videoView = findViewById<VideoView>(R.id.videoView)

        // Set the path for the raw video file
       // val videoPath = "android.resource://" + packageName + "/"+R.raw.app

        val uri :Uri=Uri.parse("android.resource://" + packageName + "/raw/"+"app_help_video")

        // Create a MediaController to enable play, pause, etc.
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

// Initialize cancel button
        btnCancel = findViewById(R.id.btnCancel)
        btnCancel.setOnClickListener {
            // Handle cancel button click (stop the video and finish the activity)
            videoView.stopPlayback()
            finish()
        }


        // Set the video URI and start playing
        videoView.setVideoURI(uri)
        videoView.start()
    }

}
