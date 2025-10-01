package org.bombayneurosciences.bna_2023
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.Data.SharedPreferencesManager
import org.bombayneurosciences.bna_2023.Model.Notification.Data
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp


class NotificationAdapter(private var notifications: ArrayList<Data>, private val cardClickListener: OnCardClickListener,
                          private val sharedPreferencesManager: SharedPreferencesManager) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.notificationTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.notificationDescription)
        val dateTimeTextView: TextView = itemView.findViewById(R.id.notificationDateTime)
        val arrowImageView: ImageView = itemView.findViewById(R.id.arrowcircle)
        val lineView: ImageView = itemView.findViewById(R.id.lineview)

        val card :LinearLayout = itemView.findViewById(R.id.card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_recyclerview, parent, false)
        return NotificationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val currentItem = notifications[position]

        Log.d(ConstanstsApp.tag, "Offline notifications count: {notifications.size}")


           if (currentItem.attachment.isNotEmpty()){
               holder.arrowImageView.visibility = View.VISIBLE
           }else{
               holder.arrowImageView.visibility = View.GONE
           }
        // Set values from the NotificationEntity
        holder.titleTextView.text = currentItem.title
        holder.descriptionTextView.text = currentItem.content
        holder.dateTimeTextView.text = currentItem.created_at

        Log.d(ConstanstsApp.tag, "recyclerview => recyclerview")

        // Check if the notification is read or not from SharedPreferencesManager
        val isRead = sharedPreferencesManager.isNotificationRead(currentItem.id)

        if (isRead) {
            // Notification is read, hide the lineview
            holder.lineView.visibility = View.GONE
        } else {
            // Notification is unread, show the lineview
            holder.lineView.visibility = View.VISIBLE
        }
        holder.arrowImageView.setOnClickListener{
            cardClickListener.onCardClick(currentItem)

        }


    }
    interface OnCardClickListener {

        fun onCardClick(notificationData: Data)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }
}