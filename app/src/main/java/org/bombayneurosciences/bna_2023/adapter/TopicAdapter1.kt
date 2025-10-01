package org.bombayneurosciences.bna_2023.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.Activity.LoginActivity
import org.bombayneurosciences.bna_2023.Activity.VotingActivity
import org.bombayneurosciences.bna_2023.Model.Topic_new.Data
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import java.text.SimpleDateFormat
import java.util.Locale

class TopicAdapter1() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var topics: List<Data> = emptyList()
    lateinit var sessionManager: SessionManager1
    private val LOGIN_REQUEST_CODE = 1001


    companion object {
        const val VIEW_TYPE_TOPIC = 1
        const val VIEW_TYPE_BREAK = 2
        const val VIEW_TYPE_TOPIC_WITH_BREAK = 3
        const val VIEW_TYPE_SESSION = 4
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

            VIEW_TYPE_SESSION -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.session_recyclerview, parent, false)
                SessionViewHolder(view)
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
            is SessionViewHolder -> holder.bindSession(item)
        }

        // Set a click listener for all view holders
        holder.itemView.setOnClickListener {
            val clickedItem = topics[position]
            when (clickedItem.type) {
                "Topic", "Session" -> {
                    // Handle click for Topic or Session
                    // Open the VotingActivity or perform any other action
                }

                else -> {
                    // Handle click for breaks if needed
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            topics[position].type.equals(
                "Topic + Break",
                ignoreCase = true
            ) -> VIEW_TYPE_TOPIC_WITH_BREAK

            topics[position].type.equals("Topic", ignoreCase = true) -> VIEW_TYPE_TOPIC
            topics[position].type.equals("Session", ignoreCase = true) -> VIEW_TYPE_SESSION
            else -> VIEW_TYPE_BREAK
        }
    }

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Add views and bind data for Topic type
        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val dateTextView: TextView = itemView.findViewById(R.id.topicdate)
        private val speakerTextView: TextView = itemView.findViewById(R.id.speaker)
        val voting: ImageView = itemView.findViewById(R.id.vector)
        val layout: CardView = itemView.findViewById(R.id.layout)

        fun bindTopic(topic: Data) {



            titleTextView.text = topic.title
            dateTextView.text =
                "${formatTime24to12(topic.start_time)} - ${formatTime24to12(topic.end_time)}"
//            speakerTextView.text = topic.speaker?.toString() ?: "Speaker not available"

            if (topic.speaker.equals("-")) {
                Log.e("chairpersons1", "chairpersons is null or blank: '${topic.speaker}'")

                speakerTextView.visibility = View.GONE
                layout.visibility = View.GONE

            } else {

                speakerTextView.visibility = View.VISIBLE
                layout.visibility = View.VISIBLE

                val venueText = "Speaker : ${topic.speaker}"
                val spannable = SpannableString(venueText)
                spannable.setSpan(
                    ForegroundColorSpan(itemView.context.getColor(android.R.color.holo_red_dark)),
                    0,
                    9,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                ) // Set color to red for "Venue"
                spannable.setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    8,
                    venueText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                ) // Set color to black for venue name
                spannable.setSpan(
                    StyleSpan(Typeface.ITALIC),
                    8,
                    venueText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                ) // Set italic style for venue name
                speakerTextView.text = spannable
                speakerTextView.setTextColor(itemView.context.getColor(android.R.color.black))
            }

            // Set click listener for the voting ImageView
            voting.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedTopic = topics[position]

                    Log.d(ConstanstsApp.tag, "topic id on click=" + clickedTopic.id)

                   // handleVectorClick(adapterPosition)


                    // Open the VotingActivity
                    val intent = Intent(itemView.context, VotingActivity::class.java)
                    // Pass any necessary data to the VotingActivity using intent extras
                    intent.putExtra("topicId", clickedTopic.id)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

//    private fun handleVectorClick(position: Int) {
//
//        Log.d("mytag", "sharepreferenceAppkill" + sharepreferenceAppkill)
//        Log.d("mytag", "isLoggedIn" + isLoggedIn)
//
//
//
//        when (isLoggedIn) {
//            true -> {
//
//                val intent = Intent(context, VotingActivity::class.java)
//                intent.putExtra("topicId", topics[position].id)
//                intent.putExtra("eventId", topics[position].event_id)
//                intent.putExtra("sessionId", topics[position].session_id)
//                intent.putExtra("name", name)
//                context.startActivity(intent)
//
//            }
//
//            false -> {
//
//                startLoginActivity("Voting", position)
//
//            }
//        }
//
//
////        if (position != RecyclerView.NO_POSITION) {
////            val clickedTopic = topics[position]
////            if (isOnline() || isLoggedIn) {
////                //val (isLoggedIn, shouldRememberMe) = sharedPreferencesManager1.getUserStatus()
////                if (session1 == true) {
////                    val intent = Intent(context, VotingActivity::class.java)
////                    context.startActivity(intent)
////                    Log.d("mytag","condition1")
////                } else {
////                    if (sharepreferenceAppkill == null || sharepreferenceAppkill.equals("AppKill")) {
////                        val intent = Intent(context, LoginActivity::class.java)
////                        context.startActivity(intent)
////                        Log.d("mytag","condition2")
////
////                    } else {
////                        if (isLoggedIn == true && shouldRememberMe == true) {
////                            val intent = Intent(context, VotingActivity::class.java)
////                            context.startActivity(intent)
////                            Log.d("mytag","condition3")
////
////                        } else if (isLoggedIn == true && shouldRememberMe == false) {
////                            val intent = Intent(context, VotingActivity::class.java)
////                            context.startActivity(intent)
////                            Log.d("mytag","condition4")
////
////                        } else if (isLoggedIn == false && shouldRememberMe == false) {
////                            startLoginActivity("Voting")
////
////
////                            Log.d("mytag","condition5")
////
////                        }
////                    }
////                }
////            } else {
////                // Show "No Internet" alert
////              //  showNoInternetAlert()
////            }
////        }
//    }


    inner class BreakViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Add views and bind data for Break type
        private val titleTextView: TextView = itemView.findViewById(R.id.breakname)
        private val timeTextView: TextView = itemView.findViewById(R.id.brektime)
        private val breaks: CardView = itemView.findViewById(R.id.breaks)

        @SuppressLint("SetTextI18n")
        fun bindBreak(breakData: Data) {



            if (breakData.title.equals("null")){

                breaks.visibility = View.GONE

            }else{

                Log.e(" breakData.title ",""+ breakData.title)
                Log.e(" breakData.title ",""+ breakData.start_time)
                Log.e(" breakData.title ",""+ breakData.end_time)

                breaks.visibility = View.VISIBLE

            titleTextView.text = breakData.title
            timeTextView.text =
                "${formatTime24to12(breakData.start_time)} - ${formatTime24to12(breakData.end_time)}"
        }
        }
    }

    inner class TopicWithBreakViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Add views and bind data for Topic with Break type
        private val titleTextView: TextView = itemView.findViewById(R.id.title_topicwithbreak)
        private val dateTextView: TextView = itemView.findViewById(R.id.topicdate_topicwithbreak)
        private val speakerTextView: TextView = itemView.findViewById(R.id.speaker)
        val voting: ImageView = itemView.findViewById(R.id.vector)
        val layout: LinearLayout = itemView.findViewById(R.id.layout)

        fun bindTopicWithBreak(topic: Data) {
            titleTextView.text = topic.title
            dateTextView.text =
                "${formatTime24to12(topic.start_time)} - ${formatTime24to12(topic.end_time)}"
//            speakerTextView.text = topic.speaker?.toString() ?: "Speaker not available"
            Log.e("topic", "chairpersons is null or blank: '${topic.speaker}'")
            if (topic.speaker.equals("-")) {
                Log.e("topic", "chairpersons is null or blank: '${topic.speaker}'")

                speakerTextView.visibility = View.GONE
                layout.visibility = View.GONE

            }else{

                speakerTextView.visibility = View.VISIBLE
                layout.visibility = View.VISIBLE

            val venueText = "Speaker : ${topic.speaker}"
            val spannable = SpannableString(venueText)
            spannable.setSpan(
                ForegroundColorSpan(itemView.context.getColor(android.R.color.holo_red_dark)),
                0,
                9,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            ) // Set color to red for "Venue"
            spannable.setSpan(
                ForegroundColorSpan(Color.BLACK),
                8,
                venueText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            ) // Set color to black for venue name
            spannable.setSpan(
                StyleSpan(Typeface.ITALIC),
                8,
                venueText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            ) // Set italic style for venue name
            speakerTextView.text = spannable
            speakerTextView.setTextColor(itemView.context.getColor(android.R.color.black))
}
        }
    }

    inner class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Add views and bind data for Session type
        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val dateTextView: TextView = itemView.findViewById(R.id.topicdate)
        private val speakerTextView: TextView = itemView.findViewById(R.id.speaker)
        val voting: ImageView = itemView.findViewById(R.id.vector)

        fun bindSession(session: Data) {
            titleTextView.text = session.title
            dateTextView.text =
                "${formatTime24to12(session.start_time)} - ${formatTime24to12(session.end_time)}"
            speakerTextView.text = session.speaker?.toString() ?: "Speaker not available"


            val venueText = "Speaker : ${session.speaker}"
            val spannable = SpannableString(venueText)
            spannable.setSpan(
                ForegroundColorSpan(itemView.context.getColor(android.R.color.holo_red_dark)),
                0,
                9,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            ) // Set color to red for "Venue"
            spannable.setSpan(
                ForegroundColorSpan(Color.BLACK),
                8,
                venueText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            ) // Set color to black for venue name
            spannable.setSpan(
                StyleSpan(Typeface.ITALIC),
                8,
                venueText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            ) // Set italic style for venue name
            speakerTextView.text = spannable
            speakerTextView.setTextColor(itemView.context.getColor(android.R.color.black))

            // Set click listener for the voting ImageView
            voting.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedSession = topics[position]

                    Log.d(ConstanstsApp.tag, "session id on click=" + clickedSession.id)

                    // Handle click for sessions if needed
                    // Open the VotingActivity or perform any other action
                }
            }
        }
    }

    fun formatTime24to12(time: String): String? {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
        val date = inputFormat.parse(time)
        return outputFormat.format(date)?.toUpperCase(Locale.getDefault())
    }
}
//    private fun startLoginActivity(loginType: String, position: Int) {
//        if (isOnline()) {
//
//            sessionManager= SessionManager1(context)
//
//            sessionManager.setTopicId(topics[position].id)
//            //  sessionManager.setName(name)
//            val intent = Intent(context, LoginActivity::class.java)
//            intent.putExtra("loginType", loginType)
//            intent.putExtra("topicId",topics[position].id)
//            intent.putExtra("name", name)
//
//
//            Log.d("mytag","topics[position].id=>" +topics[position].id)
//
//            //startActivityForResult(intent, LOGIN_REQUEST_CODE)
//            (context as Activity).startActivityForResult(intent, LOGIN_REQUEST_CODE)
//        } else {
//            //showNoInternetAlert()
//        }
//
//}



