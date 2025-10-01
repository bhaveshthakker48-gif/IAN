package org.bombayneurosciences.bna_2023.Activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bombayneurosciences.bna_2023.CallBack.InterfaceVoting
import org.bombayneurosciences.bna_2023.Model.Topics.Data
import org.bombayneurosciences.bna_2023.Model.Topics.EventTopic
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.adapter.TopicAdapter
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import org.bombayneurosciences.bna_2023.utils.SessionManagerSingleton
import org.bombayneurosciences.bna_2023.utils.sharepreferenceAppkill
import org.json.JSONObject
import java.net.URL

class TopicsActivity : AppCompatActivity(), InterfaceVoting {

    private lateinit var topicRecyclerView: RecyclerView
    private lateinit var topicAdapter: TopicAdapter
    var eventId:String?=null
    var name:String?=null
    var EXTRA_EVENT_TYPE:String?=null

    var start_date: String? = null
    var end_date: String? = null
    var sessionID:String?=null
    lateinit var sessionManager: SessionManager1
    private lateinit var SharepreferenceAppkill: sharepreferenceAppkill
    var isLoggedIn: Boolean = false

    lateinit var sharedPreferencesManager1: SessionManager1
    private lateinit var sharedPreferences: SharedPreferences


    var sharedpreferences: SharedPreferences? = null
    val Login_PREFERENCES = "Login_Prefs"
    var retrievedString: String?=null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics)


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



        sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        sharedPreferencesManager1 = SessionManager1(this)
        sessionManager = SessionManagerSingleton.getSessionManager(this)

        SharepreferenceAppkill = sharepreferenceAppkill(this)
        isLoggedIn = sharedPreferencesManager1.isLoggedIn()

       val  sharepreferenceAppkill = SharepreferenceAppkill.getAppKill()
         val session1 =sessionManager.getBackState()
         val session2 =sessionManager.setBottomMenuBar("Voting")

        sharedpreferences = getSharedPreferences(Login_PREFERENCES, Context.MODE_PRIVATE);

        val backButton = findViewById<ImageView>(R.id.backbutton)
        // Set a click listener for the back button
        val refreshButton = findViewById<ImageView>(R.id.refreshbutton)

        //val event_id = intent.getStringExtra("eventID")
        eventId = intent.getStringExtra("eventID") ?: "default_value"
        sessionID= intent.getStringExtra("sessionId")?:"default_value"
        name = intent.getStringExtra("name")
        EXTRA_EVENT_TYPE = intent.getStringExtra(EventActivity.EXTRA_EVENT_TYPE)
        Log.d(ConstanstsApp.tag,"sessionID in topicActivity=>"+sessionID)
        Log.d(ConstanstsApp.tag,"name in topicActivity=>"+name)
        Log.d(ConstanstsApp.tag,"name in topicActivity=>"+EXTRA_EVENT_TYPE)

        Log.d(ConstanstsApp.tag,"sessionID in Shareprefernce topicActivity=>"+sessionID)


        val eventHeaderTextView: TextView = findViewById(R.id.eventheader)
        name?.let {
            eventHeaderTextView.text = it
        }
        applyFadeInAnimation(eventHeaderTextView, 0)
        
        refreshButton.setOnClickListener {
            // Call the method to handle the refresh logic
            refreshPage()
        }
        /*  start_date=intent.getStringExtra("start_date")?:"default"
          end_date=intent.getStringExtra("end_date")?:"default"
          Log.d(ConstanstsApp.tag,"startdate in topicActivity=>"+start_date)
          Log.d(ConstanstsApp.tag,"enddate in topicActivity=>"+end_date)*/
        // Obtain an instance of SharedPreferences
        val sharedPreferences = getSharedPreferences("EVENTID", Context.MODE_PRIVATE)

