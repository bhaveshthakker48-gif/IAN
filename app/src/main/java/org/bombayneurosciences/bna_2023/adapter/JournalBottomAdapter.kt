package org.bombayneurosciences.bna_2023.adapter

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData
import org.bombayneurosciences.bna_2023.CallBack.JournalDataClick
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.databinding.ActivityJournalBinding
import org.bombayneurosciences.bna_2023.databinding.AdapterJournalBottomBinding

class JournalBottomAdapter(val JournalList: List<JournalLoacalData>, val context: Context,
                           val JournalDataClick: JournalDataClick
):RecyclerView.Adapter<JournalBottomAdapter.ViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION


    inner class ViewHolder(val binding:AdapterJournalBottomBinding):RecyclerView.ViewHolder(binding.root) {
        fun binding(data: JournalLoacalData, position: Int) {

            Log.e("JournalLoacalData_adapter",""+data)

            binding.TextViewIndex.text=(position+1).toString()

            binding.TextViewNoPage.text=data.indexPage.toString()

            val titleText=data.title+":"+data.articleType

            binding.TextViewAuthor.text=data.author


            // Assuming data is your object containing title and articleType
            val title = data.title ?: "" // Assuming title can't be null, if so, handle it as needed

// Check if articleType is null or empty
            val articleType = data.articleType
            val typeString = if (!articleType.isNullOrEmpty()) {
                " : $articleType"
            } else {
                ""
            }

// Create a SpannableStringBuilder to apply different colors
            val spannableBuilder = SpannableStringBuilder()

// Append title in red color
            spannableBuilder.append(title)
            spannableBuilder.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context,R.color.dark_red)),
                0,
                title.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

// Append articleType if not empty, in gray color
            if (!articleType.isNullOrEmpty()) {
                val startIndex = spannableBuilder.length
                spannableBuilder.append(typeString)
                spannableBuilder.setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    startIndex,
                    startIndex + typeString.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

// Now spannableBuilder contains the formatted title with colors
            val formattedTitle = spannableBuilder.toString()

            binding.TextViewTitle.text=spannableBuilder

            val truncatedText = if (titleText.length > 35) titleText.substring(0, 35) + "..." else titleText



            // Update background color based on selection
            if (position == selectedPosition) {
                binding.MaterialCardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.dark_red)
                )
                /*val radius = context.resources.getDimension(R.dimen.card_corner_radius)
                binding.MaterialCardView.radius = radius*/
                binding.TextViewTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.TextViewIndex.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.TextViewNoPage.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.TextViewAuthor.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                binding.MaterialCardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, android.R.color.white)
                )
                binding.TextViewTitle.setTextColor(ContextCompat.getColor(context, R.color.dark_red))
                binding.TextViewIndex.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.TextViewNoPage.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.TextViewAuthor.setTextColor(ContextCompat.getColor(context, R.color.black))


            }

            binding.ConstraintLayoutLatest.setOnClickListener(object :View.OnClickListener
            {
                override fun onClick(v: View?) {

                    val previousPosition = selectedPosition
                    selectedPosition = position

                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                 JournalDataClick.JournalItemClicked(data,position,v!!)
                }

            })
            binding.TextViewTitle.setOnClickListener(object :View.OnClickListener
            {
                override fun onClick(v: View?) {

                    val previousPosition = selectedPosition
                    selectedPosition = position

                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    JournalDataClick.JournalItemClicked(data,position,v!!)
                }

            })

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JournalBottomAdapter.ViewHolder {

        val binding = AdapterJournalBottomBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: JournalBottomAdapter.ViewHolder, position: Int) {
        val data=JournalList[position]
        
        holder.binding(data,position)
    }

    override fun getItemCount(): Int {
       return JournalList.size
    }
}