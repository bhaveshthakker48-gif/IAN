package org.bombayneurosciences.bna_2023.Activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.Data.SharedPreferencesActivity
import org.bombayneurosciences.bna_2023.Data.SharedPreferencesManager
import org.bombayneurosciences.bna_2023.Model.Voting.Data
import org.bombayneurosciences.bna_2023.Model.Voting.SubmitResponse
import org.bombayneurosciences.bna_2023.Model.Voting.voting
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class VotingActivity : AppCompatActivity(), View.OnClickListener {
    private var lastSelectedOptionId: Int = -1

    lateinit var sessionManager: SessionManager1
    private var topicId: Int = -1
    private var eventId: Int = -1
    private var sessionId: Int = -1
    lateinit var submitButton: TextView
    var selectedOption: String? = null
    var userId: Int? = null
    private var aIsSelected = false
    private var bIsSelected = false
    private var cIsSelected = false
    private var dIsSelected = false
    private var eIsSelected = false
    private var selectedOptionId: Int = -1
    private var lastSelected: String = ""
    private lateinit var imgVoting: ImageView
    private lateinit var videoVoting: ImageView
    private val selectedOptions = mutableSetOf<String>()
    private lateinit var cardViewA: CardView
    private lateinit var cardViewB: CardView
    private lateinit var cardViewC: CardView
    private lateinit var cardViewD: CardView
    private lateinit var cardViewE: CardView
    private var isTimeUp: Boolean = false
    var name:String?=null
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    var id: String? = null
    var event_id: String? = null
    var session_id: String? = null
    var question_id: String? = null
    var question: String? = null
    var is_dummy: String? = null
    var cd_start_time: String? = null
    var correct_answer: String? = null

    var votingDataList: List<Data>? = null
    lateinit var imageA:ImageView
    var flagA=1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting)



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


        imageA = findViewById(R.id.optionImageA_votingActivity)
        val refreshButton = findViewById<ImageView>(R.id.refreshbutton)
        sharedPreferencesManager = SharedPreferencesManager(this)


        videoVoting = findViewById(R.id.video_voting)
        // Assuming you're passing topicId through intent
        topicId = intent.getIntExtra("topicId", -1)
        // Retrieve data from intent extras
        eventId = intent.getIntExtra("eventId", -1)
        sessionId = intent.getIntExtra("sessionId", -1)
             name = intent.getStringExtra("name")

        Log.d(ConstanstsApp.tag, "topicId in VotingActivity => $topicId")
        Log.d(ConstanstsApp.tag, "eventId in VotingActivity => $eventId")
        Log.d(ConstanstsApp.tag, "sessionId in VotingActivity => $sessionId")


        Log.d(ConstanstsApp.tag, "name in VotingActivity => $name")

        submitButton = findViewById(R.id.submitButton)
        // Find the CardViews for options A, B, C, D, and E
        cardViewA = findViewById(R.id.OptionA)
        cardViewB = findViewById(R.id.OptionB)
        cardViewC = findViewById(R.id.OptionC)
        cardViewD = findViewById(R.id.OptionD_voting)
        cardViewE = findViewById(R.id.OptionE_voting)
//        val eventHeaderTextView: TextView = findViewById(R.id.eventheader)
//        name?.let {
//            eventHeaderTextView.text = it
//        }
//        applyFadeInAnimation(eventHeaderTextView, 2000)

        val eventName = sharedPreferencesManager.getStoredSelectedEventName()

        val backButton = findViewById<ImageView>(R.id.backbutton)
        val optionIds = listOf(
            R.id.optionTextA, R.id.optionTextB, R.id.optionTextC, R.id.optionTextD, R.id.optionTextE
        )
// Set a click listener for the refresh button
        refreshButton.setOnClickListener {
            // Call the method to handle the refresh logic
            refreshPage()
        }

        submitButton.setOnClickListener(this)
        // userId = getUserIdFromPreferences(this)
        userId = SharedPreferencesActivity.getDelegateId(this)
        Log.d(ConstanstsApp.tag, "delegateId in VotingActivity" + userId)

        sessionManager=SessionManager1(this)
        val topic_id=sessionManager.getTopicId()
        val name_voting = sessionManager.getName()
        Log.d(ConstanstsApp.tag,"name_voting in voting activity=>"+name_voting)

        Log.d(ConstanstsApp.tag,"topic_id in voting activity=>"+topic_id)

