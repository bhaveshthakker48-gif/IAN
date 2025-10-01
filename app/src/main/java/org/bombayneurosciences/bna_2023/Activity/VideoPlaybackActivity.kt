// VideoPlaybackActivity.kt
package org.bombayneurosciences.bna_2023.Activity

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import org.bombayneurosciences.bna_2023.R


class VideoPlaybackActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private var exoPlayer: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_playback)

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


        // Retrieve data from the Intent
        val videoUrl = intent.getStringExtra("videoUrl")
        val videoLabel = intent.getStringExtra("videoLabel")

        // Initialize ExoPlayer
        playerView = findViewById(R.id.VideoView)
        exoPlayer = SimpleExoPlayer.Builder(this).build()

        // Set up the PlayerView with ExoPlayer
        playerView.player = exoPlayer

        // Create a MediaItem for the video URL
        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        exoPlayer?.setMediaItem(mediaItem)

        // Prepare and start playback
        exoPlayer?.prepare()
        exoPlayer?.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release ExoPlayer resources when the activity is destroyed
        exoPlayer?.release()
    }


}
