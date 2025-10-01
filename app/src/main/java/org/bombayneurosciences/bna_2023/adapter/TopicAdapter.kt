package org.bombayneurosciences.bna_2023.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.Activity.LoginActivity
import org.bombayneurosciences.bna_2023.Activity.TopicsActivity
import org.bombayneurosciences.bna_2023.Activity.VotingActivity
import org.bombayneurosciences.bna_2023.CallBack.InterfaceVoting
import org.bombayneurosciences.bna_2023.Model.Topics.Data
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TopicAdapter(
    private val context: Context,

    val isLoggedIn: Boolean,
    val shouldRememberMe: Boolean,
    val sharepreferenceAppkill: String?,
    private val name: String?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() , InterfaceVoting {

    private var topics: List<Data> = emptyList()

    private val LOGIN_REQUEST_CODE = 1001

    lateinit var sessionManager: SessionManager1

    companion object {
        const val VIEW_TYPE_TOPIC = 1
        const val VIEW_TYPE_BREAK = 2
        const val VIEW_TYPE_TOPIC_WITH_BREAK = 3
        const val VIEW_TYPE_SESSION = 4
    }
//    override fun onVotingClick(topicId: Int, name: String) {
//        // Implement the onVotingClick method
//        interfaceVoting.onVotingClick(topicId, name)
//    }

    override fun onVotingClick(topicId: Int, name: String) {
        // Use name here as needed
        Log.d(ConstanstsApp.tag, "name on onVotingClick=> $name")
    }



    fun setTopics(topics: List<Data>) {
        this.topics = topics
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TOPIC -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.topic_recyclerview, parent, false)
                TopicViewHolder(view)
            }
            VIEW_TYPE_BREAK -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_break, parent, false)
                BreakViewHolder(view)
            }
            VIEW_TYPE_TOPIC_WITH_BREAK -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.topic_with_break_recyclerview, parent, false)
                TopicWithBreakViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = topics[position]

        when (holder) {
            is TopicViewHolder -> holder.bindTopic(item)
            is BreakViewHolder -> holder.bindBreak(item)
            is TopicWithBreakViewHolder -> holder.bindTopicWithBreak(item)
        }

       //  Set a click listener for both TopicViewHolder and TopicWithBreakViewHolder
        holder.itemView.setOnClickListener {
            val clickedItem = topics[position]

            if (clickedItem.type == "Topic" && !isEventInPast(clickedItem.end_date) && clickedItem.quecnt.toInt() > 0) {
                //interfaceVoting.onVotingClick(clickedItem.id, clickedItem.title)

                // Open VotingActivity only for future events and when quecnt > 0
                // openVotingActivity(clickedItem)
                handleVectorClick(position)

            } else {
                // Handle click for breaks or past events if needed
            }
        }


    }

    override fun getItemCount(): Int {
        return topics.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            topics[position].type.equals("Topic + Break", ignoreCase = true) -> VIEW_TYPE_TOPIC_WITH_BREAK
            topics[position].type.equals("Topic", ignoreCase = true) -> VIEW_TYPE_TOPIC
            else -> VIEW_TYPE_BREAK
        }
    }

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val dateTextView: TextView = itemView.findViewById(R.id.topicdate)
        private val speakerTextView: TextView = itemView.findViewById(R.id.speaker)
        private val vector: ImageView = itemView.findViewById(R.id.vector)

        fun bindTopic(topic: Data) {
            titleTextView.text = topic.title
            dateTextView.text = "${formatTime24to12(topic.start_time)} - ${formatTime24to12(topic.end_time)}"
//            speakerTextView.text = topic.speaker?.toString() ?: "Speaker not available"

            if (topic.speaker.equals("Null")  || topic.speaker.equals("null")) {
                Log.e("chairpersons3", "chairpersons is null or blank: '${topic.speaker}'")
                speakerTextView.visibility = View.GONE

            } else {

                Log.e("chairpersons4", "chairpersons is null or blank: '${topic.speaker}'")
                speakerTextView.visibility = View.VISIBLE

                val venueText = "Speaker : ${topic.speaker}"
                val spannable = SpannableString(venueText)
                spannable.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            itemView.context,
                            android.R.color.holo_red_dark
                        )
                    ),
                    0, 9, // Index of "Speaker :" including the space after ":"
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                val chairpersonStartIndex = 10 // Index where the actual chairperson name starts
                val chairpersonEndIndex =
                    venueText.length // Index where the actual chairperson name ends
                spannable.setSpan(
                    StyleSpan(Typeface.ITALIC),
                    chairpersonStartIndex,
                    chairpersonEndIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                speakerTextView.text = spannable

            }

            if (ConstanstsApp.isEventPast(topic.end_date)) {
                // If the event has ended, hide the voting ImageView
                vector.visibility = View.GONE
            } else {
                if (topic.quecnt.toInt() == 0) {
                    vector.visibility = View.GONE
                } else {
                    vector.visibility = View.VISIBLE
                }
            }
        }

        init {
            topics.sortedBy { it.start_time }

            // Set a click listener for the vector ImageView
            vector.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedTopic = topics[position]
                    // Open VotingActivity only for future events and when quecnt > 0
                    if (!ConstanstsApp.isEventPast(clickedTopic.end_date) && clickedTopic.quecnt.toInt() > 0) {
                        // openVotingActivity(clickedTopic)


                        handleVectorClick(adapterPosition)

                    }
                }
            }
        }

