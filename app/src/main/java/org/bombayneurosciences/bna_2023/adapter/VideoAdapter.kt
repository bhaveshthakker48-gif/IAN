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

class VideoAdapter(
    private val context: Context,
    private val videoItems: List<String>, // Use List<String> for video paths
    private val onImageClickCaseOfMonthClickListener: onImageClickCaseOfMonth? = null
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        loadVideo(videoItems[position], holder.videoView)
        holder.videoView.setOnClickListener {
            onImageClickCaseOfMonthClickListener?.onImageCaseItemClick(
                holder.adapterPosition,
                videoItems[position],
                MediaType.VIDEO.toString()
            )
        }
    }

    override fun getItemCount(): Int = videoItems.size

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoView: ImageView = itemView.findViewById(R.id.videoView)
    }

    private fun loadVideo(videoPath: String, videoView: ImageView) {
        Glide.with(context)
            .load("https://www.telemedocket.com/BNA/public/uploads/caseimages/img/$videoPath")
            .apply(RequestOptions().centerCrop().placeholder(R.drawable.ph_video_duotone))
            .into(videoView)
    }
}
