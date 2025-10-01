package org.bombayneurosciences.bna_2023.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.CallBack.CaseOfMonthInterface.CaseItemClickListenerCaseOfMonth
import org.bombayneurosciences.bna_2023.Model.CaseofMonth.Data
import org.bombayneurosciences.bna_2023.Model.CaseofMonth.DataX
import org.bombayneurosciences.bna_2023.R
import java.text.SimpleDateFormat
import java.util.Locale

class CaseAdapter(private val context: Context, private var dataList: List<DataX>,
                  private val itemClickListener: CaseItemClickListenerCaseOfMonth? = null) :
    RecyclerView.Adapter<CaseAdapter.CaseViewHolder>() {

    class CaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val caseNoTextView: TextView = itemView.findViewById(R.id.case_no)
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        val nameDesignationTextView: TextView = itemView.findViewById(R.id.name_designation)
        val dateValidationTextView: TextView = itemView.findViewById(R.id.datevalidation)
        val layout:RelativeLayout=itemView.findViewById(R.id.case_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaseViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.case_recyclerview, parent, false)
        return CaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CaseViewHolder, position: Int) {
        val data = dataList[position]

        holder.caseNoTextView.text = "Case No: ${data.case_id}"
        holder.titleTextView.text = data.title_plain
        holder.nameDesignationTextView.text = "${data.name}, ${data.designation}"
       // holder.dateValidationTextView.text = "Valid upto: ${data.end_date}"
        holder.dateValidationTextView.text = "Valid upto: ${convertDateFormat(data.end_date!!, "yyyy-MM-dd", "dd-MM-yyyy")}"


        // Set click listener for the item
        holder.itemView.setOnClickListener {
            // Open CaseActivity with a list of Data objects

        }

        holder.layout.setOnClickListener {
            itemClickListener!!.onCaseItemClick(holder.adapterPosition,data)
        }
    }

    private fun convertDateFormat(dateString: String, inputFormat: String, outputFormat: String): String {
        val inputFormatter = SimpleDateFormat(inputFormat, Locale.getDefault())
        val outputFormatter = SimpleDateFormat(outputFormat, Locale.getDefault())
        val date = inputFormatter.parse(dateString)
        return outputFormatter.format(date)
    }


    override fun getItemCount(): Int {
        return dataList.size
    }

        fun updateData(filteredData: List<DataX>) {
            dataList = filteredData
            notifyDataSetChanged()
        }

    }

