package org.bombayneurosciences.bna_2023.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import org.bombayneurosciences.bna_2023.Activity.ImagePagerAdapter
import org.bombayneurosciences.bna_2023.Activity.VideoPlaybackActivity
import org.bombayneurosciences.bna_2023.Model.CaseofMonth.Media
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp

class MediaAdapter1(private val context: Context, private val mediaList: List<Media>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var totalImageCount = 0

    companion object {
        const val TYPE_IMAGE = 1
        const val TYPE_VIDEO = 2
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val mediaLabelTextView: TextView = itemView.findViewById(R.id.mediaLabelTextView)
        val imageNumberTextView: TextView = itemView.findViewById(R.id.imageNumberTextView)

        init {

            imageView.setOnClickListener {

                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentMedia = mediaList[position]
                    onClick(
                        imageView,
                        "https://www.telemedocket.com/BNA/public/uploads/caseimages/img/${currentMedia.imagename}",
                        currentMedia.imagelable.toString(),
                        currentMedia.imageno?.toString() ?: "N/A",
                        mediaList.size,
                        position
                    )
                }



            }
        }
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnailImageView: ImageView = itemView.findViewById(R.id.thumbnailImageView)
        val mediaLabelTextView: TextView = itemView.findViewById(R.id.mediaLabelTextView)

        private var exoPlayer: SimpleExoPlayer? = null

        init {
            thumbnailImageView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentMedia = mediaList[position]
                    // Check for network availability
                    if (isOnline()) {
                        playVideo(currentMedia)
                    } else {
                        // Show an alert or message to the user indicating no internet connection
                        showNoInternetAlert("Please connect to the internet")
                    }
                }
            }


        }


