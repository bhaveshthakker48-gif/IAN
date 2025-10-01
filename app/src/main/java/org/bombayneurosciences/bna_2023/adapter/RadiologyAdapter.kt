package org.bombayneurosciences.bna_2023.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.bombayneurosciences.bna_2023.CallBack.CaseOfMonthInterface.onImageClickCaseOfMonth
import org.bombayneurosciences.bna_2023.Model.CaseofMonth.MediaType
import org.bombayneurosciences.bna_2023.R

class RadiologyAdapter(
    private val context: Context,
    private val radiologyItems: List<String>, // Use List<String> for image paths
    private val onImageClickCaseOfMonthClickListener: onImageClickCaseOfMonth? = null
) : RecyclerView.Adapter<RadiologyAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_media, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        loadImage(radiologyItems[position], holder.imageView)
        holder.imageView.setOnClickListener {
            onImageClickCaseOfMonthClickListener?.onImageCaseItemClick(
                holder.adapterPosition,
                radiologyItems[position],
                MediaType.R_IMAGE.toString()
            )
        }
    }

    override fun getItemCount(): Int = radiologyItems.size

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView_media)
    }

    private fun loadImage(imagePath: String, imageView: ImageView) {
        Glide.with(context)
            .load("https://www.telemedocket.com/BNA/public/uploads/caseimages/img/$imagePath")
            .apply(RequestOptions().centerCrop())
            .into(imageView)
    }
}
