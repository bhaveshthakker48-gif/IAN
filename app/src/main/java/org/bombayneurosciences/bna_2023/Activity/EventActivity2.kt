package org.bombayneurosciences.bna_2023.Activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bombayneurosciences.bna_2023.Activity.EventActivity.Companion
import org.bombayneurosciences.bna_2023.Activity.EventActivity.Companion.EXTRA_EVENT_TYPE
import org.bombayneurosciences.bna_2023.CallBack.SessionAdapterCallBack
import org.bombayneurosciences.bna_2023.Data.RetrofitInstance
import org.bombayneurosciences.bna_2023.Model.Topic_new.Topics1
import org.bombayneurosciences.bna_2023.Model.Topics.EventTopic
import org.bombayneurosciences.bna_2023.Model.sesssions.Data
import org.bombayneurosciences.bna_2023.Model.sesssions.EventSessions
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.adapter.SessionAdapter
import org.bombayneurosciences.bna_2023.adapter.TopicAdapter1
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp.Companion.calculateDaysBetween
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.net.URL


class EventActivity2 : AppCompatActivity(),SessionAdapterCallBack,
    RadioGroup.OnCheckedChangeListener {
    companion object {
        const val EXTRA_FROM_PAST_EVENT = "fromPastEvent"
    }
    private lateinit var sessionRecyclerView: RecyclerView
    private lateinit var sessionAdapter: SessionAdapter
    private lateinit var noSessionTextView: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton1: RadioButton
    private lateinit var radioButton2: RadioButton
    private lateinit var radioButton3: RadioButton
    private lateinit var radioButton4: RadioButton
    private lateinit var radioButton5: RadioButton
    private lateinit var radioButton6: RadioButton
    private lateinit var radioButton7: RadioButton
    private lateinit var topicAdapter: TopicAdapter1

    var eventId: String? = null
    var name: String? = null
    var EXTRA_EVENT_TYPE: String? = null

    var start_date: String? = null
    var end_date: String? = null
    var successTopic = 0
    private var selectedDay: Int=0  // Default to Day 1
    // Declare a mutable list of sessions
    var sessionsList: MutableList<Data> = mutableListOf()
    var topicsList: MutableList<org.bombayneurosciences.bna_2023.Model.Topic_new.Data> = mutableListOf()


    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event2)


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

