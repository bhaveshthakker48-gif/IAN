package org.bombayneurosciences.bna_2023.Activity

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager

import org.bombayneurosciences.bna_2023.Data.SharedPreferencesActivity
import org.bombayneurosciences.bna_2023.Model.SendComment.SendResponse
import org.bombayneurosciences.bna_2023.Model.chats.receivecomments.Data
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.adapter.ReplyAdapter
import org.bombayneurosciences.bna_2023.databinding.ActivityJournalBinding
import org.bombayneurosciences.bna_2023.databinding.ActivityRepliesBinding
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class RepliesActivity : AppCompatActivity() {

    private lateinit var replyAdapter: ReplyAdapter
    private lateinit var caseId: String // Added to store caseId
    private var commentId: Int = -1
    var delegateId: String? = null
    private var isArchived: Boolean = false
    lateinit var binding:ActivityRepliesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRepliesBinding.inflate(layoutInflater)
        setContentView(binding.root)


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


        val sendButton: ImageView = findViewById(R.id.sendButton)
        val editTextChats: EditText = findViewById(R.id.editTextChats)
        val arrowImageView: ImageView = findViewById(R.id.arrowImageView)

        val commenttext :TextView = findViewById(R.id.comment)
         isArchived = intent.getBooleanExtra("isArchived", false)

        // Retrieve caseId from the intent
        caseId = intent.getStringExtra("case_id") ?: ""
        val intent = intent
        if (intent != null && intent.hasExtra("comment")) {
            val comment = intent.getStringExtra("comment")
            Log.d(ConstanstsApp.tag, "Received comment in RepliesActivity:"+comment)


            commenttext.text = comment
        }

        if (isArchived) {
            editTextChats.visibility = View.GONE
            sendButton.visibility = View.GONE
        } else {
            editTextChats.visibility = View.VISIBLE
            sendButton.visibility = View.VISIBLE
        }
        if (intent != null && intent.hasExtra("id")) {
            commentId = intent.getIntExtra("id", -1)

            // Now you have the commentId, and you can use it as needed
            Log.d(ConstanstsApp.tag, "Received commentId in RepliesActivity:"+commentId)
        }
        delegateId = SharedPreferencesActivity.getDelegateId(this).toString()
        Log.d(ConstanstsApp.tag, "delegate ID for Chat: $delegateId")

        sendButton.setOnClickListener {
            Log.d(ConstanstsApp.tag, "send button click ")
            Log.d(ConstanstsApp.tag, "send button click "+editTextChats.text.toString())

            val    delegateId = SharedPreferencesActivity.getDelegateId(this).toString()
            Log.d(ConstanstsApp.tag, "delegate ID for sendButton: $delegateId")
            Log.d(ConstanstsApp.tag, "case ID for sendButton: $caseId")
            sendReply(editTextChats.text.toString())
        }
        arrowImageView.setOnClickListener {
            // Launch the ChatActivity
           finish()
        }
        // Use the caseId as needed
        if (caseId.isNotEmpty()) {
            Log.d(ConstanstsApp.tag, "Received caseId in RepliesActivity: $caseId")
            // Perform actions with the caseId
            FetchRecommentsTask().execute(caseId)
        } else {
            Log.e(ConstanstsApp.tag, "No caseId received in RepliesActivity")
        }
    }

    private fun sendReply(replyText: String) {
        if (caseId.isNotEmpty() && replyText.isNotEmpty()) {
            SendReplyTask().execute(caseId, replyText, delegateId)
        } else {
            Log.e(ConstanstsApp.tag, "Invalid caseId or empty reply text")
        }
    }

    inner class SendReplyTask : AsyncTask<String, Void, Boolean>() {
        override fun doInBackground(vararg params: String?): Boolean {
            val caseId = params[0]
            val replyText = params[1]
            val delegateId = params[2]

            try {
                val apiUrl =
                    "https://www.telemedocket.com/BNA/public/Addcomments" +
                            "?case_id=$caseId" +
                            "&comment=$replyText" +
                            "&user=$delegateId" +
                            "&user_id=$delegateId" +
                            "&is_replied=1" +
                            "&is_repID=$commentId"

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
                    val result = parseJson(jsonString)
                    return result.success == 1
                } else {
                    // Handle the case when the server returns an error
                    Log.e(ConstanstsApp.tag, "API request failed with code $responseCode")
                }
            } catch (e: Exception) {
                // Handle exceptions that may occur during the network request
                Log.e(ConstanstsApp.tag, "API request failed: ${e.message}", e)
            }

            // Return false in case of failure
            return false
        }

        override fun onPostExecute(result: Boolean) {
            if (result) {
                Log.d(ConstanstsApp.tag, "Reply sent successfully")
                // Optionally, refresh the list of replies
                FetchRecommentsTask().execute(caseId)

                binding.editTextChats.setText(null)
            } else {
                Log.e(ConstanstsApp.tag, "Failed to send reply")
            }
        }
    }
    private fun parseJson(jsonString: String): SendResponse {
        val json = JSONObject(jsonString)
        val message = json.optString("message", "Default Message")
        val success = json.optInt("success", 0)
        return SendResponse(message, success)
    }


    private fun setupRecyclerView(repliesList: List<Data>) {
        Log.d(ConstanstsApp.tag, "Setting up RecyclerView with data: $repliesList")
        replyAdapter = ReplyAdapter(repliesList, delegateId!!)


        binding.recyclerviewReplychats.layoutManager = LinearLayoutManager(this)
        binding.recyclerviewReplychats.adapter = replyAdapter

        val itemDecoration = DashLineItemDecoration(this)
        binding.recyclerviewReplychats.addItemDecoration(itemDecoration)

        replyAdapter.notifyDataSetChanged()
    }


    inner class FetchRecommentsTask : AsyncTask<String, Void, List<Data>>() {
        override fun doInBackground(vararg params: String?): List<Data> {
            val caseId = params[0]
            val url = "https://www.telemedocket.com/BNA/public/getrecomments?case_id=$caseId"
            val jsonString = URL(url).readText()
            return parseJsonRecomments(jsonString)
        }

        override fun onPostExecute(result: List<Data>?) {
            if (result != null && result.isNotEmpty()) {
                // Handle the list of recomments
                Log.d(ConstanstsApp.tag, "Recomments: $result")
                setupRecyclerView(result)
                replyAdapter.notifyDataSetChanged()

                binding.noReplyTextView.visibility = View.GONE
            } else {
                Log.d(ConstanstsApp.tag, "No recomments found")
                binding.noReplyTextView.visibility = View.VISIBLE

            }
        }

        private fun parseJsonRecomments(jsonString: String): List<Data> {
            val recomments = mutableListOf<Data>()
            try {
                val jsonResponse = JSONObject(jsonString)
                if (jsonResponse.has("data")) {
                    val dataArray = jsonResponse.getJSONArray("data")
                    for (i in 0 until dataArray.length()) {
                        val recomment = dataArray.getJSONObject(i)

                        // Check if reTocommid is the same as the commentId
                        val currentReTocommid = recomment.optInt("reTocommid")
                        if (currentReTocommid == commentId) {
                            val data = Data(
                                recomment.getInt("case_id"),
                                recomment.optString("commFrom"),
                                recomment.optInt("commFromid"),
                                recomment.optString("commFromname"),
                                recomment.optString("comment"),
                                recomment.optString("created_at"),
                                recomment.optInt("id"),
                                recomment.optInt("is_replied"),
                                recomment.optInt("reTocommid"),
                                recomment.optInt("represent")
                            )
                            recomments.add(data)
                        }
                    }
                } else {
                    Log.e(ConstanstsApp.tag, "No 'data' key found in the JSON response")
                }

            } catch (e: JSONException) {
                Log.e(ConstanstsApp.tag, "Error parsing JSON", e)
            }
            return recomments
        }
    }

    }
