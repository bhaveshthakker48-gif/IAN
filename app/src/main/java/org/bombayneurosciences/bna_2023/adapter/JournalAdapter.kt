package org.bombayneurosciences.bna_2023.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna.Model.Journal.JournalEntry
import org.bombayneurosciences.bna_2023.R

class JournalAdapter(
    private val context: Context,
    private var journalList: List<JournalEntry>,
    var isLatestSection: Boolean
) : RecyclerView.Adapter<JournalAdapter.JournalViewHolder>() {

    // Listener to handle item clicks
    private var itemClickListener: OnItemClickListener? = null

    // Interface for item click listener
    interface OnItemClickListener {
        fun onItemClick(position: Int,isLatestSection: Boolean)
    }

    // Set the item click listener
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.journal_recyclerview, parent, false)
        return JournalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        val journalItem = journalList[position]

        if (isLatestSection) {
            // Your logic for the "Latest" section
            holder.titleTextView.text = journalItem.title
            holder.authorTextView.text = journalItem.author
            holder.arrowCircleImageView.visibility = if (journalItem.articleFile.isNotEmpty()) View.VISIBLE else View.GONE
        } else {
            // Your logic for the "Archive" section
            holder.titleTextView.text = journalItem.month
            holder.authorTextView.text = journalItem.year
        }

        // Set a click listener for the item view
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position, isLatestSection)
        }
    }

    // Update the data set
    fun setData(newList: List<JournalEntry>) {
        journalList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return journalList.size
    }

    inner class JournalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        val authorTextView: TextView = itemView.findViewById(R.id.author)
        val arrowCircleImageView: ImageView = itemView.findViewById(R.id.arrowcircle)

    }
}