// Retrieve the String value with the key
         retrievedString = sharedPreferences.getString("eventID", "Default Value")
        val sessionID = sharedPreferences.getString("session_id","Default Value")
        start_date = sharedPreferences.getString("start_date", "Default Value")
        end_date = sharedPreferences.getString("end_date", "Default Value")
        Log.d(ConstanstsApp.tag,"sessionId in topicActivity=>"+retrievedString)
        Log.d(ConstanstsApp.tag,"start_date start_date in topicActivity=>"+start_date)
        Log.d(ConstanstsApp.tag,"end_date end_date in topicActivity=>"+end_date)
        Log.d(ConstanstsApp.tag,"sessionID  in topicActivity sessionID=>"+sessionID)
        FetchTopicsTask().execute(retrievedString.toString(),sessionID)
        if (retrievedString.equals("")) {

        } else {
            // Handle invalid event ID
        }

        backButton.setOnClickListener {

            Log.d(ConstanstsApp.tag,"retrievedString=>"+retrievedString)
            Log.d(ConstanstsApp.tag,"retrievedString=>"+name)


            val intent = Intent(this, EventActivity2::class.java)
            intent.putExtra("eventId",retrievedString)
        //    Log.e(ConstanstsApp.tag, "eventID in back "+retrievedString)
            intent.putExtra("isBackPressed", true)
            intent.putExtra("start_date",start_date)
            intent.putExtra("end_date",end_date)

            intent.putExtra(EventActivity.EXTRA_EVENT_TYPE,EXTRA_EVENT_TYPE)

             intent.putExtra("name",name)
            startActivity(intent)
            finish()
        }
