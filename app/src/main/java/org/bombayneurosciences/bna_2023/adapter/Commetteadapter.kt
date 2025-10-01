package org.bombayneurosciences.bna_2023.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.Model.CommiteeDataClass
import org.bombayneurosciences.bna_2023.R

class Commetteadapter(
    context: Context,
    CommiteeArrayList: ArrayList<CommiteeDataClass>
) :RecyclerView.Adapter<Commetteadapter.ViewHolder>() {

    lateinit var context: Context

    var CommiteeArrayList: ArrayList<CommiteeDataClass>?=null

    init {
        this.context=context
        this.CommiteeArrayList=CommiteeArrayList
    }





    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {

        val imageView: ImageView = itemView.findViewById(R.id.image_35)
        val textView: TextView = itemView.findViewById(R.id.header)
        val textView1: TextView = itemView.findViewById(R.id.president)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.commetee_single_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val data=CommiteeArrayList!![position]

        holder.textView.text=data.first_name+" "+data.last_name
        holder.textView1.text=data.position
        holder.imageView.setImageResource(data.imageUrl!!)
    }

    override fun getItemCount(): Int {
       return CommiteeArrayList!!.size
    }
}