//        init {
//            // Set a click listener for the vector ImageView
//            vector.setOnClickListener {
//                handleVectorClick(adapterPosition)
//                Log.d("mytag","voting button clicked")
//            }
//        }
    }

    private fun handleVectorClick(position: Int) {

        Log.d("mytag","sharepreferenceAppkill" +sharepreferenceAppkill)
        Log.d("mytag","isLoggedIn"+isLoggedIn)



        when(isLoggedIn){
            true->{

                val intent = Intent(context, VotingActivity::class.java)
                intent.putExtra("topicId",topics[position].id)
                intent.putExtra("eventId",topics[position].event_id)
                intent.putExtra("sessionId",topics[position].session_id)
                intent.putExtra("name", name)
                context.startActivity(intent)

            }
            false->{

                startLoginActivity("Voting",position)

            }
        }


//        if (position != RecyclerView.NO_POSITION) {
//            val clickedTopic = topics[position]
//            if (isOnline() || isLoggedIn) {
//                //val (isLoggedIn, shouldRememberMe) = sharedPreferencesManager1.getUserStatus()
//                if (session1 == true) {
//                    val intent = Intent(context, VotingActivity::class.java)
//                    context.startActivity(intent)
//                    Log.d("mytag","condition1")
//                } else {
//                    if (sharepreferenceAppkill == null || sharepreferenceAppkill.equals("AppKill")) {
//                        val intent = Intent(context, LoginActivity::class.java)
//                        context.startActivity(intent)
//                        Log.d("mytag","condition2")
//
//                    } else {
//                        if (isLoggedIn == true && shouldRememberMe == true) {
//                            val intent = Intent(context, VotingActivity::class.java)
//                            context.startActivity(intent)
//                            Log.d("mytag","condition3")
//
//                        } else if (isLoggedIn == true && shouldRememberMe == false) {
//                            val intent = Intent(context, VotingActivity::class.java)
//                            context.startActivity(intent)
//                            Log.d("mytag","condition4")
//
//                        } else if (isLoggedIn == false && shouldRememberMe == false) {
//                            startLoginActivity("Voting")
//
//
//                            Log.d("mytag","condition5")
//
//                        }
//                    }
//                }
//            } else {
//                // Show "No Internet" alert
//              //  showNoInternetAlert()
//            }
//        }
    }

    fun formatTime24to12(time: String): String? {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
        val date = inputFormat.parse(time)
        return outputFormat.format(date)?.toUpperCase(Locale.getDefault())
    }

    inner class BreakViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.breakname)
        private val timeTextView: TextView = itemView.findViewById(R.id.brektime)

        fun bindBreak(breakData: Data) {
            titleTextView.text = breakData.title
            timeTextView.text = "${formatTime24to12(breakData.start_time)} - ${formatTime24to12(breakData.end_time)}"
        }
    }

    inner class TopicWithBreakViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.title_topicwithbreak)
        private val dateTextView: TextView = itemView.findViewById(R.id.topicdate_topicwithbreak)
        private val speakerTextView: TextView = itemView.findViewById(R.id.speaker)
        private val vector: ImageView = itemView.findViewById(R.id.vector)

        @SuppressLint("SuspiciousIndentation")
        fun bindTopicWithBreak(topic: Data) {
            titleTextView.text = topic.title
            dateTextView.text = "${formatTime24to12(topic.start_time)} - ${formatTime24to12(topic.end_time)}"
//            speakerTextView.text = topic.speaker?.toString() ?: "Speaker not available"

            if (topic.speaker.equals("Null") || topic.speaker.equals("null")) {
                Log.e("chairpersons5", "chairpersons is null or blank: '${topic.speaker}'")

                speakerTextView.visibility = View.GONE

            } else {
                Log.e("chairpersons7", "chairpersons is null or blank: '${topic.speaker}'")

                speakerTextView.visibility = View.VISIBLE

                val venueText = "Speaker : ${topic.speaker}"
                val spannable = SpannableString(venueText)
                spannable.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            itemView.context,
                            android.R.color.holo_red_dark
                        )
                    ),
                    0, 9, // Index of "Speaker :" including the space after ":"
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                val chairpersonStartIndex = 10 // Index where the actual chairperson name starts
                val chairpersonEndIndex =
                    venueText.length // Index where the actual chairperson name ends
                spannable.setSpan(
                    StyleSpan(Typeface.ITALIC),
                    chairpersonStartIndex,
                    chairpersonEndIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                speakerTextView.text = spannable

            }

            if (ConstanstsApp.isEventPast(topic.end_date)) {
                // If the event has ended, hide the voting ImageView
                vector.visibility = View.GONE
            } else {
                if (topic.quecnt.toInt() == 0) {
                    vector.visibility = View.GONE
                } else {
                    vector.visibility = View.VISIBLE
                }
            }
        }

        init {
            // Set a click listener for the vector ImageView
            vector.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedTopic = topics[position]
                    // Open VotingActivity only for future events and when quecnt > 0
                    if (!ConstanstsApp.isEventPast(clickedTopic.end_date) && clickedTopic.quecnt.toInt() > 0) {
                       // openVotingActivity(clickedTopic)
                        handleVectorClick(adapterPosition)
                    }
                }
            }
        }
    }

    private fun openVotingActivity(topic: Data) {
        Log.d(ConstanstsApp.tag, "Opening VotingActivity for topic: ${topic.title}")
        val intent = Intent(context, VotingActivity::class.java)
        intent.putExtra("topicId", topic.id)
        context.startActivity(intent)
    }

    // Function to check if the event is in the past
    private fun isEventInPast(eventDate: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        val eventDateTime = dateFormat.parse(eventDate)
        return currentDate.after(eventDateTime)
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun showNoInternetAlert(context: Context, message: String) {
        AlertDialog.Builder(context)
            .setTitle("No Internet Connection")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }


    private fun startLoginActivity(loginType: String, position: Int) {
        if (isOnline()) {

            sessionManager= SessionManager1(context)

            sessionManager.setTopicId(topics[position].id)
          //  sessionManager.setName(name)
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra("loginType", loginType)
            intent.putExtra("topicId",topics[position].id)
            intent.putExtra("name", name)


            Log.d("mytag","topics[position].id=>" +topics[position].id)

            //startActivityForResult(intent, LOGIN_REQUEST_CODE)
            (context as Activity).startActivityForResult(intent, LOGIN_REQUEST_CODE)
        } else {
            //showNoInternetAlert()
        }
    }
}
