package org.bombayneurosciences.bna_2023

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Matrix
import android.graphics.PointF
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.bombayneurosciences.bna_2023.Activity.CaseOfMonthActivity
import org.bombayneurosciences.bna_2023.Activity.CaseOfMonthActivity2
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp

class ImageViewverCaseOfMonth : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnCancel: ImageButton

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var matrix = Matrix()
    private var lastEvent: MotionEvent? = null
    private var startPoint = PointF()

    private val handler = Handler()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewver_case_of_month)

        // Retrieve the imagepath from the intent
        val imagePath = intent.getStringExtra("imagepath")

        // Now you can use 'imagePath' to do something with the path, for example, load an image
        Log.d(ConstanstsApp.tag, "Received imagepath in ImageViewerCaseOfMonth: $imagePath")


        imageView = findViewById(R.id.imageView)
        progressBar = findViewById(R.id.progressBar)
        btnCancel = findViewById(R.id.btnCancel)
        // Set up cancel button click listener
        btnCancel.setOnClickListener {
            // Handle cancel button click (finish the activity)
            finish()
        }

        // Set a click listener for the transparent button
        val transparentButton: Button = findViewById(R.id.backButton)
        transparentButton.setOnClickListener {
            onBackButtonClick(it)
        }


        Glide.with(this)
            .load("https://www.telemedocket.com/BNA/public/uploads/caseimages/img/$imagePath")
            .apply(RequestOptions().centerCrop())
            .into(imageView)

        imageView.setOnTouchListener(touchListener)
//        imageView.setOnClickListener {
//            Log.d(ConstanstsApp.tag,"clicked image")
//            // Handle the click event (e.g., go back)
//            val intent = Intent(this, CaseOfMonthActivity2::class.java)
//            startActivity(intent)
//        }
        imageView.setOnClickListener {
            //            Log.d(ConstanstsApp.tag,"clicked image")
            val intent = Intent(this, CaseOfMonthActivity2::class.java)
            startActivity(intent)
        }
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
    }

     fun onBackButtonClick(it: View?) {
         onBackPressed()
    }



    private val touchListener = View.OnTouchListener { _, event ->
        scaleGestureDetector.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastEvent = MotionEvent.obtain(event)
                startPoint.set(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.x - startPoint.x
                val deltaY = event.y - startPoint.y

                matrix.postTranslate(deltaX, deltaY)
                startPoint.set(event.x, event.y)
            }
        }

        imageView.imageMatrix = matrix

        true
    }

    /*    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                // Show progress bar when zoom begins
                handler.post { progressBar.visibility = View.VISIBLE }
                return super.onScaleBegin(detector)
            }

            override fun onScaleEnd(detector: ScaleGestureDetector?) {
                // Hide progress bar when zoom ends
                handler.postDelayed({ progressBar.visibility = View.GONE }, 500)
                super.onScaleEnd(detector)
            }

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                matrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                imageView.imageMatrix = matrix
                return true
            }
        }*/


    /* inner class ScaleListener:ScaleGestureDetector.SimpleOnScaleGestureListener()
     {
         override fun onScale(detector: ScaleGestureDetector): Boolean {
            // return super.onScale(detector)

             val scaleFactor = detector.scaleFactor
             matrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
             imageView.imageMatrix = matrix
             return true
         }

         override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
           //  return super.onScaleBegin(detector)

             // Show progress bar when zoom begins
             handler.post { progressBar.visibility = View.VISIBLE }
             return super.onScaleBegin(detector)
         }

         override fun onScaleEnd(detector: ScaleGestureDetector) {
             //super.onScaleEnd(detector)

             // Hide progress bar when zoom ends
             handler.postDelayed({ progressBar.visibility = View.GONE }, 500)
             super.onScaleEnd(detector)


         }
     }*/


    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            matrix.postScale(scaleFactor, scaleFactor, imageView.width / 2f, imageView.height / 2f)

            // Get the current image dimensions
            val values = FloatArray(9)
            matrix.getValues(values)
            val transX = values[Matrix.MTRANS_X]
            val transY = values[Matrix.MTRANS_Y]
            val imageWidth = imageView.drawable.intrinsicWidth * values[Matrix.MSCALE_X]
            val imageHeight = imageView.drawable.intrinsicHeight * values[Matrix.MSCALE_Y]

            // Adjust translation to keep the image centered
            val deltaX = if (imageWidth < imageView.width) {
                (imageView.width - imageWidth) / 2 - transX
            } else {
                0f
            }

            val deltaY = if (imageHeight < imageView.height) {
                (imageView.height - imageHeight) / 2 - transY
            } else {
                0f
            }

            matrix.postTranslate(deltaX, deltaY)
            imageView.imageMatrix = matrix

            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            // Show progress bar when zoom begins
            handler.post { progressBar.visibility = View.VISIBLE }
            return super.onScaleBegin(detector)
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            // Hide progress bar when zoom ends
            handler.postDelayed({ progressBar.visibility = View.GONE }, 500)
            super.onScaleEnd(detector)
        }
    }


}