//
        if (!intent.getBooleanExtra("isBackPressed", false) && ConstanstsApp.isInternetAvailable(this)) {
            showCustomProgressDialog()
        }
        sessionRecyclerView = findViewById(R.id.recyclerview_session)
        noSessionTextView = findViewById(R.id.no_data_text_view_activity_event2)

        val itemDecoration = Dashline_event(this)
        sessionRecyclerView.addItemDecoration(itemDecoration)
        radioGroup = findViewById(R.id.radio_group)
        radioButton1 = findViewById(R.id.day1)
        radioButton2 = findViewById(R.id.day2)
        radioButton3 = findViewById(R.id.day3)
        radioButton4 = findViewById(R.id.day4)
        radioButton5 = findViewById(R.id.day5)
        radioButton6 = findViewById(R.id.day6)
        radioButton7 = findViewById(R.id.day7)
        radioGroup.setOnCheckedChangeListener(this)

        topicAdapter = TopicAdapter1()
        sessionRecyclerView.layoutManager = LinearLayoutManager(this)
        val backButton = findViewById<ImageView>(R.id.backbutton)
        val refreshButton = findViewById<ImageView>(R.id.refreshbutton)

        radioButton1.setBackgroundResource(R.drawable.background_selected)
        radioButton1.setTextColor(Color.WHITE)


        refreshButton.setOnClickListener {
            Log.e("modi ",""+"sunil2")
            refreshPage()
        }
        eventId = intent.getStringExtra("eventId")
        Log.d(ConstanstsApp.tag, "evendid in eventactivity2=>" + eventId)

        val daysUntilEvent = intent.getIntExtra("days_until_event", 0)
         start_date = intent.getStringExtra("start_date")
         end_date = intent.getStringExtra("end_date")
        name = intent.getStringExtra("name")
        EXTRA_EVENT_TYPE = intent.getStringExtra(EventActivity.EXTRA_EVENT_TYPE)
        Log.d(ConstanstsApp.tag, "start_date" + start_date)
        Log.d(ConstanstsApp.tag, "end_date" + end_date)
        Log.d(ConstanstsApp.tag,"name=>"+name)
        Log.d(ConstanstsApp.tag,"EXTRA_EVENT_TYPE=>"+EXTRA_EVENT_TYPE)
        val daysBetween = calculateDaysBetween(start_date.toString(), end_date.toString())

        Log.d(ConstanstsApp.tag, "daysBetween=>" + (daysBetween.toInt() + 1))

        val eventHeaderTextView: TextView = findViewById(R.id.eventheader)
        name?.let {
            eventHeaderTextView.text = it
        }
        applyFadeInAnimation(eventHeaderTextView, 0)


        //FetchTopicsTask().execute(eventId)

        //FetchSessionsTask().execute(eventId.toString())

        FetchSessionsTask2().execute(eventId.toString())


        FetchTopicsTask2().execute(eventId.toString())








        val NoOfeventSessions = (daysBetween.toInt() + 1)

        when (NoOfeventSessions) {
            -1 -> {
            }

            1 -> {
                radioGroup.visibility = View.VISIBLE
                radioButton1.visibility = View.VISIBLE
//
            }
            2 -> {
                radioGroup.visibility = View.VISIBLE
                radioButton1.visibility = View.VISIBLE
                radioButton2.visibility = View.VISIBLE

            }
            3 -> {
                radioGroup.visibility = View.VISIBLE
                radioButton1.visibility = View.VISIBLE
                radioButton2.visibility = View.VISIBLE
                radioButton3.visibility = View.VISIBLE
            }
            4 -> {
                radioGroup.visibility = View.VISIBLE
                radioButton1.visibility = View.VISIBLE
                radioButton2.visibility = View.VISIBLE
                radioButton3.visibility = View.VISIBLE
                radioButton4.visibility = View.VISIBLE
            }
            5 -> {
                radioGroup.visibility = View.VISIBLE
                radioButton1.visibility = View.VISIBLE
                radioButton2.visibility = View.VISIBLE
                radioButton3.visibility = View.VISIBLE
                radioButton4.visibility = View.VISIBLE
                radioButton5.visibility = View.VISIBLE

            }
            6 -> {
                radioGroup.visibility = View.VISIBLE
                radioButton1.visibility = View.VISIBLE
                radioButton2.visibility = View.VISIBLE
                radioButton3.visibility = View.VISIBLE
                radioButton4.visibility = View.VISIBLE
                radioButton5.visibility = View.VISIBLE
                radioButton6.visibility = View.VISIBLE

            }
            7 -> {
                radioGroup.visibility = View.VISIBLE
                radioButton1.visibility = View.VISIBLE
                radioButton2.visibility = View.VISIBLE
                radioButton3.visibility = View.VISIBLE
                radioButton4.visibility = View.VISIBLE
                radioButton5.visibility = View.VISIBLE
                radioButton6.visibility = View.VISIBLE
                radioButton7.visibility = View.VISIBLE
            }
        }


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


        backButton.setOnClickListener {

            Log.e("modi ",""+"sunil")
            val intent = Intent(this, EventActivity::class.java)
            intent.putExtra("isBackPressed", true)
            intent.putExtra(EventActivity.EXTRA_EVENT_TYPE, EXTRA_EVENT_TYPE)
            startActivity(intent)
            finish()
        }



    }

    private fun applyFadeInAnimation(view: TextView, duration: Long) {
        view.alpha = 0f
        val fadeInAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f)
        fadeInAnimator.duration = duration
        fadeInAnimator.start()
    }

    private fun refreshPage() {
        recreate()

    }
    private fun showCustomProgressDialog() {
        val progressDialog = Dialog(this)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(progressDialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER
        progressDialog.window?.attributes = layoutParams
        progressDialog.setContentView(R.layout.dialog_progress)
        progressDialog.setCancelable(false)
        progressDialog.show()
        val imageViewLoading: ImageView = progressDialog.findViewById(R.id.imageViewLoading)
        Glide.with(this)
            .asGif()
            .load(R.raw.loderbna)
            .into(imageViewLoading)
        imageViewLoading.postDelayed({
            progressDialog.dismiss()
        }, 2000)
    }
    private fun GetTopicEventIDWise() {
        val apiServiceTopics = RetrofitInstance.apiServiceTopics
        // Replace with the actual event ID
        val call = apiServiceTopics.getTopics(eventId!!.toInt())
        call.enqueue(object : retrofit2.Callback<List<EventTopic>> {
            override fun onResponse(
                call: Call<List<EventTopic>>,
                response: retrofit2.Response<List<EventTopic>>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    Log.d(ConstanstsApp.tag, "topics: $data")
                } else {
                    Log.e(ConstanstsApp.tag, "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<EventTopic>>, t: Throwable) {
                // Handle failure
                Log.e(ConstanstsApp.tag, "onFailure: ${t.message}")
            }
        })
    }
    override fun onItemClick(position: Int, data: Data) {
        if (data != null) {
            val event_id = data.event_id
            Log.d(ConstanstsApp.tag, "onItemClick => $event_id")
            Log.d(ConstanstsApp.tag, "onItemClick => ${data.id}")
            val sharedPreferences = getSharedPreferences("EVENTID", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("eventID", event_id.toString())
            editor.putString("start_date", start_date.toString())
            editor.putString("end_date", end_date.toString())
            editor.putString("session_id", data.id.toString())
            // Commit the changes
            editor.apply()
            val intent = Intent(this, TopicsActivity::class.java)
            intent.putExtra("eventID", event_id)
            intent.putExtra("session_id", data.id)
            intent.putExtra("name",name)
            intent.putExtra(EventActivity.EXTRA_EVENT_TYPE, EXTRA_EVENT_TYPE)
            startActivity(intent)
            finish()
        } else {
            Log.e(ConstanstsApp.tag, "Data is null")
        }
}
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.day1 -> {
                selectedDay=1
                //FetchTopicsTask().execute(eventId)
                Log.d(ConstanstsApp.tag,  "day1"+eventId)

              //  Log.d(ConstanstsApp.tag,"sessionsList=>"+sessionsList)



                radioButton1.setBackgroundResource(R.drawable.background_selected)
                radioButton1.setTextColor(Color.WHITE)
                radioButton2.setTextColor(Color.BLACK)
                radioButton3.setTextColor(Color.BLACK)
                radioButton4.setTextColor(Color.BLACK)
                radioButton5.setTextColor(Color.BLACK)
                radioButton6.setTextColor(Color.BLACK)
                radioButton7.setTextColor(Color.BLACK)

                radioButton2.setBackgroundResource(R.drawable.background_unselected)
                radioButton3.setBackgroundResource(R.drawable.background_unselected)
                radioButton4.setBackgroundResource(R.drawable.background_unselected)
                radioButton5.setBackgroundResource(R.drawable.background_unselected)
                radioButton6.setBackgroundResource(R.drawable.background_unselected)
                radioButton7.setBackgroundResource(R.drawable.background_unselected)

                setDataSessionAdapter(selectedDay)
            }
            R.id.day2 -> {
                selectedDay=2
                Log.d(ConstanstsApp.tag,"selectedDay2=>"+selectedDay)
                //FetchTopicsTask().execute(eventId)



                radioButton2.setBackgroundResource(R.drawable.background_selected)
                radioButton2.setTextColor(Color.WHITE)
                radioButton1.setTextColor(Color.BLACK)
                radioButton3.setTextColor(Color.BLACK)
                radioButton4.setTextColor(Color.BLACK)
                radioButton5.setTextColor(Color.BLACK)
                radioButton6.setTextColor(Color.BLACK)
                radioButton7.setTextColor(Color.BLACK)

                radioButton1.setBackgroundResource(R.drawable.background_unselected)
                radioButton3.setBackgroundResource(R.drawable.background_unselected)
                radioButton4.setBackgroundResource(R.drawable.background_unselected)
                radioButton5.setBackgroundResource(R.drawable.background_unselected)
                radioButton6.setBackgroundResource(R.drawable.background_unselected)
                radioButton7.setBackgroundResource(R.drawable.background_unselected)

             //   sessionAdapter.notifyDataSetChanged()

                setDataSessionAdapter(selectedDay)
            }
            R.id.day3 -> {

                selectedDay=3


                Log.d(ConstanstsApp.tag,"selectedDay3=>"+selectedDay)



                setDataSessionAdapter(selectedDay)


                //FetchTopicsTask().execute(eventId)
                radioButton3.setBackgroundResource(R.drawable.background_selected)
                radioButton3.setTextColor(Color.WHITE)
                radioButton1.setTextColor(Color.BLACK)
                radioButton2.setTextColor(Color.BLACK)
                radioButton4.setTextColor(Color.BLACK)
                radioButton5.setTextColor(Color.BLACK)
                radioButton6.setTextColor(Color.BLACK)
                radioButton7.setTextColor(Color.BLACK)

                radioButton1.setBackgroundResource(R.drawable.background_unselected)
                radioButton2.setBackgroundResource(R.drawable.background_unselected)
                radioButton4.setBackgroundResource(R.drawable.background_unselected)
                radioButton5.setBackgroundResource(R.drawable.background_unselected)
                radioButton6.setBackgroundResource(R.drawable.background_unselected)
                radioButton7.setBackgroundResource(R.drawable.background_unselected)
            }
            R.id.day4 -> {
                selectedDay=4
               // FetchTopicsTask().execute(eventId)

                setDataSessionAdapter(selectedDay)
                radioButton4.setBackgroundResource(R.drawable.background_selected)
                radioButton4.setTextColor(Color.WHITE)
                radioButton1.setTextColor(Color.BLACK)
                radioButton3.setTextColor(Color.BLACK)
                radioButton2.setTextColor(Color.BLACK)
                radioButton5.setTextColor(Color.BLACK)
                radioButton6.setTextColor(Color.BLACK)
                radioButton7.setTextColor(Color.BLACK)

                radioButton1.setBackgroundResource(R.drawable.background_unselected)
                radioButton3.setBackgroundResource(R.drawable.background_unselected)
                radioButton2.setBackgroundResource(R.drawable.background_unselected)
                radioButton5.setBackgroundResource(R.drawable.background_unselected)
                radioButton6.setBackgroundResource(R.drawable.background_unselected)
                radioButton7.setBackgroundResource(R.drawable.background_unselected)

            }
            R.id.day5 -> {
                selectedDay=5
              //  FetchTopicsTask().execute(eventId)

                radioButton5.setBackgroundResource(R.drawable.background_selected)
                radioButton5.setTextColor(Color.WHITE)
                radioButton1.setTextColor(Color.BLACK)
                radioButton3.setTextColor(Color.BLACK)
                radioButton4.setTextColor(Color.BLACK)
                radioButton2.setTextColor(Color.BLACK)
                radioButton6.setTextColor(Color.BLACK)
                radioButton7.setTextColor(Color.BLACK)

                radioButton1.setBackgroundResource(R.drawable.background_unselected)
                radioButton3.setBackgroundResource(R.drawable.background_unselected)
                radioButton4.setBackgroundResource(R.drawable.background_unselected)
                radioButton2.setBackgroundResource(R.drawable.background_unselected)
                radioButton6.setBackgroundResource(R.drawable.background_unselected)
                radioButton7.setBackgroundResource(R.drawable.background_unselected)

                setDataSessionAdapter(selectedDay)
            }
            R.id.day6 -> {
                selectedDay=6
              //  FetchTopicsTask().execute(eventId)
                radioButton6.setBackgroundResource(R.drawable.background_selected)
                radioButton6.setTextColor(Color.WHITE)
                radioButton1.setTextColor(Color.BLACK)
                radioButton3.setTextColor(Color.BLACK)
                radioButton4.setTextColor(Color.BLACK)
                radioButton5.setTextColor(Color.BLACK)
                radioButton2.setTextColor(Color.BLACK)
                radioButton7.setTextColor(Color.BLACK)

                radioButton1.setBackgroundResource(R.drawable.background_unselected)
                radioButton3.setBackgroundResource(R.drawable.background_unselected)
                radioButton4.setBackgroundResource(R.drawable.background_unselected)
                radioButton5.setBackgroundResource(R.drawable.background_unselected)
                radioButton2.setBackgroundResource(R.drawable.background_unselected)
                radioButton7.setBackgroundResource(R.drawable.background_unselected)

                setDataSessionAdapter(selectedDay)
            }
            R.id.day7 -> {
                selectedDay=7
            //    FetchTopicsTask().execute(eventId)

                setDataSessionAdapter(selectedDay)

                radioButton7.setBackgroundResource(R.drawable.background_selected)
                radioButton7.setTextColor(Color.WHITE)
                radioButton1.setTextColor(Color.BLACK)
                radioButton3.setTextColor(Color.BLACK)
                radioButton4.setTextColor(Color.BLACK)
                radioButton5.setTextColor(Color.BLACK)
                radioButton6.setTextColor(Color.BLACK)
                radioButton2.setTextColor(Color.BLACK)

                radioButton1.setBackgroundResource(R.drawable.background_unselected)
                radioButton3.setBackgroundResource(R.drawable.background_unselected)
                radioButton4.setBackgroundResource(R.drawable.background_unselected)
                radioButton5.setBackgroundResource(R.drawable.background_unselected)
                radioButton6.setBackgroundResource(R.drawable.background_unselected)
                radioButton2.setBackgroundResource(R.drawable.background_unselected)
            }
        }
    }

    private fun setDataSessionAdapter(selectedDay: Int) {



        Log.d(ConstanstsApp.tag,"sessionsList=>"+sessionsList)

        // Filter the sessionsList to get sessions where event_day is 1
        val filteredSessions = sessionsList.filter { it.event_day == selectedDay }

        val filteredTopics = topicsList.filter { it.event_day == selectedDay }

// Log the filtered sessions
        Log.d(ConstanstsApp.tag, "Filtered sessions where event_day is $selectedDay: $filteredSessions")
        Log.d(ConstanstsApp.tag, "Filtered Topic where event_day is $selectedDay: $filteredTopics")

        if (filteredSessions.isNotEmpty()) {
            sessionRecyclerView.visibility = View.VISIBLE
            // If there are sessions for the selected day, show the session data
            sessionAdapter = SessionAdapter(applicationContext, this@EventActivity2)
            sessionRecyclerView.adapter = sessionAdapter
            sessionRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

            sessionAdapter.setSessions(filteredSessions)
            noSessionTextView.visibility = View.GONE
        } else if (filteredTopics.isNotEmpty()){
            // If there are no sessions for the selected day, hide session data
            sessionRecyclerView.visibility = View.VISIBLE
            noSessionTextView.visibility = View.GONE

            Log.d(ConstanstsApp.tag, "No data available for selectedDay $selectedDay")


            topicAdapter = TopicAdapter1()
            sessionRecyclerView.adapter = topicAdapter
            sessionRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
            topicAdapter.setTopics(filteredTopics)

        }
        else
        {
            noSessionTextView.visibility = View.VISIBLE
            sessionRecyclerView.visibility = View.GONE
        }


    }

    inner class FetchTopicsTask : AsyncTask<String, Void, Topics1>() {
        override fun doInBackground(vararg params: String?): Topics1 {
            val eventId = params[0]

            val url = "https://www.telemedocket.com/BNA/public/gettopics?eid=$eventId"

            Log.d(ConstanstsApp.tag,"Topic url=>"+url)
            val jsonString = URL(url).readText()
            Log.d(ConstanstsApp.tag,""+jsonString)
            return parseJsonTopic(jsonString)
        }
        @SuppressLint("SuspiciousIndentation")
        override fun onPostExecute(result: Topics1?) {
            Log.d(ConstanstsApp.tag,"topic response=>"+result)
            Log.d(ConstanstsApp.tag,"selectedDay in onPostExecute=>"+selectedDay)
            if (result != null && result.success == 1 && !result.data.isNullOrEmpty()) {

                Log.d(ConstanstsApp.tag,"topics Data=>"+result.data)
                // Filter topics based on the selected day
                val filteredTopics = result.data.filter { it.event_day == selectedDay }
                topicAdapter.setTopics(filteredTopics)

                for (i in 0 until result.data.size)
                {
                    var data=result.data[i]
                    var event_day=data.event_day
                    var session_id=result.data[i].session_id
                    Log.d(ConstanstsApp.tag,"session_id=>"+session_id)
                    Log.d(ConstanstsApp.tag," selected event_day=>"+event_day)
                    Log.d(ConstanstsApp.tag,"selectedDay=>"+event_day)

                    if (event_day==selectedDay)
                    {
                        Log.d(ConstanstsApp.tag,"session_id in post of topic=>"+session_id)


                        when(session_id)
                        {
                            0->
                            {
                                val filteredTopics = mutableListOf<org.bombayneurosciences.bna_2023.Model.Topic_new.Data>()
                                Log.d(ConstanstsApp.tag,"session id if 0=>"+session_id)
                                Log.d(ConstanstsApp.tag,"session id if 0 event_day=>"+event_day)

                                val created_at=data.created_at
                                val end_time=data.end_time
                                val event_date=data.event_date
                                val event_day=data.event_day
                                val event_id=data.event_id
                                val id=data.id
                                val is_active=data.is_active
                                val is_deleted=data.is_deleted
                                val moderators=data.moderators
                                val session_id=data.session_id
                                val slides=data.slides
                                val speaker=data.speaker
                                val start_time=data.start_time
                                val title=data.title
                                val type=data.type
                                val updated_at=data.updated_at
                                val quecnt = data.quecnt

                                val topicData=org.bombayneurosciences.bna_2023.Model.Topic_new.Data(created_at, end_time, event_date, event_day, event_id, id, is_active, is_deleted, moderators, session_id, slides, speaker, start_time, title, type, updated_at,quecnt)
                                filteredTopics.add(topicData)
                                topicAdapter = TopicAdapter1()
                                sessionRecyclerView.adapter = topicAdapter
                                sessionRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                                topicAdapter.setTopics(filteredTopics)
                            }
                            else->
                            {
                                Log.d(ConstanstsApp.tag,"eventId in else"+eventId)
                                Log.d(ConstanstsApp.tag,"selectedDay in else"+selectedDay)
                                Log.d(ConstanstsApp.tag,"FetchSessionsTask call")
                                FetchSessionsTask().execute(eventId.toString())
                            }
                        }
                     /*   if (session_id==0)
                        {
                            val filteredTopics = mutableListOf<org.bombayneurosciences.bna_2023.Model.Topic_new.Data>()
                            Log.d(ConstanstsApp.tag,"session id if 0=>"+session_id)
                            Log.d(ConstanstsApp.tag,"session id if 0 event_day=>"+event_day)

                            val created_at=data.created_at
                            val end_time=data.end_time
                            val event_date=data.event_date
                            val event_day=data.event_day
                            val event_id=data.event_id
                            val id=data.id
                            val is_active=data.is_active
                            val is_deleted=data.is_deleted
                            val moderators=data.moderators
                            val session_id=data.session_id
                            val slides=data.slides
                            val speaker=data.speaker
                            val start_time=data.start_time
                            val title=data.title
                            val type=data.type
                            val updated_at=data.updated_at
                            val quecnt = data.quecnt

                            val topicData=org.bombayneurosciences.bna_2023.Model.Topic_new.Data(created_at, end_time, event_date, event_day, event_id, id, is_active, is_deleted, moderators, session_id, slides, speaker, start_time, title, type, updated_at,quecnt)
                            filteredTopics.add(topicData)
                            topicAdapter = TopicAdapter1()
                            sessionRecyclerView.adapter = topicAdapter
                            sessionRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                            topicAdapter.setTopics(filteredTopics)
                        }
                        else
                        {
                            Log.d(ConstanstsApp.tag,"eventId in else"+eventId)
                            Log.d(ConstanstsApp.tag,"selectedDay in else"+selectedDay)
                            Log.d(ConstanstsApp.tag,"FetchSessionsTask call")
                            FetchSessionsTask().execute(eventId.toString())
                        }*/
                    }
                }
                if (filteredTopics.isEmpty()) {
                    Log.d(ConstanstsApp.tag,"No data available3")
                    // If no topics available for the selected day, set "No Session Available" visibility
                    noSessionTextView.visibility = View.VISIBLE
                    sessionRecyclerView.visibility = View.GONE
                }
                else {
                    // If topics are available, update the TopicAdapter and set visibility accordingly
                    topicAdapter.setTopics(filteredTopics)
                    noSessionTextView.visibility = View.GONE
                    sessionRecyclerView.visibility = View.VISIBLE
                }
            } else {
                FetchSessionsTask().execute(eventId.toString())
            }
    }
}
    private fun parseJsonTopic(jsonString: String): Topics1 {
        val topics = mutableListOf<org.bombayneurosciences.bna_2023.Model.Topic_new.Data>()
        val jsonObject = JSONObject(jsonString)

        try {
             successTopic = jsonObject.getInt("success")
            when (successTopic) {
                1 -> {
                    val jsonArray = jsonObject.getJSONArray("data")
                    Log.d(ConstanstsApp.tag, "" + jsonArray.length())

                    if (jsonArray.length() != 0) {
                        for (i in 0 until jsonArray.length()) {
                            val topic = jsonArray.getJSONObject(i)

                            val created_at = topic.getString("created_at")
                            val end_time = topic.getString("end_time")
                            val event_date = topic.getString("event_date")
                            Log.d(ConstanstsApp.tag,"eventdate topic"+event_date)

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
                            // Create Data object and add to the list
                            topics.add(
                                org.bombayneurosciences.bna_2023.Model.Topic_new.Data(
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
                                    slides, speaker,
                                    start_time,
                                    title,
                                    type,
                                    updated_at,
                                    quecnt
                                )
                            )
                        }
                    }
                }
                0 -> {
                    // Handle case where success is 0
                    Log.d(ConstanstsApp.tag, "Data not found. Success is 0.")
                }
                else -> {
                    // Handle unexpected values of success
                    Log.e(ConstanstsApp.tag, "Unexpected value for success: $successTopic")
                }
            }
        } catch (e: JSONException) {
            // Handle JSON parsing exception
            Log.e(ConstanstsApp.tag, "JSON parsing exception: ${e.message}")
        }
        return Topics1(topics, successTopic)
    }

    inner class FetchSessionsTask : AsyncTask<String, Void, EventSessions>() {
        override fun doInBackground(vararg params: String?): EventSessions {
            val eventId = params[0]

            Log.d(ConstanstsApp.tag,"call FetchSessionsTask2")
            val url = "https://www.telemedocket.com/BNA/public/getsessions?eid=$eventId"
            Log.d(ConstanstsApp.tag,"session url=>"+url)
            val jsonString = URL(url).readText()
            return parseJson(jsonString)
        }
        override fun onPostExecute(result: EventSessions?) {

            Log.d(ConstanstsApp.tag,"data of EventSessions=> "+result!!.data.toString())
            if (result != null && result.success == 1 && !result.data.isNullOrEmpty()) {
                val size = result.data.size
                Log.d(ConstanstsApp.tag, "result data size of session => $size")
                if (size > 0)
                {
                    when(selectedDay)
                    {
                        1->
                        {
                            handleSessionData(result.data,selectedDay)
                        }
                        2->
                        {
                            handleSessionData(result.data,selectedDay)
                        }
                        3->
                        {
                            handleSessionData(result.data,selectedDay)
                        }
                        4->
                        {
                            handleSessionData(result.data,selectedDay)
                        }
                        5->
                        {
                            handleSessionData(result.data,selectedDay)
                        }
                        6->
                        {
                            handleSessionData(result.data,selectedDay)
                        }
                        7->
                        {
                            handleSessionData(result.data,selectedDay)
                        }
                    }
                }

            } else {
                FetchTopicsTask1().execute(eventId.toString())
            }
        }
    }
    inner class FetchTopicsTask1 : AsyncTask<String, Void, Topics1>() {
        override fun doInBackground(vararg params: String?): Topics1 {
            val eventId = params[0]

            val url = "https://www.telemedocket.com/BNA/public/gettopics?eid=$eventId"
            Log.d(ConstanstsApp.tag,"Topic url=>"+url)
            val jsonString = URL(url).readText()
            Log.d(ConstanstsApp.tag,""+jsonString)
            return parseJsonTopic(jsonString)
        }
        @SuppressLint("SuspiciousIndentation")
        override fun onPostExecute(result: Topics1?) {

            Log.d(ConstanstsApp.tag,"topic response=>"+result)
            Log.d(ConstanstsApp.tag,"selectedDay in onPostExecute=>"+selectedDay)
            if (result != null && result.success == 1 && !result.data.isNullOrEmpty()) {

                Log.d(ConstanstsApp.tag,"topics Data=>"+result.data)
                val filteredTopics = result.data.filter { it.event_day == selectedDay }
                topicAdapter.setTopics(filteredTopics)

                for (i in 0 until result.data.size)
                {
                    var data=result.data[i]
                    var event_day=data.event_day
                    var session_id=result.data[i].session_id
                    Log.d(ConstanstsApp.tag,"session_id=>"+session_id)
                    Log.d(ConstanstsApp.tag," selected event_day=>"+event_day)

                    Log.d(ConstanstsApp.tag,"selectedDay=>"+event_day)

                    if (event_day==selectedDay)
                    {
                        Log.d(ConstanstsApp.tag,"session_id in post of topic=>"+session_id)
                        val filteredTopics = mutableListOf<org.bombayneurosciences.bna_2023.Model.Topic_new.Data>()
                            Log.d(ConstanstsApp.tag,"session id if 0=>"+session_id)
                            Log.d(ConstanstsApp.tag,"session id if 0 event_day=>"+event_day)

                            val created_at=data.created_at
                            val end_time=data.end_time
                            val event_date=data.event_date
                            val event_day=data.event_day
                            val event_id=data.event_id
                            val id=data.id
                            val is_active=data.is_active
                            val is_deleted=data.is_deleted
                            val moderators=data.moderators
                            val session_id=data.session_id
                            val slides=data.slides
                            val speaker=data.speaker
                            val start_time=data.start_time
                            val title=data.title
                            val type=data.type
                            val updated_at=data.updated_at
                        val quecnt = data.quecnt

                        val topicData=org.bombayneurosciences.bna_2023.Model.Topic_new.Data(created_at, end_time, event_date, event_day, event_id, id, is_active, is_deleted, moderators, session_id, slides, speaker, start_time, title, type, updated_at,quecnt)
                            filteredTopics.add(topicData)

                        topicAdapter = TopicAdapter1()

                        sessionRecyclerView.adapter = topicAdapter
                            sessionRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                            topicAdapter.setTopics(filteredTopics)
                    }
                }
                if (filteredTopics.isEmpty()) {
                    Log.d(ConstanstsApp.tag,"No data available3")
                    // If no topics available for the selected day, set "No Session Available" visibility
                    noSessionTextView.visibility = View.VISIBLE
                    sessionRecyclerView.visibility = View.GONE
                } else {

                    // If topics are available, update the TopicAdapter and set visibility accordingly
                    topicAdapter.setTopics(filteredTopics)
                    noSessionTextView.visibility = View.GONE
                    sessionRecyclerView.visibility = View.VISIBLE
                }

            } else {
                FetchTopicsTask().execute(eventId.toString())
            }
        }
    }
    private fun handleSessionData(data: List<Data>, selectedDay: Int) {
        Log.d(ConstanstsApp.tag, "selectedDay in handleSessionData => $selectedDay")
        val sessionsForSelectedDay = data.filter { it.event_day == selectedDay }

        if (sessionsForSelectedDay.isNotEmpty()) {
            // If there are sessions for the selected day, show the session data
            sessionAdapter = SessionAdapter(applicationContext, this@EventActivity2)
            sessionRecyclerView.adapter = sessionAdapter
            sessionRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

            sessionAdapter.setSessions(sessionsForSelectedDay)
            noSessionTextView.visibility = View.GONE
        } else {
            // If there are no sessions for the selected day, hide session data
            noSessionTextView.visibility = View.VISIBLE
            sessionRecyclerView.visibility = View.GONE
            Log.d(ConstanstsApp.tag, "No data available for selectedDay $selectedDay")
        }
    }
    private fun onRadioButtonChanged(day: Int) {
        // Implement the logic to fetch and display sessions for the selected day
        val selectedDaySessions = sessionAdapter.getSessionsForDay(day)

        // Update the RecyclerView with the sessions for the selected day
        sessionAdapter.setSessions(selectedDaySessions)

        // Handle visibility of "No Session Available" TextView
        noSessionTextView.visibility =
            if (selectedDaySessions.isEmpty()) View.VISIBLE else View.GONE
    }
    private fun parseJson(jsonString: String): EventSessions {
        val sessions = mutableListOf<Data>()
        val jsonObject = JSONObject(jsonString)

        val success = jsonObject.getInt("success")
        val jsonArray = jsonObject.getJSONArray("data")

        Log.d(ConstanstsApp.tag,"Session data=>"+jsonArray)

        for (i in 0 until jsonArray.length()) {
            val session = jsonArray.getJSONObject(i)

            val chairpersons = session.getString("chairpersons")
            val createdAt = session.getString("created_at")
            val endTime = session.getString("end_time")
            val eventDate = session.getString("event_date")
            val eventDay = session.getInt("event_day")
            val eventId = session.getInt("event_id")
            val id = session.getInt("id")
            val isActive = session.getInt("is_active")
            val isDeleted = session.getInt("is_deleted")
            val sno = session.getInt("sno")
            val startTime = session.getString("start_time")
            val title = session.getString("title")
            val type = session.getString("type")
            val updatedAt = session.getString("updated_at")

            sessions.add(
                Data(
                    chairpersons,
                    createdAt,
                    endTime,
                    eventDate,
                    eventDay,
                    eventId,
                    id,
                    isActive,
                    isDeleted,
                    sno,
                    startTime,
                    title,
                    type,
                    updatedAt
                )
            )
        }
        return EventSessions(sessions, success)
    }
   /* override fun onBackPressed() {
        super.onBackPressed()

       *//* val intent = Intent(this, EventActivity::class.java)
        startActivity(intent)
        finish()*//*



    }*/


   /* override fun onBackPressed() {
        super.onBackPressed()

        Log.e("modi ",""+"sunil1")
        val intent = Intent(this, EventActivity::class.java)
        intent.putExtra("isBackPressed", true)
        startActivity(intent)
        finish()
    }*/

    private fun handleBackPress() {
        Log.e("modi", "sunil1")
        val intent = Intent(this, EventActivity::class.java)
        intent.putExtra("isBackPressed", true)
        intent.putExtra(EventActivity.EXTRA_EVENT_TYPE, EXTRA_EVENT_TYPE)
        startActivity(intent)
        finish()
    }



   inner class FetchSessionsTask2 : AsyncTask<String, Void, List<Data>>() {

        override fun doInBackground(vararg params: String?): List<Data> {
            val eventId = params[0]
            val url = "https://www.telemedocket.com/BNA/public/getsessions?eid=$eventId"

            // Initialize an empty list to hold session data
            sessionsList = mutableListOf<Data>()

            try {
                val jsonString = URL(url).readText()
                val json = JSONObject(jsonString)
                val success = json.getInt("success")

                if (success == 1) {
                    val jsonArray = json.getJSONArray("data")

                    // Loop through the JSON array and parse session data
                    for (i in 0 until jsonArray.length()) {
                        val sessionJson = jsonArray.getJSONObject(i)
                        val session = parseSession(sessionJson)

                        // Add the parsed session to the list
                        sessionsList.add(session)
                    }
                }
            } catch (e: Exception) {
                Log.e("FetchSessionsTask", "Error fetching sessions: ${e.message}")
            }

            return sessionsList
        }

        private fun parseSession(json: JSONObject): Data {
            // Parse session JSON object into a Data object
            return Data(
                json.getString("chairpersons"),
                json.getString("created_at"),
                json.getString("end_time"),
                json.getString("event_date"),
                json.getInt("event_day"),
                json.getInt("event_id"),
                json.getInt("id"),
                json.getInt("is_active"),
                json.getInt("is_deleted"),
                json.getInt("sno"),
                json.getString("start_time"),
                json.getString("title"),
                json.getString("type"),
                json.getString("updated_at")
            )
        }

        override fun onPostExecute(result: List<Data>) {
            // Handle the result, such as updating UI with the fetched sessions
            // For example:
            // myAdapter.setData(result)

            selectedDay=1
            setDataSessionAdapter(selectedDay)

        }
    }



   inner class FetchTopicsTask2 : AsyncTask<String, Void, List<org.bombayneurosciences.bna_2023.Model.Topic_new.Data>>() {

        @SuppressLint("StaticFieldLeak")
        private lateinit var topicAdapter: TopicAdapter1

        override fun doInBackground(vararg params: String?): List<org.bombayneurosciences.bna_2023.Model.Topic_new.Data>? {
            val eventId = params[0]
            val url = "https://www.telemedocket.com/BNA/public/gettopics?eid=$eventId"

            topicsList = mutableListOf<org.bombayneurosciences.bna_2023.Model.Topic_new.Data>()

            try {
                val jsonString = URL(url).readText()
                val json = JSONObject(jsonString)
                val success = json.getInt("success")

                if (success == 1) {
                    val jsonArray = json.getJSONArray("data")

                    // Loop through the JSON array and parse topic data
                    for (i in 0 until jsonArray.length()) {
                        val topicJson = jsonArray.getJSONObject(i)
                        val topic = parseTopic(topicJson)

                        // Add the parsed topic to the list
                        topicsList.add(topic)
                    }
                }
            } catch (e: Exception) {
                Log.e("FetchTopicsTask", "Error fetching topics: ${e.message}")
            }

            return topicsList
        }

        private fun parseTopic(json: JSONObject): org.bombayneurosciences.bna_2023.Model.Topic_new.Data {
            // Parse topic JSON object into a Data object
            return org.bombayneurosciences.bna_2023.Model.Topic_new.Data(
                json.getString("created_at"),
                json.getString("end_time"),
                json.getString("event_date"),
                json.getInt("event_day"),
                json.getInt("event_id"),
                json.getInt("id"),
                json.getInt("is_active"),
                json.getInt("is_deleted"),
                json.getString("moderators"),
                json.getInt("session_id"),
                json.getString("slides"),
                json.getString("speaker"),
                json.getString("start_time"),
                json.getString("title"),
                json.getString("type"),
                json.getString("updated_at"),
                json.getString("quecnt")
            )
        }

        override fun onPostExecute(result: List<org.bombayneurosciences.bna_2023.Model.Topic_new.Data>) {
            // Handle the result, such as updating UI with the fetched topics
            // For example:
            // topicAdapter.setTopics(result)

            selectedDay=1
            setDataSessionAdapter(selectedDay)

        }


    }






}


