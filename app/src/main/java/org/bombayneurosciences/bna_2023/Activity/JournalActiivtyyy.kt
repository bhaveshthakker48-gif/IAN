package org.bombayneurosciences.bna_2023.Activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna.Model.Journal.JournalEntry
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.adapter.JournalAdapterrrrr
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp

class JournalActiivtyyy : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JournalAdapterrrrr

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_actiivtyyy)


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

        // Retrieve the selectedJournalEntry outside of the click listener
        val selectedJournalEntry =
            intent.getParcelableExtra<JournalEntry>("selectedJournalEntry")
        val isLatestSelected = intent.getBooleanExtra("isLatestSelected", true)
        val articleFile = intent.getStringExtra("articleFile")
        Log.d(ConstanstsApp.tag, "Received articleFile: $articleFile")
        val title = intent.getStringExtra("title")
        val month = intent.getStringExtra("month")
        if (title != null){
            Log.d(ConstanstsApp.tag, "Received title=> $title")

        }

        if (month != null){
            Log.d(ConstanstsApp.tag, "Received month => $month")

        }

        val eventHeaderTextView: TextView = findViewById(R.id.eventheader)
        month?.let {
            eventHeaderTextView.text = it
        }
        applyFadeInAnimation(eventHeaderTextView, 0)

        // Retrieve the selectedJournalEntry and journalEntriesList
        val journalEntriesList = intent.getParcelableArrayListExtra<JournalEntry>("journalEntries")

        // Log the size of the journalEntriesList
        if (journalEntriesList != null) {
            Log.d(ConstanstsApp.tag, "JournalEntriesList size: ${journalEntriesList.size}")
            Log.d(ConstanstsApp.tag, "JournalEntriesList contents: $journalEntriesList")
        } else {
            Log.d(ConstanstsApp.tag, "JournalEntriesList is null")
        }

        // Ensure selectedJournalEntry is not null
        if (selectedJournalEntry != null) {
            // Set a click listener for the back button
            backButton.setOnClickListener {
                val intent = Intent(this, JournalActivity::class.java)
                intent.putExtra("isLatestSelected", false)
                startActivity(intent)
                finish()
            }

            // Initialize RecyclerView and its adapter
            recyclerView = findViewById(R.id.recyclerview)
            adapter = JournalAdapterrrrr(journalEntriesList!!) { clickedJournalEntry ->
                // Handle item click here, e.g., start a new activity
                val intent = Intent(this, JournalActivity2::class.java)
                intent.putExtra("selectedJournalEntry", clickedJournalEntry)
                intent.putExtra("Intent", "archive")
                intent.putExtra("articleFile", clickedJournalEntry.articleFile)
                intent.putExtra("title",clickedJournalEntry.title)

                startActivity(intent)
            }

            // Set layout manager and adapter for the RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        } else {
            // Handle the case where selectedJournalEntry is null, maybe show an error message or go back
            finish()
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
      //  sharedPreferencesManager1.setBackState(true)

        val intent = Intent(this, JournalActivity::class.java)
        intent.putExtra("isBackPressed", true)

        startActivity(intent)
        finish()
    }
}