//        fun playVideo(media: Media) {
//            releasePlayer()
//
//            exoPlayer = SimpleExoPlayer.Builder(context).build()
//            playerView.player = exoPlayer
//
//            val mediaItem = MediaItem.fromUri("https://www.telemedocket.com/BNA/public/uploads/caseimages/video/${media.imagename}")
//            exoPlayer?.setMediaItem(mediaItem)
//            exoPlayer?.prepare()
//            exoPlayer?.play()
//        }
//
//        private fun releasePlayer() {
//            exoPlayer?.release()
//            exoPlayer = null
//        }
    }
    private fun showNoInternetAlert(message:String) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.intenet_alert)

        // Find views in the custom dialog layout
        val notificationTitle: TextView = dialog.findViewById(R.id.notificationTitle)
        val textViewLogoutConfirmation: TextView = dialog.findViewById(R.id.textViewLogoutConfirmation)
        val buttonYes: Button = dialog.findViewById(R.id.buttonYes)
        val buttonNo: Button = dialog.findViewById(R.id.buttonNo)

        // Set notification title and confirmation text
        notificationTitle.text = "Connectivity Issue"
        textViewLogoutConfirmation.text = message

        // Set click listener for the Yes button
        buttonYes.setOnClickListener {
            // Handle the Yes button click event
            dialog.dismiss()
        }

        // Set click listener for the No button
        buttonNo.setOnClickListener {
            // Handle the No button click event
            dialog.dismiss()
        }

        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
    }



    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun playVideo(currentMedia: Media) {
        // Assuming VideoPlaybackActivity is the activity where you want to play the video
        val intent = Intent(context, VideoPlaybackActivity::class.java)

        // Pass necessary information to VideoPlaybackActivity
        intent.putExtra("videoUrl", "https://www.telemedocket.com/BNA/public/uploads/caseimages/video/${currentMedia.imagename}")
        intent.putExtra("videoLabel", currentMedia.imagelable)

        // Start VideoPlaybackActivity
        context.startActivity(intent)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_IMAGE -> {
                val itemView = LayoutInflater.from(context).inflate(R.layout.media_images, parent, false)
                ImageViewHolder(itemView)
            }
            TYPE_VIDEO -> {
                val itemView = LayoutInflater.from(context).inflate(R.layout.media_video, parent, false)
                VideoViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMedia = mediaList[position]

        when (holder) {
            is ImageViewHolder -> {
                // Handle Image
                Glide.with(context)
                    .load("https://www.telemedocket.com/BNA/public/uploads/caseimages/img/${currentMedia.imagename}")
                    .into(holder.imageView)
//                holder.mediaLabelTextView.text = currentMedia.imagelable

                val truncatedText = if (currentMedia.imagelable?.length!! > 11) "${currentMedia.imagelable.substring(0, 10)}..." else currentMedia.imagelable
                holder.mediaLabelTextView.text = truncatedText.toString()
                // Calculate total image count and set the label
                val currentImagePosition = position + 1
                holder.imageNumberTextView.text = "$currentImagePosition/$totalImageCount"

                Log.d("mytag", "total image count: $currentImagePosition/$totalImageCount")
                Log.e("mytag", "total image count: ${currentMedia.imagename}")


                holder.itemView.setOnClickListener {
                    // Handle image item click
                    val videoHolder = VideoViewHolder(
                        LayoutInflater.from(context).inflate(R.layout.media_video, null)
                    )
                  //  videoHolder.playVideo(currentMedia)
                }
                holder.itemView.setOnClickListener {
                    // Handle image item click
                }
            }

            is VideoViewHolder -> {

                holder.mediaLabelTextView.text = currentMedia.imagelable
                holder.itemView.setOnClickListener {
                    // Handle the click event for the video item if needed
                    // You can add your logic here
                }
               // holder.playVideo(currentMedia)


            }
        }
    }

    // MediaAdapter1.kt
    fun onClick(view: View, imageUrl: String, imageLabel: String, imageNumber: String, totalImageCount: Int, currentPosition: Int) {
        if (mediaList[currentPosition].imagetype == "Video") {
            val intent = Intent(context, VideoPlaybackActivity::class.java)
            intent.putExtra("videoPath", "https://www.telemedocket.com/BNA/public/uploads/caseimages/video/${mediaList[currentPosition].imagename}")
            context.startActivity(intent)
        } else {
            showImagePopup(imageUrl, imageLabel, imageNumber, totalImageCount, currentPosition)
        }
    }

    private fun showImagePopup(imageUrl: String, imageLabel: String, imageNumber: String, totalImageCount: Int, currentPosition: Int) {
        val popupView = LayoutInflater.from(context).inflate(R.layout.imageview_popup, null)

        val viewPagerImages: ViewPager2 = popupView.findViewById(R.id.imageViewPopup)
        val imageNumberTextView: TextView = popupView.findViewById(R.id.imageNumberTextView)
        val leftArrowButton: ImageButton = popupView.findViewById(R.id.leftArrowButton)
        val rightArrowButton: ImageButton = popupView.findViewById(R.id.rightArrowButton)
        val imageLabelTextView: TextView = popupView.findViewById(R.id.imageLabelTextView)

        val imageUrls = mediaList.map { "https://www.telemedocket.com/BNA/public/uploads/caseimages/img/${it.imagename}" }
        val imagePagerAdapter = ImagePagerAdapter(context, imageUrls)
        viewPagerImages.adapter = imagePagerAdapter
        viewPagerImages.currentItem = currentPosition

        // Update image number text
        imageNumberTextView.text = "${currentPosition + 1}/$totalImageCount"
        imageLabelTextView.text = imageLabel

        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(popupView)
        dialog.window?.setBackgroundDrawableResource(R.color.transparentGlass2)

        // Set an OnClickListener to dismiss the popup when clicked outside the image
        popupView.setOnClickListener {
            dialog.dismiss()
        }

        // Handle arrow button clicks
        leftArrowButton.setOnClickListener {
            if (viewPagerImages.currentItem > 0) {
                viewPagerImages.currentItem -= 1
                imageNumberTextView.text = "${viewPagerImages.currentItem + 1}/$totalImageCount"
                imageLabelTextView.text = mediaList[viewPagerImages.currentItem].imagelable.toString()

            }
        }

        rightArrowButton.setOnClickListener {
            if (viewPagerImages.currentItem < imageUrls.size - 1) {
                viewPagerImages.currentItem += 1
                imageNumberTextView.text = "${viewPagerImages.currentItem + 1}/$totalImageCount"
                imageLabelTextView.text = mediaList[viewPagerImages.currentItem].imagelable.toString()
            }
        }

        // Update image number when page changes
        viewPagerImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                imageNumberTextView.text = "${position + 1}/$totalImageCount"
                imageLabelTextView.text =  mediaList[position].imagelable.toString()

            }
        })

        dialog.show()
    }


  /*  private fun showImagePopup(imageUrl: String, imageLabel: String, imageNumber: String, totalImageCount: Int, currentPosition: Int) {


        val popupView = LayoutInflater.from(context).inflate(R.layout.imageview_popup, null)

        val imageViewPopup: ImageView = popupView.findViewById(R.id.imageViewPopup)
        val imageLabelTextView: TextView = popupView.findViewById(R.id.imageLabelTextView)
        val imageNumberTextView: TextView = popupView.findViewById(R.id.imageNumberTextView)

        val leftArrowButton: ImageButton = popupView.findViewById(R.id.leftArrowButton)
        val rightArrowButton: ImageButton = popupView.findViewById(R.id.rightArrowButton)

        // Load the image into the ImageView using Glide or any other image loading library
        Glide.with(context)
            .load(imageUrl)
            .into(imageViewPopup)

        // Set the image label
        imageLabelTextView.text = imageLabel
        imageNumberTextView.text = imageNumber

        imageNumberTextView.visibility = View.VISIBLE
        imageNumberTextView.text = "${currentPosition + 1}/$totalImageCount"


        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(popupView)
        dialog.window?.setBackgroundDrawableResource(R.color.transparentGlass2)

        // Set an OnClickListener to dismiss the popup when clicked outside the image
        popupView.setOnClickListener {
            dialog.dismiss()
        }


        // Handle arrow button clicks
        leftArrowButton.setOnClickListener {
            if (currentPosition > 0) {
                val previousPosition = currentPosition - 1
                showImagePopup(
                    "https://www.telemedocket.com/BNA/public/uploads/caseimages/img/${mediaList[previousPosition].imagename}",
                    mediaList[previousPosition].imagelable.toString(),
                    (previousPosition + 1).toString(),
                    totalImageCount,
                    previousPosition
                )
                dialog.dismiss()
            }
        }

        rightArrowButton.setOnClickListener {
            if (currentPosition < mediaList.size - 1) {
                val nextPosition = currentPosition + 1
                showImagePopup(
                    "https://www.telemedocket.com/BNA/public/uploads/caseimages/img/${mediaList[nextPosition].imagename}",
                    mediaList[nextPosition].imagelable.toString(),
                    (nextPosition + 1).toString(),
                    totalImageCount,
                    nextPosition
                )
                dialog.dismiss()
            }
        }



        dialog.show()
        // Add pinch-to-zoom functionality
        addPinchZoomListener(imageViewPopup)




    }

*/


    private fun addPinchZoomListener(imageView: ImageView) {
        val scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            var scaleFactor = 1.0f

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                imageView.scaleX = scaleFactor
                imageView.scaleY = scaleFactor
                return true
            }
        })

        imageView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mediaList[position].imagetype == "Video") TYPE_VIDEO else TYPE_IMAGE
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }
}
