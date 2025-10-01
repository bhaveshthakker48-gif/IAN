package org.bombayneurosciences.bna_2023.Activity

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp


class VideoViewerCaseOfMonth : AppCompatActivity() {

    private lateinit var videoView: PlayerView
    private lateinit var btnCancel: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_viewer_case_of_month)


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



        // Retrieve the video path from the intent
        val videoPath = intent.getStringExtra("imagepath")
        Log.d(ConstanstsApp.tag,"videoPath=>"+videoPath)

        // Initialize VideoView
        videoView = findViewById(R.id.VideoView)

// Initialize cancel button
        btnCancel = findViewById(R.id.btnCancel)
        btnCancel.setOnClickListener {
            // Handle cancel button click (stop the video and finish the activity)
            //videoView.stopPlayback()
            finish()
        }


        // Check if the video path is not null
        if (!videoPath.isNullOrBlank()) {
            // Set a MediaController for playback controls (optional)
           /* val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)*/

            val videoPath1="https://www.telemedocket.com/BNA/public/uploads/caseimages/video/$videoPath"
            Log.d(ConstanstsApp.tag,""+videoPath1)

            // Set the URI of the video to the VideoView
           /* val videoUri = Uri.parse(videoPath1)
            videoView.setVideoURI(videoUri)

            // Start playing the video
            videoView.start()*/


            val exoPlayer = SimpleExoPlayer.Builder(this).build()
            val mediaItem = MediaItem.fromUri(videoPath1)
            videoView.player = exoPlayer
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }
}
