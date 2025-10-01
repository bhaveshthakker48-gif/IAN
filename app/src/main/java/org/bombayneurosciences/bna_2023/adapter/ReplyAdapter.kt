package org.bombayneurosciences.bna_2023.adapter

import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.Model.chats.receivecomments.Data
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import java.text.SimpleDateFormat
import java.util.Locale

class ReplyAdapter(private val repliesList: List<Data>, private val delegateId: String) :
    RecyclerView.Adapter<ReplyAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commFromTextView = itemView.findViewById<TextView>(R.id.commFromTextView)
        val commtype :TextView = itemView.findViewById(R.id.commenttype)

        val createdAtTextView = itemView.findViewById<TextView>(R.id.createdAtTextView)
        val commentTextView = itemView.findViewById<TextView>(R.id.commentTextView)
        val commFromTextView1 = itemView.findViewById<ImageView>(R.id.commFromTextView1)
        val createdAtTextView1 = itemView.findViewById<TextView>(R.id.createdAtTextView1)
        val commentTextView1 = itemView.findViewById<TextView>(R.id.commentTextView1)
        val receiveCardView = itemView.findViewById<LinearLayout>(R.id.receive_cardView)
        val sendCardView = itemView.findViewById<LinearLayout>(R.id.send_cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.reply_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reply = repliesList[position]

        when (reply.commFrom) {
            "Member" -> {
                holder.commFromTextView.text = reply.commFromname
                holder.commtype.text = ""
            }
            "Moderator" -> {
                holder.commFromTextView.text = reply.commFromname
                holder.commtype.text = "(${reply.commFrom})"
            }
            "Admin" -> {
                holder.commFromTextView.text = ""
                holder.commtype.text = reply.commFrom
            }
        }

        // Parse the date from the server's format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val parsedDate = dateFormat.parse(reply.created_at)

        // Format the date to your desired format
        val formattedDate = when {
            DateUtils.isToday(parsedDate.time) -> {
                "Today " + SimpleDateFormat("hh:mm ", Locale.getDefault()).format(parsedDate)
            }
            else -> {
                SimpleDateFormat("dd/MM/yyyy hh:mm ", Locale.getDefault()).format(parsedDate)
            }
        }

        holder.createdAtTextView.text = formattedDate
        holder.commentTextView.text = reply.comment
        holder.createdAtTextView1.text = formattedDate
        holder.commentTextView1.text = reply.comment
        Log.d(ConstanstsApp.tag, "Delegate ID: $delegateId, CommFrom: ${reply.commFrom}, Comment: ${reply.comment}")

        // Check if the reply is from the same delegate as the logged-in user
        if (delegateId == reply.commFromid.toString()) {
            // Show the send_cardView and hide the receive_cardView
            holder.sendCardView.visibility = View.VISIBLE
            holder.receiveCardView.visibility = View.GONE
        } else {
            // Show the receive_cardView and hide the send_cardView
            holder.sendCardView.visibility = View.GONE
            holder.receiveCardView.visibility = View.VISIBLE
        }
    }


    override fun getItemCount(): Int {
        return repliesList.size
    }
}
