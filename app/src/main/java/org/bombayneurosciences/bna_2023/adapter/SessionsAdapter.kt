package org.bombayneurosciences.bna_2023.adapter

import android.content.Context
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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.CallBack.SessionAdapterCallBack
import org.bombayneurosciences.bna_2023.Model.sesssions.Data
import org.bombayneurosciences.bna_2023.R
import java.text.SimpleDateFormat
import java.util.Locale

class SessionAdapter(
    private val context: Context,
    private val sessionAdapterCallBack: SessionAdapterCallBack
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var sessions: List<Data> = emptyList()

    companion object {
        const val VIEW_TYPE_SESSION = 1
        const val VIEW_TYPE_BREAK = 2
        const val VIEW_TYPE_SESSION_BREAK = 3
    }

//    fun setSessions(sessions: List<Data>) {
//        // Sort the sessions by start time
//        this.sessions = sessions.sortedBy { it.start_time }
//        // Notify the adapter of the change
//        notifyDataSetChanged()
//    }
fun setSessions(sessions: List<Data>) {
    // Sort sessions first by event_day, then by start_time
    this.sessions = sessions.sortedWith(compareBy({ it.event_day }, { it.start_time }))
    notifyDataSetChanged()
}


    override fun getItemViewType(position: Int): Int {
        return when (sessions[position].type) {
            "Session" -> VIEW_TYPE_SESSION
            "Break" -> VIEW_TYPE_BREAK
            "Session + Break" -> VIEW_TYPE_SESSION_BREAK
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SESSION -> createSessionViewHolder(parent)
            VIEW_TYPE_BREAK -> createBreakViewHolder(parent)
            VIEW_TYPE_SESSION_BREAK -> createSessionWithBreakViewHolder(parent)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    private fun createSessionViewHolder(parent: ViewGroup): SessionViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_session, parent, false)
        return SessionViewHolder(view)
    }

    private fun createBreakViewHolder(parent: ViewGroup): BreakViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_break, parent, false)
        return BreakViewHolder(view)
    }

    private fun createSessionWithBreakViewHolder(parent: ViewGroup): SessionWithBreakViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_session_with_break, parent, false)
        return SessionWithBreakViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = sessions[position]

//
        when (holder) {
            is SessionViewHolder -> {
                holder.bindSession(item)
                // Set a click listener only for SessionViewHolder
                holder.itemView.setOnClickListener {
                    sessionAdapterCallBack.onItemClick(holder.adapterPosition, item)
                    // You can add additional logic based on the item type if needed
                }
            }
            is BreakViewHolder -> holder.bindBreak(item)
            is SessionWithBreakViewHolder -> {
                holder.bindSessionWithBreak(item)
                // Set a click listener only for SessionWithBreakViewHolder
                holder.itemView.setOnClickListener {
//                    sessionAdapterCallBack.onItemClick(holder.adapterPosition, item)
                    // You can add additional logic based on the item type if needed
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return sessions.size
    }

//    fun getSessionsForDay(day: Int): List<Data> {
//        return sessions.filter { it.event_day == day }
//            .sortedBy { it.start_time }
//
//    }
fun getSessionsForDay(day: Int): List<Data> {
    return sessions.filter { it.event_day == day }
        .sortedBy { it.start_time.replace(":", "").toInt() }
}



    inner class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.sessionname)
        private val timeTextView: TextView = itemView.findViewById(R.id.eventdate)
        private val chairpersonTextView: TextView = itemView.findViewById(R.id.chairperson)

        fun bindSession(session: Data) {
            titleTextView.text = session.title
            timeTextView.text =
                "${formatTime24to12(session.start_time)} - ${formatTime24to12(session.end_time)}"

//            chairpersonTextView.text = session.chairpersons

           /* if (item.name != null) {
                holder.nameTextView.text = item.name
            } else {
                holder.nameTextView.text = "No Name"
            }*/


            if (session.chairpersons.equals("null")) {
                Log.e("chairpersons1", "chairpersons is null or blank: '${session.chairpersons}'")
                chairpersonTextView.visibility = View.GONE
            } else {
                Log.e("chairpersons2", "chairpersons is not null: '${session.chairpersons}'")
                chairpersonTextView.visibility = View.VISIBLE

                val venueText = "Speaker : ${session.chairpersons}"
                val spannable = SpannableString(venueText)

                spannable.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            itemView.context,
                            android.R.color.holo_red_dark
                        )
                    ),
                    0, 9, // Length of "Speakerrr"
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                val chairpersonStartIndex = 10
                val chairpersonEndIndex = venueText.length

                spannable.setSpan(
                    StyleSpan(Typeface.ITALIC),
                    chairpersonStartIndex,
                    chairpersonEndIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                chairpersonTextView.text = spannable
            }

            }
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

    inner class SessionWithBreakViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.sessionname)
        private val timeTextView: TextView = itemView.findViewById(R.id.eventdate)
        private val chairpersonTextView: TextView = itemView.findViewById(R.id.chairperson)
        private val layout: LinearLayout = itemView.findViewById(R.id.layout)


        fun bindSessionWithBreak(sessionWithBreak: Data) {
            titleTextView.text = sessionWithBreak.title
            timeTextView.text = "${formatTime24to12(sessionWithBreak.start_time)} - ${formatTime24to12(sessionWithBreak.end_time)}"
//            chairpersonTextView.text = sessionWithBreak.chairpersons

            if (sessionWithBreak.chairpersons.equals("null")) {
                Log.e("chairpersons1", "chairpersons is null or blank: '${sessionWithBreak.chairpersons}'")

                chairpersonTextView.visibility = View.GONE
                layout.visibility = View.GONE

            } else {

                chairpersonTextView.visibility = View.VISIBLE
                layout.visibility = View.VISIBLE

                Log.e("chairpersons3 ", "" + sessionWithBreak.chairpersons)
                val venueText = "Speaker : ${sessionWithBreak.chairpersons}"
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
                chairpersonTextView.text = spannable

            }
        }
    }
}
