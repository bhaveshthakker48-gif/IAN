package org.bombayneurosciences.bna_2023.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna.Model.Journal.JournalEntry
import org.bombayneurosciences.bna_2023.R

class JournalAdapter2(
    private val context: Context,
    private val journalList: List<JournalEntry>
) : RecyclerView.Adapter<JournalAdapter2.ViewHolder>() {

    // Listener to handle item clicks
    var onItemClick: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.journal_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val journalEntry = journalList[position]

        holder.titleTextView.text = journalEntry.title
        holder.authorTextView.text = journalEntry.author

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return journalList.size
    }

    fun setData(journalList: List<JournalEntry>) {

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        val authorTextView: TextView = itemView.findViewById(R.id.author)
    }
}
