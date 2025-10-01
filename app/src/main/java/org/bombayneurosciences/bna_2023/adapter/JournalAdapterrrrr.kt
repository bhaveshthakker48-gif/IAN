package org.bombayneurosciences.bna_2023.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna.Model.Journal.JournalEntry
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp

class JournalAdapterrrrr(
    private var dataList: List<JournalEntry>,
    private val itemClickListener: (JournalEntry) -> Unit
) : RecyclerView.Adapter<JournalAdapterrrrr.ViewHolder>() {

    fun updateData(newData: List<JournalEntry>) {
        dataList = newData
        notifyDataSetChanged()
        Log.d(ConstanstsApp.tag, "Updated data. New size: ${dataList.size}")
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.journal_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]

        // Set data to your views in the ViewHolder
        holder.titleTextView.text = currentItem.title
        holder.authorTextView.text = currentItem.author
        holder.arrowCircleImageView.setImageResource(R.drawable.arrowcircle)

        // Set a click listener for the entire item view
        holder.itemView.setOnClickListener {
            itemClickListener(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        val authorTextView: TextView = itemView.findViewById(R.id.author)
        val arrowCircleImageView: ImageView = itemView.findViewById(R.id.arrowcircle)
    }
}
