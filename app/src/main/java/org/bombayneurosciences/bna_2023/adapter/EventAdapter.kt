package org.bombayneurosciences.bna_2023.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.Model.Events.Data
import org.bombayneurosciences.bna_2023.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class EventAdapter(private val onItemClick: (Data, Int) -> Unit) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private var events: List<Data> = emptyList()

    fun setEvents(events: List<Data>) {
        this.events = events.sortedBy { it.start_date }

        this.events = events
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_recyclerview, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventNameTextView: TextView = itemView.findViewById(R.id.eventname)
        private val eventDateTextView: TextView = itemView.findViewById(R.id.eventdate)
        private val eventVenueTextView: TextView = itemView.findViewById(R.id.eventvenue)
        private val eventMeetingTypeTextView: TextView = itemView.findViewById(R.id.eventmeeting)
        private val eventNameTextView1: TextView = itemView.findViewById(R.id.eventname1)
        private val eventDateTextView1: TextView = itemView.findViewById(R.id.eventdate1)
        private val eventVenueTextView1: TextView = itemView.findViewById(R.id.eventvenue1)
        private val eventMeetingTypeTextView1: TextView = itemView.findViewById(R.id.eventmeeting1)
        private val shadowLayout: RelativeLayout = itemView.findViewById(R.id.shadow)
        private val shadowLayout1: RelativeLayout = itemView.findViewById(R.id.shadow1)
        private val propertyImageActive: ImageView = itemView.findViewById(R.id.property_imag2)
        private val propertyImageActive1: ImageView = itemView.findViewById(R.id.property_imag3)

        private val propertyImageInactive: ImageView = itemView.findViewById(R.id.property_image2)


        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val daysUntilEvent = calculateDaysUntilEvent(events[position].start_date)
                    onItemClick(events[position], daysUntilEvent)
                }
            }
        }

        fun bind(event: Data) {

            // Check if the event is active
            if (isEventActive(event)) {
                // Event is active, make propertyImageActive visible and propertyImageInactive gone
                propertyImageActive.visibility = View.VISIBLE
                propertyImageActive1.visibility = View.VISIBLE

                propertyImageInactive.visibility = View.GONE
                // Set shadow background
                shadowLayout.setBackgroundResource(R.drawable.shadow)
                shadowLayout1.setBackgroundResource(R.drawable.shadow)
                // Load blink animation
                val blinkAnimation = AnimationUtils.loadAnimation(itemView.context, R.anim.blink_animation)
                // Start blink animation on propertyImageActive
                propertyImageActive1.startAnimation(blinkAnimation)

            } else {
                // Event is inactive, make propertyImageActive gone and propertyImageInactive visible
                propertyImageActive.visibility = View.GONE
                propertyImageInactive.visibility = View.VISIBLE
                propertyImageActive1.visibility = View.GONE

                // Remove shadow background
                shadowLayout.setBackgroundResource(0) // Set to no background
                shadowLayout1.setBackgroundResource(0) // Set to no background

            }

            eventNameTextView.text = event.name
            eventNameTextView1.text = event.name

            // Format date
            val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val sdfOutput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val startDate = sdfOutput.format(sdfInput.parse(event.start_date))
            val endDate = sdfOutput.format(sdfInput.parse(event.end_date))
            eventDateTextView.text = " $startDate - $endDate"
            eventDateTextView1.text = " $startDate - $endDate"

            // Set venue text with red color
            // Set venue text with red color
           /* val venueText = "Venue : ${event.venue}"
            eventVenueTextView.text = venueText
            eventVenueTextView.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark)) // Set color to red*/

            // Set "Venue :" text with black color
            eventVenueTextView.setTextColor(itemView.context.getColor(android.R.color.black))

//            val venueText1 = "Venue : ${event.venue}"
//            eventVenueTextView1.text = venueText
//            eventVenueTextView1.setTextColor(itemView.context.getColor(android.R.color.black)) // Set color to red


            val venueText = "Venue : ${event.venue}"
            val spannable = SpannableString(venueText)
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark)),
                0, 7, // Index of "Venue :" including the space after ":"
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            eventVenueTextView.text = spannable


            val venueText1 = "Venue : ${event.venue}"
            val spannable1 = SpannableString(venueText)
            spannable.setSpan(ForegroundColorSpan(itemView.context.getColor(android.R.color.holo_red_dark)), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // Set color to red for "Venue"
            spannable.setSpan(ForegroundColorSpan(Color.BLACK), 8, venueText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // Set color to black for venue name
            eventVenueTextView1.text = spannable

            // Set "Venue :" text with black color
            eventVenueTextView1.setTextColor(itemView.context.getColor(android.R.color.black))


            eventMeetingTypeTextView.text = " ${event.etype}"
            eventMeetingTypeTextView1.text = " ${event.etype}"


            if (event.etype.equals("Annual Meeting", ignoreCase = true)) {
                shadowLayout.visibility = View.VISIBLE
                shadowLayout1.visibility = View.GONE
            } else if (event.etype.equals("Quarterly Meeting", ignoreCase = true)) {
                shadowLayout.visibility = View.GONE
                shadowLayout1.visibility = View.VISIBLE
            } else {
                shadowLayout.visibility = View.GONE
                shadowLayout1.visibility = View.GONE
            }
            // Set background drawable based on meeting type
            if (event.etype.equals("Annual Meeting", ignoreCase = true)) {
               // shadowLayout.setBackgroundResource(R.drawable.shadow)

                eventMeetingTypeTextView.setTextColor(itemView.context.getColor(R.color.white))

            } else {
                //shadowLayout.setBackgroundResource(R.drawable.shadow)
                eventMeetingTypeTextView.setTextColor(itemView.context.getColor(R.color.red1))

            }



    }
    }

    private fun isEventActive(event: Data): Boolean {
        val currentDate = getCurrentDate() // Get current date
        val eventEndDate = event.end_date // Get event end date

        // Compare event end date with current date
        // If event end date is greater than or equal to current date, consider it as active event
        var isActive = eventEndDate >= currentDate

        // Also check if the current date is after the start date, to handle cases where the event is yet to start
        val eventStartDate = event.start_date // Get event start date
        val isBeforeStartDate = currentDate < eventStartDate

        // If current date is before the start date and after the end date, consider the event as inactive
        if (isBeforeStartDate) {
            isActive = false
        }

        Log.d("EventActivity", "Event: ${event.name}, Start Date: $eventStartDate, End Date: $eventEndDate, Current Date: $currentDate, Active: $isActive")

        return isActive
    }



    private fun getCurrentDate(): String {
        // Implement your logic to get the current date
        // For example, you can use SimpleDateFormat to format the current date
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }


    private fun calculateDaysUntilEvent(eventStartDate: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        val startDate = sdf.parse(eventStartDate)
        val diffInMillies = startDate.time - currentDate.time
        val diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)
        return diffInDays.toInt()

    }
}
