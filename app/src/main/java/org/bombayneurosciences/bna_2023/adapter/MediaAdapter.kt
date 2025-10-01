package org.bombayneurosciences.bna_2023.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import org.bombayneurosciences.bna_2023.Model.CaseofMonth.MediaItem
import org.bombayneurosciences.bna_2023.Model.CaseofMonth.MediaType
import org.bombayneurosciences.bna_2023.R

import android.util.Log

import android.widget.ImageView

import com.bumptech.glide.request.RequestOptions
import org.bombayneurosciences.bna_2023.CallBack.CaseOfMonthInterface.onImageClickCaseOfMonth
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp

class MediaAdapter(private val context: Context, private val mediaItems: List<MediaItem>,
                   private val onImageClickCaseOfMonthClickListener: onImageClickCaseOfMonth? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MediaType.P_IMAGE.ordinal -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_media, parent, false)
                ImageViewHolder(view)
            }
            MediaType.R_IMAGE.ordinal -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_media, parent, false)
                ImageViewHolder(view)
            }
            MediaType.VIDEO.ordinal -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false)
               // val view = LayoutInflater.from(context).inflate(R.layout.item_media, parent, false)
                VideoViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            MediaType.P_IMAGE.ordinal -> {
                val imageHolder = holder as ImageViewHolder
                loadImage(mediaItems[position].path, imageHolder.imageView)


                imageHolder.imageView.setOnClickListener {
                    onImageClickCaseOfMonthClickListener!!.onImageCaseItemClick(holder.adapterPosition,mediaItems[position].path,mediaItems[position].type.toString())
                }
            }
            MediaType.R_IMAGE.ordinal -> {
                val imageHolder = holder as ImageViewHolder
                loadImage(mediaItems[position].path, imageHolder.imageView)
                imageHolder.imageView.setOnClickListener {
                    onImageClickCaseOfMonthClickListener!!.onImageCaseItemClick(holder.adapterPosition,mediaItems[position].path,mediaItems[position].type.toString())
                }
            }
            MediaType.VIDEO.ordinal -> {
                val videoHolder = holder as VideoViewHolder
                loadVideo(mediaItems[position].path, videoHolder.videoView)

                videoHolder.videoView.setOnClickListener {
                    onImageClickCaseOfMonthClickListener!!.onImageCaseItemClick(holder.adapterPosition,mediaItems[position].path,mediaItems[position].type.toString())
                }

            }
        }
    }

    override fun getItemCount(): Int = mediaItems.size

    override fun getItemViewType(position: Int): Int {
        return mediaItems[position].type.ordinal
    }

    private fun loadImage(imagePath: String, imageView: ImageView) {
        Log.d(ConstanstsApp.tag,"imagepath=>"+"https://www.telemedocket.com/BNA/public/uploads/caseimages/img/$imagePath")
        Glide.with(context)
            .load("https://www.telemedocket.com/BNA/public/uploads/caseimages/img/$imagePath")
            .apply(RequestOptions().centerCrop())
            .into(imageView)

    }

    /*private fun loadVideo(videoPath: String, videoView: VideoView) {
        val videoUri = Uri.parse("https://www.telemedocket.com/BNA/public/uploads/caseimages/video/$videoPath")
        videoView.setVideoURI(videoUri)
    }
*/
    private fun loadVideo(videoPath: String, videoView: ImageView) {


      /*  Glide.with(context)
            .load("https://www.telemedocket.com/BNA/public/uploads/caseimages/img/$videoPath")
            .apply(RequestOptions().centerCrop())
            .into(videoView)*/

        Glide.with(context)
            .load("https://www.telemedocket.com/BNA/public/uploads/caseimages/img/$videoPath")
            .apply(RequestOptions().centerCrop().placeholder(R.drawable.ph_video_duotone))
            .into(videoView)
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView_media)
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val videoView: VideoView = itemView.findViewById(R.id.videoView)
        val videoView: ImageView = itemView.findViewById(R.id.videoView)
    }
}

