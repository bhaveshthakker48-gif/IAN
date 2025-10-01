package org.bombayneurosciences.bna_2023.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import org.bombayneurosciences.bna_2023.Data.SharedPreferencesActivity
import org.bombayneurosciences.bna_2023.Model.SendComment.Commentsend
import org.bombayneurosciences.bna_2023.Model.SendComment.SendResponse
import org.bombayneurosciences.bna_2023.Model.SendComment.memberName
import org.bombayneurosciences.bna_2023.Model.chats.Data
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.adapter.ChatsAdapter
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


class chatsActivity : AppCompatActivity() {

    private lateinit var chatsRecyclerView: RecyclerView
    private lateinit var chatsAdapter: ChatsAdapter
    private lateinit var SharedPreferences: SharedPreferencesActivity
    private lateinit var editTextChats: EditText
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    var delegateId: String? = null
    var caseId: String? = null
    private var isArchived: Boolean = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

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


        val coordinatorLayout: CoordinatorLayout = findViewById(R.id.coordinatorLayout)
        val bottomSheet: View = coordinatorLayout.findViewById(R.id.bottomSheetLayout)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HALF_EXPANDED

        chatsRecyclerView = findViewById(R.id.recyclerviewChats)

        val sendButton: ImageView = findViewById(R.id.sendButton)
        editTextChats = findViewById(R.id.editTextChats)
        val arroeimage: ImageView = findViewById(R.id.arrowImageView)
        // Set a callback to listen to bottom sheet state changes
        bottomSheetBehavior!!.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Handle state changes here
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // Handle when the bottom sheet is collapsed
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // Handle when the bottom sheet is fully expanded
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        // Handle when the bottom sheet is in a half-expanded state
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Handle sliding behavior if needed
            }
        })

        arroeimage.setOnClickListener {

            finish()
        }

        sendButton.setOnClickListener {
            Log.d(ConstanstsApp.tag, "send button click ")
            sendComment()
        }

        delegateId = SharedPreferencesActivity.getDelegateId(this).toString()
        Log.d(ConstanstsApp.tag, "delegate ID for Chat: $delegateId")

        chatsAdapter = ChatsAdapter(emptyList(), delegateId!!, isArchived)
        chatsRecyclerView.adapter = chatsAdapter
        chatsRecyclerView.layoutManager = LinearLayoutManager(this)

         caseId = intent.getStringExtra("case_id")
        if (caseId != null) {
            Log.d(ConstanstsApp.tag, "Received caseId in chatsActivity: $caseId")
        } else {
            Log.e(ConstanstsApp.tag, "No caseId received in chatsActivity")
        }

       caseId = intent.getIntExtra("case_id", -1).toString()
     //   Log.d(ConstanstsApp.tag, "Case ID for Chats: $caseId")

        FetchChatsTask().execute(caseId.toString())
    }


    private fun sendComment() {
        val commentText = editTextChats.text.toString()
        Log.d(ConstanstsApp.tag, "Comment Text: $commentText")

        Log.d(ConstanstsApp.tag, "delegateId: $delegateId")
        Log.d(ConstanstsApp.tag, "caseId: $caseId")

        if (!delegateId.isNullOrEmpty() && !caseId.isNullOrEmpty()) {
            val commentsend = Commentsend(
                case_id = caseId!!,
                comment = commentText,
                user = "Member",
                user_id = delegateId!!,
                is_replied = "0",
                is_repID = "0"
            )

            SendCommentTask().execute(commentsend)
        } else {
            Log.e(ConstanstsApp.tag, "Invalid delegateId or caseId")
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }



    inner class SendCommentTask : AsyncTask<Commentsend, Void, SendResponse>() {

        override fun doInBackground(vararg params: Commentsend): SendResponse {
            try {
                val commentData = params[0]

                // Construct the URL for the API endpoint
                val apiUrl = "https://www.telemedocket.com/BNA/public/Addcomments" +
                        "?case_id=${commentData.case_id}" +
                        "&comment=${commentData.comment}" +
                        "&user=${commentData.user}" +
                        "&user_id=${commentData.user_id}" +
                        "&is_replied=${commentData.is_replied}" +
                        "&is_repID=${commentData.is_repID}"

                Log.d(ConstanstsApp.tag, "apiUrl: $apiUrl")

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

            // Return a default SendResponse in case of failure
            return SendResponse(message = "", success = 0)
        }

        private fun parseJson(jsonString: String): SendResponse {
            val json = JSONObject(jsonString)
            val message = json.optString("message", "Default Message")
            val success = json.optInt("success", 0) // Use "success" instead of "status_code"

            return SendResponse(message, success)
        }


        override fun onPostExecute(result: SendResponse?) {
            if (result != null && result.success == 1) {
                Log.d(ConstanstsApp.tag, "Comment sent successfully")
                FetchChatsTask().execute(caseId.toString())
                editTextChats.setText(null)
                // You can perform additional actions based on the response
            } else {
                Log.d(ConstanstsApp.tag, "Failed to send comment")
            }
        }
    }

    inner class FetchChatsTask : AsyncTask<String, Void, List<Data>>() {
        override fun doInBackground(vararg params: String?): List<Data> {
            val caseId = params[0]

            if (isNetworkAvailable()) {
                Log.d(ConstanstsApp.tag, "Fetching chats from the network.")

                val url = "https://www.telemedocket.com/BNA/public/getcomments?case_id=$caseId"
                val jsonString = URL(url).readText()

                return parseJsonChats(jsonString)
            } else {
                Log.d(ConstanstsApp.tag, "No active network. Unable to fetch chats.")
                return emptyList()
            }
        }

        override fun onPostExecute(result: List<Data>?) {
            if (result != null && result.isNotEmpty()) {
                chatsAdapter.updateChats(result)
            } else {
                chatsAdapter.updateChats(emptyList())
            }
        }

        private fun parseJsonChats(jsonString: String): List<Data> {
            val chats = mutableListOf<Data>()

            try {
                val json = JSONObject(jsonString)
                val success = json.getInt("success")

                if (success == 1) {
                    val dataJsonArray = json.getJSONArray("data")

                    for (i in 0 until dataJsonArray.length()) {
                        val chatJson = dataJsonArray.getJSONObject(i)

                        val caseId = chatJson.getInt("case_id")
                        val commFrom = chatJson.getString("commFrom")
                        val commFromId = chatJson.getInt("commFromid")
                        val comment = chatJson.getString("comment")
                        val commFromname = chatJson.getString("commFromname")
                        val created_at = chatJson.getString("created_at")
                        val id = chatJson.getInt("id")
                        val isReplied = chatJson.getString("is_replied")
                        val reToCommid = chatJson.getInt("reTocommid")
                        val represent = chatJson.getInt("represent")
                        val replyCount = chatJson.getInt("replyCount")

                        val chat =
                            Data(caseId, commFrom, commFromId, comment,commFromname, created_at, id, isReplied, reToCommid, represent,replyCount)
                         chats.add(chat)




                    }
                }
            } catch (e: JSONException) {
                Log.e(ConstanstsApp.tag, "Error parsing JSON", e)
            }

            return chats
        }
    }

    private fun fetchmembername(commFromId: Int) {
            MemberNameTask().execute(commFromId.toString())
        }

    inner class MemberNameTask : AsyncTask<String, Void, memberName>() {

        override fun doInBackground(vararg params: String?): memberName {
            try {
                val memberId = params[0]

                val url = "https://www.telemedocket.com/BNA/public/getmemname?memberid=$memberId"
                val jsonString = URL(url).readText()

                return parseJsonMemberName(jsonString)
            } catch (e: Exception) {
                Log.e(ConstanstsApp.tag, "Failed to fetch member name: ${e.message}", e)
            }

            return memberName(data = emptyList(), success = 0)
        }

        private fun parseJsonMemberName(jsonString: String): memberName {
            return Gson().fromJson(jsonString, memberName::class.java)
        }

        override fun onPostExecute(result: memberName?) {
            if (result != null && result.success == 1 && result.data.isNotEmpty()) {
                // Update your UI or store member names as needed
                val memberName = result.data[0].name


                Log.d(ConstanstsApp.tag, "Member Name: $memberName")
            } else {
                Log.d(ConstanstsApp.tag, "Failed to fetch member name")
            }
        }
    }


}





