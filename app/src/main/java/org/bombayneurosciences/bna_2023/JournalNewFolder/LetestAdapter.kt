/*
package org.bombayneurosciences.bna_2023.JournalNewFolder

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.R
import java.text.SimpleDateFormat
import java.util.Locale

class LetestAdapter(private val context: Context, private var dataList: List<WelcomeElement>) :
    RecyclerView.Adapter<LetestAdapter.CaseViewHolder>() {
    private lateinit var mediaAdapter: SubSectionAdapter

    class CaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val titel: TextView = itemView.findViewById(R.id.titel)
        val articel: TextView = itemView.findViewById(R.id.articel)
        val auther: TextView = itemView.findViewById(R.id.auther)
        val desRecyclerView: RecyclerView = itemView.findViewById(R.id.desRecyclerView)
        val pdffile: ImageView = itemView.findViewById(R.id.pdffile)
        val fl_zoom: LinearLayout = itemView.findViewById(R.id.fl_zoom)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaseViewHolder {




        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.letest_jurnul, parent, false)
        return CaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CaseViewHolder, position: Int) {

//        val gestureDetector2 = GestureDetectorCompat(holder.itemView.context, DoubleTapListener(holder.itemView))


        val data = dataList[position]

        Log.e("positionget",""+position)


            holder.pdffile.visibility = View.GONE
            holder.fl_zoom.visibility = View.VISIBLE
            // Set up the RecyclerView for media
            mediaAdapter = SubSectionAdapter( context,data.subsections, data.title.toString(),data.author.toString())
            holder.desRecyclerView.layoutManager = LinearLayoutManager(context )
            holder.desRecyclerView.adapter = mediaAdapter








    }



    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateData(filteredData: List<WelcomeElement>) {
        dataList = filteredData
        notifyDataSetChanged()
    }

    override fun ItemClicked1(data: Subsection, position: Int) {
        TODO("Not yet implemented")
    }


}



*/
