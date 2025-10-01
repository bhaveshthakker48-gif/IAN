package org.bombayneurosciences.bna_2023.Activity



import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.Data.SharedPreferencesManager
import org.bombayneurosciences.bna_2023.Model.Events.Data
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.adapter.EventAdapter
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import pl.droidsonroids.gif.GifImageView
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class EventActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_EVENT_TYPE = "eventType"
    }

    private lateinit var eventRecyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var noDataTextView: TextView
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var gifImageView: GifImageView
    private lateinit var sessionManager: SessionManager1

    private lateinit var upcomingRadioButton: RadioButton
    private lateinit var pastRadioButton: RadioButton
    private lateinit var refreshButton: ImageView

    private var selectedEventType: String = "upcoming"
    private var savedEventsList: List<Data>? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)


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


        initViews()
        setupListeners()

        selectedEventType = intent.getStringExtra(EXTRA_EVENT_TYPE) ?: "upcoming"

        Log.e("selectedEventType ",""+selectedEventType)

        if (selectedEventType == "past") {
            pastRadioButton.isChecked = true
        } else {
            upcomingRadioButton.isChecked = true
        }
        styleRadioButtons()

        if (savedEventsList != null) {
            eventAdapter.setEvents(savedEventsList!!)
            noDataTextView.visibility = if (savedEventsList!!.isEmpty()) TextView.VISIBLE else TextView.GONE
        } else {
            FetchEventsTask().execute(selectedEventType)
        }
    }

    private fun initViews() {
        eventRecyclerView = findViewById(R.id.recyclerview_event)
        noDataTextView = findViewById(R.id.no_data_text_view)
        sharedPreferencesManager = SharedPreferencesManager(applicationContext)
        gifImageView = findViewById(R.id.gifImageView)
        sessionManager = SessionManager1(this)

        upcomingRadioButton = findViewById(R.id.upcoming_radio_button)
        pastRadioButton = findViewById(R.id.past_radio_button)
        refreshButton = findViewById(R.id.refreshbutton)

        eventAdapter = EventAdapter { selectedEvent, _ ->
            if (isNetworkAvailable()) {
                val intent = Intent(this, EventActivity2::class.java)
                intent.putExtra("eventId", selectedEvent.id)
                intent.putExtra("no_of_sessions", selectedEvent.NOofsession)
                intent.putExtra("start_date", selectedEvent.start_date)
                intent.putExtra("end_date", selectedEvent.end_date)
                intent.putExtra("name", selectedEvent.name)

                val isPastEvent = selectedEvent.end_date < getCurrentDate()
                intent.putExtra(EventActivity2.EXTRA_FROM_PAST_EVENT, isPastEvent)

                sessionManager.setName(selectedEvent.name)
                intent.putExtra(EXTRA_EVENT_TYPE, selectedEventType) // send current type
                startActivity(intent)
                finish()
            } else {
                showAlert("Please connect to the internet")
            }
        }

        eventRecyclerView.adapter = eventAdapter
        eventRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        findViewById<RadioGroup>(R.id.radio_group_event).setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.upcoming_radio_button -> {
                    selectedEventType = "upcoming"
                    styleRadioButtons()
                    FetchEventsTask().execute("upcoming")
                }
                R.id.past_radio_button -> {
                    selectedEventType = "past"
                    styleRadioButtons()
                    FetchEventsTask().execute("past")
                }
            }
        }

        refreshButton.setOnClickListener {
            refreshPage()
        }

        findViewById<ImageView>(R.id.backbutton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun styleRadioButtons() {
        if (selectedEventType == "upcoming") {
            upcomingRadioButton.setBackgroundResource(R.drawable.background_selected)
            upcomingRadioButton.setTextColor(Color.WHITE)
            pastRadioButton.setBackgroundResource(R.drawable.background_unselected)
            pastRadioButton.setTextColor(Color.BLACK)
        } else {
            pastRadioButton.setBackgroundResource(R.drawable.background_selected)
            pastRadioButton.setTextColor(Color.WHITE)
            upcomingRadioButton.setBackgroundResource(R.drawable.background_unselected)
            upcomingRadioButton.setTextColor(Color.BLACK)
        }
    }

    private fun refreshPage() {
        recreate()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun showAlert(message: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.intenet_alert)

        val textView = dialog.findViewById<TextView>(R.id.textViewLogoutConfirmation)
        textView.text = message

        dialog.findViewById<Button>(R.id.buttonYes).setOnClickListener { dialog.dismiss() }
        dialog.findViewById<Button>(R.id.buttonNo).setOnClickListener { dialog.dismiss() }

        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    inner class FetchEventsTask : AsyncTask<String, Void, List<Data>>() {
        override fun doInBackground(vararg params: String?): List<Data> {
            val eventType = params[0]

            if (isNetworkAvailable()) {
                val url = "https://www.telemedocket.com/BNA/public/getevents"
                val jsonString = URL(url).readText()

                val events = parseJson(jsonString, eventType)
                sharedPreferencesManager.saveEvents(events)

                return events
            } else {
                return sharedPreferencesManager.getStoredEvents()
            }
        }

        override fun onPostExecute(result: List<Data>?) {
            if (result != null && result.isNotEmpty()) {
                savedEventsList = result
                eventAdapter.setEvents(result.sortedBy { it.start_date })
                noDataTextView.visibility = TextView.GONE
            } else {
                savedEventsList = emptyList()
                eventAdapter.setEvents(emptyList())
                noDataTextView.visibility = TextView.VISIBLE
            }
        }

        private fun parseJson(jsonString: String, eventType: String?): List<Data> {
            val events = mutableListOf<Data>()
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val jsonArray = JSONObject(jsonString).getJSONArray("data")
            for (i in 0 until jsonArray.length()) {
                val event = jsonArray.getJSONObject(i)
                val eventStartDate = event.getString("start_date")
                val eventEndDate = event.getString("end_date")

                if ((eventType == "upcoming" && (eventStartDate > currentDate || eventEndDate >= currentDate)) ||
                    (eventType == "past" && eventEndDate < currentDate)) {

                    events.add(
                        Data(
                            event.getString("NOofsession"),
                            event.getString("created_at"),
                            event.getString("end_date"),
                            event.getString("end_time"),
                            event.getString("end_to_time"),
                            event.getString("etype"),
                            event.getString("id"),
                            event.getString("is_active"),
                            event.getString("is_deleted"),
                            event.getString("name"),
                            event.getString("organizers"),
                            event.getString("start_date"),
                            event.getString("start_date"),
                            event.getString("start_time"),
                            event.getString("start_to_time"),
                            event.getString("updated_at"),
                            event.getString("username"),
                            event.getString("venue")
                        )
                    )
                }
            }
            return events
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}



/*import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.Data.SharedPreferencesManager
import org.bombayneurosciences.bna_2023.Model.Events.Data
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.adapter.EventAdapter
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import org.json.JSONObject
import pl.droidsonroids.gif.GifImageView
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class EventActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_EVENT_TYPE = "eventType"
    }
    private lateinit var eventRecyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var noDataTextView: TextView
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var upcomingRadioButton: RadioButton
    private lateinit var sharedPreferencesManager1: SessionManager1

    private lateinit var gifImageView: GifImageView
    lateinit var sessionManager: SessionManager1



    @SuppressLint("SuspiciousIndentation", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        eventRecyclerView = findViewById(R.id.recyclerview_event)
        noDataTextView = findViewById(R.id.no_data_text_view)
        sharedPreferencesManager = SharedPreferencesManager(applicationContext)
        upcomingRadioButton = findViewById(R.id.upcoming_radio_button)
        sharedPreferencesManager1 = SessionManager1(this)
        val refreshButton = findViewById<ImageView>(R.id.refreshbutton)
        gifImageView = findViewById(R.id.gifImageView)


        upcomingRadioButton.setBackgroundResource(R.drawable.background_selected)
        upcomingRadioButton.setTextColor(Color.WHITE)

        eventAdapter = EventAdapter { selectedEvent, daysUntilEvent ->

            // Calculate days until the event
            val daysUntilEvent = calculateDaysUntilEvent(selectedEvent.start_date)
            val isUpcoming = upcomingRadioButton.isChecked
            if (isNetworkAvailable()) {

            Log.d(ConstanstsApp.tag, "click id=>" + selectedEvent.id)
            Log.d(ConstanstsApp.tag, "NOofsession=>" + selectedEvent.NOofsession)
            Log.d(ConstanstsApp.tag, "daysUntilEvent=>" + daysUntilEvent)
            Log.d(ConstanstsApp.tag, "start_date=>" + selectedEvent.start_date)
            Log.d(ConstanstsApp.tag, "end_date=>" + selectedEvent.end_date)

            val intent = Intent(this, EventActivity2::class.java)
            intent.putExtra("eventId", selectedEvent.id)
            intent.putExtra("no_of_sessions", selectedEvent.NOofsession)
            intent.putExtra("days_until_event", daysUntilEvent)
            intent.putExtra("start_date", selectedEvent.start_date)
            intent.putExtra("end_date", selectedEvent.end_date)
                intent.putExtra("name",selectedEvent.name)
// Pass the event type to EventActivity2
            // Determine if it's a past event based on your logic
            val isPastEvent = selectedEvent.end_date < getCurrentDate()

                // Set the flag indicating whether it's a past event or not
                intent.putExtra(EventActivity2.EXTRA_FROM_PAST_EVENT, isPastEvent)

                sessionManager=SessionManager1(this)
                 sessionManager.setName(selectedEvent.name)

                startActivity(intent)
                finish()
//                startActivity(intent)
            } else {
                // No internet, show a dialog to the user
                showAlert("Please connect to the internet",)
            }
        }
        refreshButton.setOnClickListener {
            // Call the method to handle the refresh logic
            refreshPage()
        }
        eventRecyclerView.adapter = eventAdapter
        eventRecyclerView.layoutManager = LinearLayoutManager(this)

        val itemDecoration = Dashline_event(this)
        eventRecyclerView.addItemDecoration(itemDecoration)

        eventRecyclerView.addItemDecoration(DottedLineItemDecoration(this))


        val radioGroup = findViewById<RadioGroup>(R.id.radio_group_event)
        val upcomingRadioButton = findViewById<RadioButton>(R.id.upcoming_radio_button)
        val pastRadioButton = findViewById<RadioButton>(R.id.past_radio_button)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.upcoming_radio_button -> {
                    upcomingRadioButton.setBackgroundResource(R.drawable.background_selected)
                    upcomingRadioButton.setTextColor(Color.WHITE)
                    pastRadioButton.setTextColor(Color.BLACK)

                    pastRadioButton.setBackgroundResource(R.drawable.background_unselected)
                    FetchEventsTask().execute("upcoming")
                }
                R.id.past_radio_button -> {
                    pastRadioButton.setBackgroundResource(R.drawable.background_selected)
                    pastRadioButton.setTextColor(Color.WHITE)
                    upcomingRadioButton.setTextColor(Color.BLACK)

                    upcomingRadioButton.setBackgroundResource(R.drawable.background_unselected)
                    FetchEventsTask().execute("past")
                }
            }
        }



        val backButton = findViewById<ImageView>(R.id.backbutton)

        // Set a click listener for the back button
        backButton.setOnClickListener {
val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
          //  sharedPreferencesManager1.setBackState(true)

        }

        FetchEventsTask().execute("upcoming") // Default to upcoming events
    }

    private fun refreshPage() {
        recreate()

    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }


    private fun showAlert(message: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.intenet_alert)

        // Find views in the custom dialog layout
        val notificationTitle: TextView = dialog.findViewById(R.id.notificationTitle)
        val textViewLogoutConfirmation: TextView = dialog.findViewById(R.id.textViewLogoutConfirmation)
        val buttonYes: Button = dialog.findViewById(R.id.buttonYes)
        val buttonNo: Button = dialog.findViewById(R.id.buttonNo)

        // Set notification title and confirmation text
        notificationTitle.text = "Connectivity Issue"
        textViewLogoutConfirmation.text = message

        // Set click listener for the Yes button
        buttonYes.setOnClickListener {
            // Handle the Yes button click event
            dialog.dismiss()
        }

        // Set click listener for the No button
        buttonNo.setOnClickListener {
            // Handle the No button click event
            dialog.dismiss()
        }

        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun calculateDaysUntilEvent(eventStartDate: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.format(Date())

        val startDateTime = sdf.parse(eventStartDate)
        val currentDateTime = sdf.parse(currentDate)

        val diffInMillies = startDateTime.time - currentDateTime.time
        return (diffInMillies / (24 * 60 * 60 * 1000)).toInt()
    }

    inner class FetchEventsTask : AsyncTask<String, Void, List<Data>>() {
        override fun doInBackground(vararg params: String?): List<Data> {
            val eventType = params[0]

            // Check for an active network connection
            if (isNetworkAvailable()) {
                Log.d(ConstanstsApp.tag, "Fetching events from the network.")

                val url = "https://www.telemedocket.com/BNA/public/getevents"
                val jsonString = URL(url).readText()

                val events = parseJson(jsonString, eventType)

                // Save events to SharedPreferences
                sharedPreferencesManager.saveEvents(events)

                return events
            } else {
                Log.d(ConstanstsApp.tag, "No active network. Retrieving events from SharedPreferences.")

                // If offline, retrieve events from SharedPreferences
                val storedEvents = sharedPreferencesManager.getStoredEvents()
                // Log the stored events
                Log.d(ConstanstsApp.tag, "Stored events: $storedEvents")
                sharedPreferencesManager.saveEvents(storedEvents)
                Log.d(ConstanstsApp.tag, "Stored events sharedPreferencesManager: "+ storedEvents)

                return storedEvents
            }
        }

        override fun onPostExecute(result: List<Data>?) {
            if (result != null && result.isNotEmpty()) {
                val sortedEvents = result.sortedBy { it.start_date } // Sort events by start date
                eventAdapter.setEvents(sortedEvents)
                noDataTextView.visibility = TextView.GONE
            } else {
                // If there are no events from the network and SharedPreferences is also empty,
                // then show the "No Data" message.
                val storedEvents = sharedPreferencesManager.getStoredEvents()
                if (storedEvents.isEmpty()) {
                    eventAdapter.setEvents(emptyList())
                    noDataTextView.visibility = TextView.VISIBLE
                } else {
                    // If there are stored events in SharedPreferences, display them in reverse order.
                    val reversedStoredEvents = storedEvents.reversed() // Reverse the stored events list
                    eventAdapter.setEvents(reversedStoredEvents)
                    noDataTextView.visibility = TextView.GONE
                }
            }
        }


        private fun isNetworkAvailable(): Boolean {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        }

        private fun parseJson(jsonString: String, eventType: String?): List<Data> {
            val events = mutableListOf<Data>()

            val currentDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val jsonArray = JSONObject(jsonString).getJSONArray("data")

            for (i in 0 until jsonArray.length()) {
                val event = jsonArray.getJSONObject(i)
                val eventStartDate = event.getString("start_date")
                val eventEndDate = event.getString("end_date")

                if ((eventType == "upcoming" && (eventStartDate > currentDate || eventEndDate >= currentDate)) ||
                    (eventType == "past" && eventEndDate < currentDate)
                )  {

                    val NOofsession= event.getString("NOofsession")
                    val created_at = event.getString("created_at")
                    val end_date = event.getString("end_date")
                    val end_time= event.getString("end_time")
                    val end_to_time = event.getString("end_to_time")
                    val etype = event.getString("etype")
                    val id = event.getString("id")
                    val is_active= event.getString("is_active")
                    val is_deleted= event.getString("is_deleted")
                    val name= event.getString("name")
                    val organizers= event.getString("organizers")
                    val start_date= event.getString("start_date")
                    val start_time= event.getString("start_time")
                    val start_to_time= event.getString("start_to_time")
                    val updated_at= event.getString("updated_at")
                    val username= event.getString("username")
                    val venue= event.getString("venue")

                    events.add(Data(NOofsession,created_at,end_date,end_time,end_to_time,etype,id,is_active,is_deleted,name,organizers,start_date,
                        start_date, start_time, start_to_time, updated_at, username, venue))

                }
            }

            return events
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
     //   sharedPreferencesManager1.setBackState(true)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }
    }*/