//        val eventHeaderTextView: TextView = findViewById(R.id.eventheader)
//        name?.let {
//            eventHeaderTextView.text = it
//        }

        // Set a click listener for the back button
        backButton.setOnClickListener {

            val intent = Intent(this, TopicsActivity::class.java)
            intent.putExtra("name",name_voting)

            startActivity(intent)
            finish()
        }
        topicId=topic_id

        if (topicId != -1) {
            FetchVotingQuestionsTask().execute(topicId)


        } else {
            Log.e(ConstanstsApp.tag, "Invalid topic ID")
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

    private fun refreshPage() {
        recreate()
    }

    private fun getUserIdFromPreferences(context: Context): Int? {
        val sharedPreferences = SharedPreferencesActivity.getSharedPreferences(context)
        return sharedPreferences.getInt("userId", -1)
    }

    private fun handleOptionClick(optionCardId: List<Int>) {
        val optionCardIds = listOf(
            R.id.OptionA, R.id.OptionB, R.id.OptionC, R.id.OptionD_voting, R.id.OptionE_voting
        )

        optionCardIds.forEach { optionCardId ->
            findViewById<CardView>(optionCardId).setOnClickListener {


               // findViewById<CardView>(optionCardId).background = getUnselectedBackground()
// Reset the background for the previously selected option
                if (selectedOptionId != optionCardId) {
                    // Reset the background for the previously selected option
                    selectedOptionId.takeIf { it != -1 }?.let {
                        findViewById<CardView>(it).background = getUnselectedBackground()
                    }

                    // Set background for the newly selected option
                    findViewById<CardView>(optionCardId).background = getSelectedBackground()

                    selectedOptionId = optionCardId // Set the selected option ID
                }
                selectedOption = getOptionText(optionCardIds.indexOf(optionCardId))
                selectedOptionId = optionCardId // Set the selected option ID
                toggleSelectedState(selectedOption!!)

                // Update the selected state for the clicked option
                updateSelectedState(selectedOption!!)
                updateImageVisibility(selectedOption!!)

                // Update the image based on the selected option
                updateImage(optionCardId, selectedOption!!)
            }
            // Set background for the selected card
         //   findViewById<CardView>(optionCardId).background = getSelectedBackground()


        }

    }

    private fun getSelectedBackground(): Drawable {
        return ContextCompat.getDrawable(this, R.drawable.selected_option_background)!!
    }

    private fun getUnselectedBackground(): Drawable {
        return ContextCompat.getDrawable(this, R.drawable.unselected_option_background)!!
    }

    private fun toggleSelectedState(option: String) {
        // Reset all selected states to false
        aIsSelected = false
        bIsSelected = false
        cIsSelected = false
        dIsSelected = false
        eIsSelected = false

        // Update the selected state for the clicked option
        when (option) {
            "A" -> aIsSelected = true
            "B" -> bIsSelected = true
            "C" -> cIsSelected = true
            "D" -> dIsSelected = true
            "E" -> eIsSelected = true
        }

    }


    private fun updateSelectedState(selectedOption: String) {
            // Reset all selected states to false
            aIsSelected = false
            bIsSelected = false
            cIsSelected = false
            dIsSelected = false
            eIsSelected = false

            // Update the selected state for the clicked option
            when (selectedOption) {
                "A" -> aIsSelected = true
                "B" -> bIsSelected = true
                "C" -> cIsSelected = true
                "D" -> dIsSelected = true
                "E" -> eIsSelected = true


            }
        }
    private fun getOptionText(index: Int): String {
        // Mapping index to option letter (A, B, C, D, E)
        return when (index) {
            0 -> "A"
            1 -> "B"
            2 -> "C"
            3 -> "D"
            4 -> "E"
            else -> ""
        }
    }
    private fun updateImageVisibility(selectedOption: String) {

        val optionCardIds = listOf(
            R.id.OptionA, R.id.OptionB, R.id.OptionC, R.id.OptionD_voting, R.id.OptionE_voting
        )

        optionCardIds.forEach { optionCardId ->
            val optionText = getOptionText(optionCardIds.indexOf(optionCardId))
            val imageId = getImageViewId(optionCardId)

            if (optionText == selectedOption) {
                // Show filled image for the selected option
                findViewById<ImageView>(imageId).setImageResource(getFilledImageResource(optionText))
            } else {
                // Show hollow image for other options
                findViewById<ImageView>(imageId).setImageResource(getHollowImageResource(optionText))
            }
        }

    }

    private fun getHollowImageResource(option: String): Int {
        return when (option) {
            "A" -> R.drawable.aletterblackhollow
            "B" -> R.drawable.bletterblackhollow
            "C" -> R.drawable.cletterblackhollow
            "D" -> R.drawable.dletterblackhollow
            "E" -> R.drawable.eletterblackhollow
            else -> 0
        }

    }

    private fun getFilledImageResource(option: String): Int {
        return when (option) {
            "A" -> R.drawable.aletterblackfilled
            "B" -> R.drawable.bletterblackfilled
            "C" -> R.drawable.cletterblackfilled
            "D" -> R.drawable.dletterblackfilled
            "E" -> R.drawable.eletterblackfilled
            else -> 0
        }
    }


    private fun updateImages() {
        val optionCardIds = listOf(
            R.id.OptionA, R.id.OptionB, R.id.OptionC, R.id.OptionD_voting, R.id.OptionE_voting
        )

        optionCardIds.forEach { optionCardId ->
            val optionText = getOptionText(optionCardIds.indexOf(optionCardId))
            updateImage(optionCardId, optionText)
        }
    }

    private fun updateImage(optionCardId: Int, selectedOption: String) {
        val imageName = getImageName(selectedOption)
        val drawableId = resources.getIdentifier(imageName, "drawable", packageName)

        findViewById<ImageView>(getImageViewId(optionCardId)).setImageResource(drawableId)
    }

    private fun getImageViewId(optionCardId: Int): Int {
        return when (optionCardId) {
            R.id.OptionA -> R.id.optionImageA_votingActivity
            R.id.OptionB -> R.id.optionImageB
            R.id.OptionC -> R.id.optionImageC
            R.id.OptionD_voting -> R.id.optionImageD
            R.id.OptionE_voting -> R.id.optionImageE
            else -> 0
        }
    }

// Rest of your code remains unchanged

    private fun updateImage(selectedOption: String) {

        val imageId = when (selectedOption) {
            "A" -> R.id.optionImageA_votingActivity
            "B" -> R.id.optionImageB
            "C" -> R.id.optionImageC
            "D" -> R.id.optionImageD
            "E" -> R.id.optionImageE
            else -> 0
        }

        val imageName = getImageName(selectedOption)
        val drawableId = resources.getIdentifier(imageName, "drawable", packageName)

        findViewById<ImageView>(imageId).setImageResource(drawableId)
        findViewById<ImageView>(imageId).visibility = View.VISIBLE
    }


    private fun getImageName(selectedOption: String): String {
        // If the same option is selected again, toggle between filled and hollow images
        if (selectedOption == lastSelected) {
            when (selectedOption) {
                "A" -> {
                    return if (aIsSelected) "aletterblackhollow" else "aletterblackfilled"
                }
                "B" -> {
                    return if (bIsSelected) "bletterblackhollow" else "bletterblackfilled"
                }
                "C" -> {
                    return if (cIsSelected) "cletterblackhollow" else "cletterblackfilled"
                }
                "D" -> {
                    return if (dIsSelected) "dletterblackhollow" else "dletterblackfilled"
                }
                "E" -> {
                    return if (eIsSelected) "eletterblackhollow" else "eletterblackfilled"
                }
                else -> return ""
            }
        }

        // Set the selected option to show filled image for the first selection
        when (selectedOption) {
            "A" -> {
                lastSelected = "A"
                return if (aIsSelected) "aletterblackfilled" else "aletterblackhollow"

            }
            "B" -> {
                lastSelected = "B"
                return if (bIsSelected) "bletterblackfilled" else "bletterblackhollow"
            }
            "C" -> {
                lastSelected = "C"
                return if (cIsSelected) "cletterblackfilled" else "cletterblackhollow"
            }
            "D" -> {
                lastSelected = "D"
                return if (dIsSelected) "dletterblackfilled" else "dletterblackhollow"
            }
            "E" -> {
                lastSelected = "E"
                return if (eIsSelected) "eletterblackfilled" else "eletterblackhollow"
            }
            else -> return ""
        }

    }


    inner class FetchVotingQuestionsTask : AsyncTask<Int, Void, voting>() {
        override fun doInBackground(vararg params: Int?): voting {
            try {
                val topicId = params[0]

                val url = "https://www.telemedocket.com/BNA/public/getquestions?tid=$topicId"
                Log.d(ConstanstsApp.tag, "voting questions url => $url")
                val jsonString = URL(url).readText()

                return parseJson(jsonString)
            } catch (e: Exception) {
                Log.e(ConstanstsApp.tag, "Error fetching voting questions: ${e.message}", e)
                return voting(emptyList(), 0)
            }
        }

        @SuppressLint("SuspiciousIndentation")
        override fun onPostExecute(result: voting?) {
            result?.let {
                if (it.success == 1) {
                    if (it.data.isNotEmpty()) {
                        val question = it.data[0].question
                        val options = listOf(
                            it.data[0].answer1,
                            it.data[0].answer2,
                            it.data[0].answer3,
                            it.data[0].answer4,
                            it.data[0].answer5
                        )

                        // Populate question
                        findViewById<TextView>(R.id.questionTextView).text = "Q: $question"

                        // Handle the visibility of imgVoting based on the image properties in data
                        val imgVoting = findViewById<ImageView>(R.id.imgvoting)
                        val imgrecycler = findViewById<RecyclerView>(R.id.imageRecyclerView)

                        if (
                            it.data[0].image1.isNotEmpty() ||
                            it.data[0].image2.isNotEmpty() ||
                            it.data[0].image3.isNotEmpty() ||
                            it.data[0].image4.isNotEmpty() ||
                            it.data[0].image5.isNotEmpty()

                        ) {
                            // There is at least one non-empty image property, make imgvoting visible
                            imgVoting.visibility = View.VISIBLE
                                //  imgrecycler.visibility = View.VISIBLE
                        } else {
                            // All image properties are empty, hide imgvoting
                            imgVoting.visibility = View.GONE
                          //  imgrecycler.visibility = View.VISIBLE

                        }


                        imgVoting.setOnClickListener {
                            val questionData = votingDataList!![0] // Assuming 'it' refers to the data object
                            val imageList = listOf(
                                questionData.image1,
                                questionData.image2,
                                questionData.image3,
                                questionData.image4,
                                questionData.image5
                            ).filter { it.isNotEmpty() }

                            if (imageList.isNotEmpty()) {
                               // showImageBottomSheet(imageList)
                                // Show the dialog
                                // Show the dialog
                                val dialog = ImageViewerDialog(imageList)
                                dialog.show(supportFragmentManager, "ImageViewerDialog")
                            }
                        }

                        // Assuming 'it.data' is your JSON data object
                        val videoFilename = it.data[0].video // Assuming it's the first item in the data array

// Update the visibility of videoVoting based on whether the data contains a non-empty video property
                        val videoVoting = findViewById<ImageView>(R.id.video_voting)
                        val videoVisibility = if (videoFilename.isNotEmpty()) View.VISIBLE else View.GONE
                        videoVoting.visibility = videoVisibility

                        videoVoting.setOnClickListener {
                            if (videoFilename.isNotEmpty()) {
                                // Construct the video URL using the base URL and video filename
                                val baseUrl = "https://www.telemedocket.com/BNA/public/uploads/voting/video/"
                                val videoUrl = "$baseUrl$videoFilename"

                                // Log the constructed video URL
                                Log.d("VotingActivity", "Video Path: $videoUrl")

                                // Start the Voting_videoview activity when the ImageView is clicked
                                val intent = Intent(this@VotingActivity, Voting_videoview::class.java)
                                intent.putExtra("videoUrl", videoUrl)
                                startActivity(intent)
                            } else {
                                // Handle the case where the video filename is empty (optional)
                                // You can show a message, log an error, or handle it based on your requirements
                            }
                        }


                        // Populate options
                        val optionIds = listOf(
                            R.id.optionTextA,
                            R.id.optionTextB,
                            R.id.optionTextC,
                            R.id.optionTextD,
                            R.id.optionTextE
                        )
                        options.forEachIndexed { index, option ->
                            findViewById<TextView>(optionIds[index]).text = option
                        }
                        // Handle option clicks
                        handleOptionClick(optionIds)
                        // Set the visibility of the submitButton based on your conditions
                        if (it.data.isNotEmpty()) {
                            submitButton.visibility = View.VISIBLE
                        } else {
                            submitButton.visibility = View.GONE
                        }
                        // Set the visibility of "Currently No Active Question Available" text to GONE
                        findViewById<TextView>(R.id.no_question_text_view).visibility = View.GONE


                    } else {
                        // Handle the case when there are no questions
                        Log.w(ConstanstsApp.tag, "No voting questions available.")
                        // Set the visibility of "Currently No Active Question Available" text to VISIBLE
                        findViewById<TextView>(R.id.no_question_text_view).visibility = View.VISIBLE
                        // You may want to show a message to the user
                    }
                } else {
                    // Handle the case when API response indicates failure
                    Log.w(ConstanstsApp.tag, "API response indicates failure.")
                    // Set the visibility of "Currently No Active Question Available" text to VISIBLE
                    findViewById<TextView>(R.id.no_question_text_view).visibility = View.VISIBLE
                    // You may want to show a message to the user
                }
            } ?: run {
                // Handle the case when result is null
                Log.e(ConstanstsApp.tag, "Null response received from API.")
                // You may want to show a message to the user
                // Set the visibility of "Currently No Active Question Available" text to VISIBLE
                findViewById<TextView>(R.id.no_question_text_view).visibility = View.VISIBLE

            }
        }

        private fun parseJson(jsonString: String): voting {
            val jsonObject = JSONObject(jsonString)

            return if (jsonObject.has("success") && jsonObject.has("data")) {
                val success = jsonObject.getInt("success")
                val jsonArray = jsonObject.getJSONArray("data")

                votingDataList = mutableListOf<Data>()
                var data: Data? = null

                for (i in 0 until jsonArray.length()) {
                    val votingDataJson = jsonArray.getJSONObject(i)

                    val data = Data(
                        votingDataJson.getString("answer1"),
                        votingDataJson.getString("answer2"),
                        votingDataJson.getString("answer3"),
                        votingDataJson.getString("answer4"),
                        votingDataJson.getString("answer5"),
                        votingDataJson.getString("cd_start_time"),
                        votingDataJson.getInt("cd_stop"),
                        votingDataJson.getString("correct_answer"),
                        votingDataJson.getString("create_date"),
                        votingDataJson.getInt("event_id"),
                        votingDataJson.getString("image1"),
                        votingDataJson.getString("image2"),
                        votingDataJson.getString("image3"),
                        votingDataJson.getString("image4"),
                        votingDataJson.getString("image5"),
                        votingDataJson.getInt("is_active"),
                        votingDataJson.getInt("is_cd_started"),
                        votingDataJson.getInt("is_dummy"),
                        votingDataJson.getInt("is_voted"),
                        votingDataJson.getString("modify_date"),
                        votingDataJson.getString("question"),
                        votingDataJson.getInt("question_id"),
                        votingDataJson.getInt("question_timing"),
                        votingDataJson.getString("question_type"),
                        votingDataJson.getInt("session_id"),
                        votingDataJson.getInt("topic_id"),
                        votingDataJson.getString("video")
                    )
                    val question = data.question
                    Log.d(ConstanstsApp.tag,"question=>"+question)
                    val option1= data.answer1
                    val option2= data.answer2
                    val option3= data.answer3
                    val option4= data.answer4
                    val option5= data.answer5
                    Log.d(ConstanstsApp.tag,"option1=>"+option1)
                    Log.d(ConstanstsApp.tag,"option2=>"+option2)
                    Log.d(ConstanstsApp.tag,"option3=>"+option3)
                    Log.d(ConstanstsApp.tag,"option4=>"+option4)
                    Log.d(ConstanstsApp.tag,"option5=>"+option5)
                    val questiontext=findViewById<TextView>(R.id.questionTextView)

                    val cardViewOption1=findViewById<CardView>(R.id.OptionA)
                    val cardViewOption2=findViewById<CardView>(R.id.OptionB)
                    val cardViewOption3=findViewById<CardView>(R.id.OptionC)
                    val cardViewOption4=findViewById<CardView>(R.id.OptionD_voting)
                    val cardViewOption5=findViewById<CardView>(R.id.OptionE_voting)

                    runOnUiThread {
                        questiontext?.let {
                            if (question != null && !question.equals("null") && !question.isEmpty()) {
                                it.visibility = View.VISIBLE
                            } else {
                                it.visibility = View.GONE
                            }
                        }
                        cardViewOption1?.let {
                            if (option1 != null && !option1.equals("null") && !option1.isEmpty()) {
                                it.visibility = View.VISIBLE
                            } else {
                                it.visibility = View.GONE
                            }
                        }
                        cardViewOption2?.let {
                            if (option2 != null && !option2.equals("null") && !option2.isEmpty()) {
                                it.visibility = View.VISIBLE
                            } else {
                                it.visibility = View.GONE
                            }
                        }
                        cardViewOption3?.let {
                            if (option3 != null && !option3.equals("null") && !option3.isEmpty()) {
                                it.visibility = View.VISIBLE
                            } else {
                                it.visibility = View.GONE
                            }
                        }

                        cardViewOption4?.let {
                            if (option4 != null && !option4.equals("null") && !option4.isEmpty()) {
                                it.visibility = View.VISIBLE
                            } else {
                                it.visibility = View.GONE
                            }
                        }
                        cardViewOption5?.let {
                            if (option5 != null && !option5.equals("null") && !option5.isEmpty()) {
                                it.visibility = View.VISIBLE
                            } else {
                                it.visibility = View.GONE
                            }
                        }

                    }

                    (votingDataList as MutableList<Data>).add(data)
                }

                voting(votingDataList as MutableList<Data>, success)


            } else {
                Log.e(ConstanstsApp.tag, "JSON response missing required fields.")
                voting(emptyList(), 0)
            }
        }
    }


    inner class SubmitAnswerTask : AsyncTask<String?, Void, SubmitResponse>() {
        @SuppressLint("SuspiciousIndentation")
        override fun doInBackground(vararg params: String?): SubmitResponse {
            try {
                val selectedOption = params[0]
                val userId = params[1]
                val id = params[2]
                val eventId = params[3]
                val sessionId = params[4]
                val questionId = params[5]
                val question = params[6]
                val isDummy = params[7]
                val cdStartTime = params[8]
                val correctAnswer = params[9]

                // Construct the URL for the API endpoint
                val apiUrl =
                    "https://www.telemedocket.com/BNA/public/submitanswer" +
                            "?event_id=$eventId" +
                            "&session_id=$sessionId" +
                            "&topic_id=$id" +
                            "&question_id=$questionId" +
                            "&user_id=$userId" +
                            "&cd_start_time=$cdStartTime" +
                            "&is_correct=$correctAnswer" +if (correct_answer == "1") "1" else "0"+
                "&answer=$selectedOption" +
                            "&is_dummy=$isDummy"+
                            "&question=$question"


                            Log.d(ConstanstsApp.tag,"apiUrl=>"+apiUrl)

                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                // Get the response code
                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response from the input stream
                    val inputStream = connection.inputStream
                    val jsonString = inputStream.bufferedReader().use { it.readText() }

                    // Parse the JSON response
                    return parseJson(jsonString)
                } else {
                    // Handle the case when the server returns an error
                    Log.e(ConstanstsApp.tag, "API request failed with code $responseCode")
                }

                // Close the connection
                connection.disconnect()
            } catch (e: Exception) {
                // Handle exceptions that may occur during the network request
                Log.e(ConstanstsApp.tag, "API request failed: ${e.message}", e)
            }

            // Return a default SubmitResponse in case of failure
            return SubmitResponse(message = 0, status_code = 0)
        }

        override fun onPostExecute(result: SubmitResponse?) {
            // Handle the result after the background operation completes
            result?.let {
                // Process the submitResponse as needed
                handleSubmitResponse(it)
            }
        }

        private fun parseJson(jsonString: String): SubmitResponse {
            // Basic implementation for parsing JSON
            try {
                val jsonObject = JSONObject(jsonString)

                if (jsonObject.has("status_code")) {
                    val statusCode = jsonObject.getInt("status_code")
                    val message = jsonObject.optString("message", "")

                    // Process the message based on its content
                    when (message) {
                        "Timer has not started yet!" -> {

                            //  showAlert("Timer has not started yet!")
                            runOnUiThread {
                                showAlert("Voting not yet started for this question", "OK", { _, _ ->
                                    // Handle the user's selection to go back
                                    onBackPressed()
                                }, "Back") { _, _ ->
                                    // Handle the user's selection to go back
                                    onBackPressed()
                                }

//                                val alertController = AlertDialog.Builder(this@VotingActivity)
//                                    .setTitle("")
//                                    .setMessage("Voting not yet started for this question")
//                                    .setPositiveButton("OK") { _, _ ->
//
//                                        // Handle the user's selection (if needed)
//                                        println("OK button tapped")
//                                    }
//
//                                    .create()
//                                // Set a custom style for the positive button
//                                alertController.setOnShowListener { dialog ->
//                                    val positiveButton =
//                                        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
//                                    positiveButton.setTextColor(resources.getColor(R.color.white1)) // Customize color as needed
//                                    // You can also set other properties or a custom background drawable here if needed
//                                }
//                                alertController.show()
                            }
                        }

                        "success" -> {
                            runOnUiThread {
                                if (selectedOption == null) {
                                    // Scenario 1: User did not select an answer
                                    showAlert(
                                        "Please select an answer before submitting.",
                                        "OK",
                                        { _, _ ->
                                            // Handle the user's selection to go back
                                            onBackPressed()
                                        },
                                        "Back"
                                    ) { _, _ ->
                                        // Handle the user's selection to go back
                                        onBackPressed()
                                    }
                                } else {
                                    // Scenario 2: User has selected an answer, proceed with submission
                                    submitAnswer(correct_answer)

                                    // Show a custom alert for successful submission
                                    showAlert("Your Vote has been Submitted!", "OK", { _, _ ->
                                        // Handle the user's selection to go back
                                        onBackPressed()
                                    }, "Back") { _, _ ->
                                        // Handle the user's selection to go back
                                        onBackPressed()
                                    }

                                    // If needed, you can perform additional actions or navigate back
                                    // based on the user's selection in the alert
                                }
                            }
                        }

                        "You have already submitted!" -> {
                            //  showAlert("You have already submitted the answer")
                            runOnUiThread {
                                showAlert("You have already submitted the answer", "OK", { _, _ ->
                                    // Handle the user's selection to go back
                                    onBackPressed()
                                }, "Back") { _, _ ->
                                    // Handle the user's selection to go back
                                    onBackPressed()
                                }

//                                val alertController = AlertDialog.Builder(this@VotingActivity)
//                                    .setTitle("")
//                                    .setMessage("You have already submitted the answer")
//                                    .setPositiveButton("OK") { _, _ ->
//                                        // Handle the user's selection (if needed)
//                                        // e.g., self.navigationController?.popViewController(animated: true)
//                                        // self.dismiss(animated: true, completion: nil)
//                                        submitButton.isEnabled = false
//                                        println("OK button tapped")
//                                    }
//                                    .create()
//                                // Set a custom style for the positive button
//                                alertController.setOnShowListener { dialog ->
//                                    val positiveButton =
//                                        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
//                                    positiveButton.setTextColor(resources.getColor(R.color.white1)) // Customize color as needed
//                                    // You can also set other properties or a custom background drawable here if needed
//                                }
//                                alertController.show()
                            }
                        }

                        "Time's up!" -> {
                            runOnUiThread {
                                if (isTimeUp) {
                                    // Scenario 1: Time is up, show the Time's up dialog
                                    showAlert("Time's up for this Question", "OK", { _, _ ->
                                        // Handle the user's selection to go back
                                        onBackPressed()
                                    }, "Back") { _, _ ->
                                        // Handle the user's selection to go back
                                        onBackPressed()
                                    }
                                } else {
                                    // Scenario 2: Time is up, but the user interacts with options or submit
                                    showAlert("Time's up for this Question", "OK", { _, _ ->
                                        // Handle the user's selection to go back
                                        onBackPressed()
                                    }, "Back") { _, _ ->
                                        // Handle the user's selection to go back
                                        onBackPressed()
                                    }
                                }
                            }

                    }

                    }
                        // Create a SubmitResponse object with the parsed status code value
                    return SubmitResponse(message = 0, status_code = statusCode)
                } else {
                    Log.e(ConstanstsApp.tag, "JSON response missing 'status_code' field.")
                }
            } catch (e: Exception) {
                Log.e(ConstanstsApp.tag, "Error parsing JSON: ${e.message}", e)
            }

            // Return a default SubmitResponse in case of parsing failure
            return SubmitResponse(message = 0, status_code = 0)
        }
    }

    private fun showAlert(
        message: String,
        s: String,
        param: (Any, Any) -> Unit,
        s1: String,
        param1: (Any, Any) -> Unit
    ) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.login_alert)

        // Find views in the custom dialog layout
        val notificationTitle: TextView = dialog.findViewById(R.id.notificationTitle)
        val textViewLogoutConfirmation: TextView = dialog.findViewById(R.id.textViewLogoutConfirmation)
        val buttonYes: Button = dialog.findViewById(R.id.buttonYes)
        val buttonNo: Button = dialog.findViewById(R.id.buttonNo)

        // Set notification title and confirmation text
        notificationTitle.text = "Alert"
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

    private fun showTimeUpDialog() {
        val alertController = AlertDialog.Builder(this@VotingActivity)
            .setTitle("Sorry")
            .setMessage("Time's up for this Question")
            .setPositiveButton("OK") { _, _ ->
                // Handle the user's selection to go back or perform other actions
                onBackPressed()
            }
            .setNegativeButton("Back") { _, _ ->
                // Handle the user's selection to go back
                onBackPressed()
            }
            .create()

        // Set a custom style for the positive button
        alertController.setOnShowListener { dialog ->
            val positiveButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(resources.getColor(R.color.white1)) // Customize color as needed
            // You can also set other properties or a custom background drawable here if needed
        }

        // Set a custom style for the negative button
        alertController.setOnShowListener { dialog ->
            val negativeButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setTextColor(resources.getColor(R.color.white1)) // Customize color as needed
            // You can also set other properties or a custom background drawable here if needed
        }

        alertController.show()

    }

    private fun handleSubmitResponse(submitResponse: SubmitResponse) {
        // Handle the response as needed
        // For example, show a message to the user based on the status code value
        if (submitResponse.status_code == 1) {
            // Successful submission
            Log.d(ConstanstsApp.tag, "Answer submitted successfully.")
            // Update the background color based on correctness
            val isCorrect = correct_answer.equals(selectedOption)
            updateOptionBackgroundColor(selectedOptionId, isCorrect)
        } else {
            // Submission failed
            Log.e(ConstanstsApp.tag, "Answer submission failed.")
        }
    }

    private fun updateOptionBackgroundColor(optionId: Int, isCorrect: Boolean) {
        val cardViewId = when (optionId) {
            R.id.optionTextA -> R.id.OptionA
            R.id.optionTextB -> R.id.OptionB
            R.id.optionTextC -> R.id.OptionC
            R.id.optionTextD -> R.id.OptionD_voting
            R.id.optionTextE -> R.id.OptionE_voting
            else -> 0
        }

        val cardView = findViewById<CardView>(cardViewId)
        val colorResId = if (isCorrect) R.color.green else R.color.red // Change color based on correctness

        // Set the background color of the card view
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, colorResId))
    }

    private fun resetOption(optionId: Int) {
        // Reset the image visibility (you can customize this based on your UI)
        val imageId = when (optionId) {
            R.id.optionTextA -> R.id.optionImageA
            R.id.optionTextB -> R.id.optionImageB
            R.id.optionTextC -> R.id.optionImageC
            R.id.optionTextD -> R.id.optionImageD
            R.id.optionTextE -> R.id.optionImageE
            else -> 0
        }
        findViewById<ImageView>(imageId).visibility = View.INVISIBLE
    }

    override fun onClick(v: View?) {

        var answer_flag=0

        when(v)
        {
            submitButton->
            {



                Log.d(ConstanstsApp.tag, "selected answer=" + selectedOption)
                Log.d(ConstanstsApp.tag, "userId" + userId)

                for (i in 0 until votingDataList!!.size) {
                    val data = votingDataList!![i]

                    id = data.event_id.toString()
                    event_id = data.event_id.toString()
                    session_id = data.session_id.toString()
                    question_id = data.question_id.toString()
                    question = data.question
                    is_dummy = data.is_dummy.toString()
                    cd_start_time = data.cd_start_time.toString()
                    correct_answer = data.correct_answer

                    Log.d(ConstanstsApp.tag,"correct_answer=>"+correct_answer)

                }




                if (correct_answer.equals(selectedOption))
                {
                    answer_flag=1
                }
                else
                {
                    answer_flag=0
                }

                // Move the network operation to a background thread
                SubmitAnswerTask().execute(
                    selectedOption,
                    userId.toString(),
                    id,
                    event_id,
                    session_id,
                    question_id,
                    question,
                    is_dummy,
                    cd_start_time,
                    answer_flag.toString()
                )
            }
        }

    }

    @SuppressLint("ResourceAsColor")
    private fun submitAnswer(correctAnswer: String?) {

        Log.d(ConstanstsApp.tag,"correct_answer1=>"+correctAnswer)

        if (correctAnswer.equals(selectedOption))
        {
            Log.d(ConstanstsApp.tag,"correct answer")
        }
        else
        {

            Log.d(ConstanstsApp.tag,"wrong answer")
            when(selectedOption)
            {
                "A"->
                {
                    cardViewA.setBackgroundColor(ContextCompat.getColor(this, R.color.red))

                }
                "B"->
                {
                    cardViewB.setBackgroundColor(ContextCompat.getColor(this, R.color.red))

                }
                "C"->
                {
                    cardViewC.setBackgroundColor(ContextCompat.getColor(this, R.color.red))

                }
                "D"->
                {
                    cardViewD.setBackgroundColor(ContextCompat.getColor(this, R.color.red))

                }
                "E"->
                {
                    cardViewE.setBackgroundColor(ContextCompat.getColor(this, R.color.red))

                }
            }

            when(correctAnswer)
            {
                "A"->
                {
                    cardViewA.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                }
                "B"->
                {
                    cardViewB.setBackgroundColor(ContextCompat.getColor(this, R.color.green))

                }
                "C"->
                {
                    cardViewC.setBackgroundColor(ContextCompat.getColor(this, R.color.green))

                }
                "D"->
                {
                    cardViewD.setBackgroundColor(ContextCompat.getColor(this, R.color.green))

                }
                "E"->
                {
                    cardViewE.setBackgroundColor(ContextCompat.getColor(this, R.color.green))

                }
            }
        }
    }
}