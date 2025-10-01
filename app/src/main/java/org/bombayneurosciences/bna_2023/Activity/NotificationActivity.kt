package org.bombayneurosciences.bna_2023.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.Data.SharedPreferencesManager
import org.bombayneurosciences.bna_2023.Model.Notification.Data
import org.bombayneurosciences.bna_2023.Model.Notification.NotificationDataClass
import org.bombayneurosciences.bna_2023.NotificationAdapter
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.Roomdb.AppDatabase
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import org.bombayneurosciences.bna_2023.utils.SessionManagerSingleton
import pl.droidsonroids.gif.GifImageView
import java.util.Collections

class NotificationActivity : AppCompatActivity() {

    private lateinit var appDatabase: AppDatabase // Initialize your Room database

    private lateinit var recyclerView: RecyclerView
    private val notificationsList = ArrayList<Data>()
    private lateinit var sharedPreferencesManager1: SessionManager1
    private lateinit var gifImageView: GifImageView

    var NotificationExistsArrayList: ArrayList<Int>? = null
    var NotificationArrayList: ArrayList<Data>? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

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


        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        gifImageView = findViewById(R.id.gifImageView)

        NotificationExistsArrayList = ArrayList()
        NotificationArrayList = ArrayList()

        val backButton = findViewById<ImageView>(R.id.backbutton)
        val arrowCircleImageView = findViewById<ImageView>(R.id.arrowcircle)
        val card = findViewById<LinearLayout>(R.id.card)

        val sharedPreferencesManager = SharedPreferencesManager(applicationContext)
        sharedPreferencesManager1 = SessionManager1(this)

        val notification = sharedPreferencesManager.getNotificationData()
// Retrieve data from SharedPreferences in offline mode
        val notificationData = sharedPreferencesManager.getNotificationData()
        val offlineNotifications = sharedPreferencesManager.getOfflineNotifications()
        val latestNotification = sharedPreferencesManager.getLatestNotification()

        // Sort notifications by read/unread status
        val sortedNotifications = offlineNotifications.sortedByDescending { it.isRead }

        if (notificationData != null) {
            // Filter out the unread notifications (if needed)
            val unreadNotifications = notificationData.data.filter { !it.isRead }

// After loading notificationData
            Log.d(ConstanstsApp.tag, "notificationData => notificationData")

            // Add the latest notification to the list if it exists
            if (unreadNotifications.isNotEmpty()) {
                val latestNotification = unreadNotifications.first()
                sharedPreferencesManager.saveLatestNotification(latestNotification) // Save the latest notification
                notificationsList.add(latestNotification)
            }
            Log.d(ConstanstsApp.tag, "unreadNotifications => unreadNotifications")

            if (latestNotification != null) {
                notificationsList.add(latestNotification)
            }

            NotificationArrayList = notification?.data as? ArrayList<Data> ?: ArrayList()
            //      NotificationArrayList = ArrayList(unreadNotifications)

            Collections.reverse(NotificationArrayList)
            val adapter = NotificationAdapter(
                NotificationArrayList ?: ArrayList(offlineNotifications),
                object : NotificationAdapter.OnCardClickListener {
                    override fun onCardClick(notificationData: Data) {

                        NotificationExistsArrayList?.add(notificationData.id)
                        Log.d(ConstanstsApp.tag, "onCardClick: notificationData => $notificationData")
                        // Handle the click event, mark the notification as read, and update SharedPreferences
                        sharedPreferencesManager.markNotificationAsRead(notificationData.id)

                        // Inside your NotificationActivity, after fetching the latest notification
                        val latestNotification = notificationsList.firstOrNull()
                        if (latestNotification != null) {
                            sharedPreferencesManager.saveLatestNotification(latestNotification)
                        }

                        val bundle = Bundle()
                        bundle.putParcelable("notificationdata", notificationData)
                        val intent = Intent(applicationContext, NotificationActivity2::class.java)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                },
                sharedPreferencesManager  // Pass the sharedPreferencesManager here
            )

            recyclerView.adapter = adapter
            // Set an OnClickListener for the back button
            backButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                //    sharedPreferencesManager1.setBackState(true)
val intent = Intent(applicationContext, MainActivity::class.java)
               startActivity(intent)
                    finish()

                }
            })

        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
     //   sharedPreferencesManager1.setBackState(true)
        val sessionManager = SessionManagerSingleton.getSessionManager(this)

        Log.d(ConstanstsApp.tag,"getUserStatus=>"+sharedPreferencesManager1.getUserStatus())
        Log.d(ConstanstsApp.tag,"getUserStatus1=>"+sessionManager.getUserStatus())

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun <E> ArrayList<E>.add(element: NotificationDataClass) {

    }
}
