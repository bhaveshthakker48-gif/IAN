package org.bombayneurosciences.bna_2023.adapter

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData
import org.bombayneurosciences.bna_2023.CallBack.JournalDataClick
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.databinding.AdapterJournalAchieveBinding
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import java.io.File

class JournalArchievesAdapter(
    private val journalList: List<JournalLoacalData>,
    private val context: Context,
    val JournalDataClick: JournalDataClick
) : RecyclerView.Adapter<JournalArchievesAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: AdapterJournalAchieveBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: JournalLoacalData, adapterPosition: Int) {
            // Set date text
            binding.TextViewDate.text = "${data.month} ${data.year}"

            // Create a SpannableStringBuilder to format the text with different colors
            val builder = SpannableStringBuilder()

// Append "Vol." + topItem.volume in black color
            builder.appendColoredText("Vol.${data.volume}", Color.BLACK)

// Append " | " in gray color
            builder.appendColoredText(" | ", Color.GRAY)

// Append "Issue" + topItem.issue_no in black color
            builder.appendColoredText("Issue${data.issue_no}", Color.BLACK)

            //builder.appendColoredText(" | ", Color.GRAY)

// Append " " + abbreviatedMonth + " " + lastTwoDigits in red color
           // builder.appendColoredText(" $abbreviatedMonth $lastTwoDigits", Color.RED)

// Now `builder` contains the formatted text with different colors
            val formattedText = builder.toString()

// Use `formattedText` as needed, for example setting it to a TextView


            binding.TextViewVol.text=builder


            // Calculate and set card elevation dynamically based on position
            val elevation = binding.cardView.cardElevation
            val newElevation = elevation - (adapterPosition * 0.5f) // Adjust this factor as needed
            binding.cardView.cardElevation = newElevation

            // Load PDF file if exists
            val pdfFileName = ConstanstsApp.getFileNameFromUrl(data.issueFile)
            val fileDir = context.getExternalFilesDir("BNA_App_PDF")
            val pdfFile = File(fileDir, pdfFileName)
            Log.e("ppppppp",""+pdfFileName)
            Log.e("ppppppp",""+fileDir)

            if (pdfFile.exists()) {
                val filePath = ConstanstsApp.getFilePathPDF(pdfFileName)
                Log.d(ConstanstsApp.tag, "filePath => $filePath")

                // Load the PDF file into the PDFView
                binding.pdfView.loadPdf(filePath)

                // Apply animation to cardView
                //animateCardView(binding.cardView)
            } else {
                Log.e(ConstanstsApp.tag, "File does not exist: ${pdfFile.absolutePath}")
                // Optionally, handle case where file does not exist
            }

            binding.cardView.setOnClickListener(object :View.OnClickListener
            {
                override fun onClick(v: View?) {


                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        JournalDataClick.JournalItemClicked(data, position, v!!)
                        binding.cardView.strokeColor=context.getColor(R.color.red)
                    }
                }

            })
        }

        private fun animateCardView(cardView: MaterialCardView) {
            // Scale animation for cardView
            cardView.scaleX = 0.8f
            cardView.scaleY = 0.8f
            cardView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(AccelerateInterpolator())
                .setDuration(500)
                .start()

          //  cardView.strokeColor=context.getColor(R.color.red)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = AdapterJournalAchieveBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = journalList[position]
        holder.bind(data,holder.adapterPosition)

        // Adjust bottom margin to overlap cards
       /* val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = -350 // Adjust this value as needed
        holder.itemView.layoutParams = layoutParams*/
    }

    override fun getItemCount(): Int {
        return journalList.size
    }

    fun SpannableStringBuilder.appendColoredText(text: String, color: Int) {
        val start = length
        append(text)
        setSpan(ForegroundColorSpan(color), start, length, 0)
    }
}

