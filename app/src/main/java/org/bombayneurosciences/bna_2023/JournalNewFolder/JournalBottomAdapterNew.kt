package org.bombayneurosciences.bna_2023.JournalNewFolder

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
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
import org.bombayneurosciences.bna_2023.databinding.PopupJournalNewBinding

class JournalBottomAdapterNew(val JournalList: List<WelcomeElement>, val context: Context,val JournalDataClick: JournalDataClick

):RecyclerView.Adapter<JournalBottomAdapterNew.ViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION


    inner class ViewHolder(val binding:PopupJournalNewBinding):RecyclerView.ViewHolder(binding.root) {
        fun binding(data: WelcomeElement, position: Int) {

            binding.TextViewIndex.text=(position+1).toString()

            binding.TextViewNoPage.text=data.index_page.toString()

            var titleText =""
            if (data.article_type==null) {
                 titleText =""
            }else{
                titleText= " : " + data.article_type
            }

            binding.TextViewAuthor.text=data.author


            // Create a SpannableString
            val spannable = SpannableString("${data.title} ${titleText}")

// Set color for the first name
            val firstNameColor = context.getColor(R.color.dark_red)
            spannable.setSpan(
                ForegroundColorSpan(firstNameColor),
                0,
                data.title.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

// Set color for the second name
            val secondNameColor = Color.BLACK
            spannable.setSpan(
                ForegroundColorSpan(secondNameColor),
                data.title.length + 1, // start after the first name and the space
                spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )


            binding.TextViewTitle.text=spannable



            binding.ConstraintLayoutLatest.setOnClickListener(object :View.OnClickListener
            {
                override fun onClick(v: View?) {

                    val previousPosition = selectedPosition
                    selectedPosition = position

                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    JournalDataClick.JournalItemClicked1(data,position,v!!)
                }

            })
            binding.TextViewTitle.setOnClickListener(object :View.OnClickListener
            {
                override fun onClick(v: View?) {

                    val previousPosition = selectedPosition
                    selectedPosition = position

                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    JournalDataClick.JournalItemClicked1(data,position,v!!)
                }

            })


    }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JournalBottomAdapterNew.ViewHolder {

        val binding = PopupJournalNewBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: JournalBottomAdapterNew.ViewHolder, position: Int) {
        val data=JournalList[position]
        
        holder.binding(data,position)
    }

    override fun getItemCount(): Int {
       return JournalList.size
    }
}