//        if (ConstanstsApp.isInternetAvailable(this)) {
//            showCustomProgressDialog()
//            // Your other logic here
//        }
        // Check if it's not coming from a back press event
        if (!intent.getBooleanExtra("isBackPressed", false) && ConstanstsApp.isInternetAvailable(this)) {
            showCustomProgressDialog()
        }
   // Use the retrievedString as needed
        if (eventId != null) {
            // handle the event_id
            Log.d(ConstanstsApp.tag,"sessionId  topicActivity Intent=>"+eventId)
            Log.d(ConstanstsApp.tag,"start_date in topicActivity Intent=>"+start_date)
            Log.d(ConstanstsApp.tag,"end_date in topicActivity Intent=>"+end_date)
        } else {
            Log.e(ConstanstsApp.tag, "eventID is null")
        }

        topicRecyclerView = findViewById(R.id.recyclerview_topics)

        val (isLoggedIn, shouldRememberMe) = sharedPreferencesManager1.getUserStatus()
        topicAdapter = TopicAdapter(this,isLoggedIn,shouldRememberMe,sharepreferenceAppkill,name )
        topicRecyclerView.adapter = topicAdapter
        topicRecyclerView.layoutManager = LinearLayoutManager(this)

        val itemDecoration = Dashline_event(this)
        topicRecyclerView.addItemDecoration(itemDecoration)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (API 33+): Use OnBackInvokedDispatcher
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                object : OnBackInvokedCallback {
                    override fun onBackInvoked() {
                        handleBackPress()
                    }
                }
            )
        } else {
            // Android 9-12: Use traditional onBackPressedDispatcher
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPress()
                }
            })
        }


    }

    private fun handleBackPress() {
        Log.e("modi", "sunil1")
        val intent = Intent(this, EventActivity2::class.java)
        intent.putExtra("eventId",retrievedString)
        //    Log.e(ConstanstsApp.tag, "eventID in back "+retrievedString)
        intent.putExtra("isBackPressed", true)
        intent.putExtra("start_date",start_date)
        intent.putExtra("end_date",end_date)

        intent.putExtra(EventActivity.EXTRA_EVENT_TYPE,EXTRA_EVENT_TYPE)

        intent.putExtra("name",name)
        startActivity(intent)
        finish()
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

    private fun refreshPage() {
       recreate()
    }

    private fun showCustomProgressDialog() {
        val progressDialog = Dialog(this)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set layout parameters to center the dialog
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(progressDialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER

        progressDialog.window?.attributes = layoutParams

        progressDialog.setContentView(R.layout.dialog_progress)
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Example: Dismiss the dialog after a delay (simulating a task completion)
        val imageViewLoading: ImageView = progressDialog.findViewById(R.id.imageViewLoading)

        Glide.with(this)
            .asGif()
            .load(R.raw.loderbna)
            .into(imageViewLoading)

        imageViewLoading.postDelayed({
            progressDialog.dismiss()
        }, 2000)
    }


    inner class FetchTopicsTask : AsyncTask<String, Void, EventTopic>() {
        override fun doInBackground(vararg params: String?): EventTopic {
            val eventId = params[0]

            val sessionId = params[1]

            Log.d(ConstanstsApp.tag,"topic sessionId =>"+sessionId)


            val url = "https://www.telemedocket.com/BNA/public/gettopics?eid=$eventId"
            Log.d(ConstanstsApp.tag,"topic url=>"+url)
            val jsonString = URL(url).readText()

            return parseJson(jsonString,sessionId)
        }

        override fun onPostExecute(result: EventTopic?) {
            if (result != null && result.success == 1 && !result.data.isNullOrEmpty()) {
                topicAdapter.setTopics(result.data)
                // Make the RecyclerView visible
                topicRecyclerView.visibility = View.VISIBLE

                // Hide the "No Topics Available" TextView
                findViewById<TextView>(R.id.no_topic_text_view).visibility = View.GONE
            } else {
                // Handle the case when there are no topics or an error occurs
                topicAdapter.setTopics(emptyList())
                // No topics available, hide the RecyclerView
                topicRecyclerView.visibility = View.GONE

                // Show the "No Topics Available" TextView
                findViewById<TextView>(R.id.no_topic_text_view).visibility = View.VISIBLE
            }
        }
    }


//    private fun parseJson(jsonString: String): EventTopic {
//        val topics = mutableListOf<Data>()
//        val jsonObject = JSONObject(jsonString)
//
//        val success = jsonObject.getInt("success")
//        val jsonArray = jsonObject.getJSONArray("data")
//
//        Log.d(ConstanstsApp.tag,"jsonArray=>"+jsonArray)
//
//        for (i in 0 until jsonArray.length()) {
//            val topic = jsonArray.getJSONObject(i)
//
//            val created_at = topic.getString("created_at")
//            val end_time = topic.getString("end_time")
//            val event_date = topic.getString("event_date")
//            val event_day = topic.getInt("event_day")
//            val event_id = topic.getInt("event_id")
//            val id = topic.getInt("id")
//            val is_deleted = topic.getInt("is_deleted")
//            val session_id = topic.getInt("session_id")
//            val slides = topic.getString("slides")
//            val speaker = topic.getString("speaker")
//            val start_time = topic.getString("start_time")
//            val title = topic.getString("title")
//            val type = topic.getString("type")
//            val updated_at = topic.getString("updated_at")
//            val moderators = topic.getString("moderators")
//            val is_active = topic.getString("is_active").toInt()
//
//            topics.add(
//                Data(
//                    created_at,
//                    end_time,
//                    event_date,
//                    event_day,
//                    event_id,
//                    id,
//                    is_active,
//                    is_deleted,
//                    moderators,
//                    session_id,
//                    slides, speaker,
//                    start_time,
//                    title,
//                    type,
//                    updated_at
//                )
//            )
//        }
//
//        return EventTopic(topics, success)
//    }
@SuppressLint("SuspiciousIndentation")
//private fun parseJson(jsonString: String, sessionId: String?): EventTopic {
//    val topics = mutableListOf<Data>()
//    val jsonObject = JSONObject(jsonString)
//
//    Log.d(ConstanstsApp.tag,"jsonObject=> "+jsonObject)
//
//    if (jsonObject.has("success") && jsonObject.has("data")) {
//        val success = jsonObject.getInt("success")
//        val jsonArray = jsonObject.getJSONArray("data")
//
//        for (i in 0 until jsonArray.length()) {
//            val topic = jsonArray.getJSONObject(i)
//
//            val created_at = topic.getString("created_at")
//         val end_time = topic.getString("end_time")
//            val event_date = topic.getString("event_date")
//            val event_day = topic.getInt("event_day")
//           val event_id = topic.getInt("event_id")
//            val id = topic.getInt("id")
//            val is_deleted = topic.getInt("is_deleted")
//            val session_id = topic.getInt("session_id")
//            val slides = topic.getString("slides")
//            val speaker = topic.getString("speaker")
//            val start_time = topic.getString("start_time")
//            val title = topic.getString("title")
//            val type = topic.getString("type")
//            val updated_at = topic.getString("updated_at")
//            val moderators = topic.getString("moderators")
//           val is_active = topic.getString("is_active").toInt()
//          val quecnt = topic.getString("quecnt")
//
//
//            // Create Data object and add to the list
//            topics.add(
//                Data(
//                    created_at,
//                    end_time,
//                   event_date,
//                    event_day,
//                    event_id,
//                    id,
//                    is_active,
//                    is_deleted,
//                    moderators,
//                    session_id,
//                    slides, speaker,
//                    start_time,
//                   title,
//                    type,
//                    updated_at,
//                    quecnt,
//                    start_date.toString(),
//                    end_date.toString()
//
//                )
//            )
//        }
//
//        return EventTopic(topics, success)
//    } else {
//        Log.e(ConstanstsApp.tag, "JSON response missing required fields.")
//        return EventTopic(emptyList(), 0)
//    }
//}

private fun parseJson(jsonString: String, sessionId: String?): EventTopic {
    val topics = mutableListOf<Data>()
    val jsonObject = JSONObject(jsonString)

    Log.d(ConstanstsApp.tag, "jsonObject=> $jsonObject")

    if (jsonObject.has("success") && jsonObject.has("data")) {
        val success = jsonObject.getInt("success")
        val jsonArray = jsonObject.getJSONArray("data")

        for (i in 0 until jsonArray.length()) {
            val topic = jsonArray.getJSONObject(i)

            val created_at = topic.getString("created_at")
            val end_time = topic.getString("end_time")
            val event_date = topic.getString("event_date")
            val event_day = topic.getInt("event_day")
            val event_id = topic.getInt("event_id")
            val id = topic.getInt("id")
            val is_deleted = topic.getInt("is_deleted")
            val session_id = topic.getInt("session_id")
            val slides = topic.getString("slides")
            val speaker = topic.getString("speaker")
            val start_time = topic.getString("start_time")
            val title = topic.getString("title")
            val type = topic.getString("type")
            val updated_at = topic.getString("updated_at")
            val moderators = topic.getString("moderators")
            val is_active = topic.getString("is_active").toInt()
            val quecnt = topic.getString("quecnt")

            // Filter topics based on sessionId
            if (session_id == sessionId?.toInt()) {
                // Create Data object and add to the list
                topics.add(
                    Data(
                        created_at,
                        end_time,
                        event_date,
                        event_day,
                        event_id,
                        id,
                        is_active,
                        is_deleted,
                        moderators,
                        session_id,
                        slides,
                        speaker,
                        start_time,
                        title,
                        type,
                        updated_at,
                        quecnt,
                        start_date.toString(),
                        end_date.toString(),

                    )
                )
            }
        }
        return EventTopic(topics, success)
    } else {
        Log.e(ConstanstsApp.tag, "JSON response missing required fields.")
        return EventTopic(emptyList(), 0)
    }
}

    override fun onVotingClick(topicId: Int, name: String) {

        Log.d(ConstanstsApp.tag,"name on  onVotingClick=> "+name)
//        val intent = Intent(this, VotingActivity::class.java)
//        intent.putExtra("topicId", topicId)
//       //  Pass necessary data as extras to the intent
//       // intent.putExtra("eventId", data.event_id)
//       // intent.putExtra("sessionId", data.session_id)
//
//        intent.putExtra("name", name)
//        startActivity(intent)
    }

//    override fun onVotingClick(
//        data: org.bombayneurosciences.bna_2023.Model.Topic_new.Data,
//        position: Int,
//        isLoggedIn: Boolean
//    ) {
//        if (isLoggedIn) {
//            // User is logged in, open VotingActivity
//            val intent = Intent(this, VotingActivity::class.java)
//            intent.putExtra("topicId", data.id)
//
//            startActivity(intent)
//        } else {
//            // User is not logged in, open LoginActivity
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//        }
//    }

}