package org.bombayneurosciences.bna_2023.CustomPageUtils

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener

class ScaleListener(context: Context) : SimpleOnScaleGestureListener() {
    private var scaleFactor = 1.0f
    private val minScaleFactor = 0.1f
    private val maxScaleFactor = 5.0f

    private val scaleGestureDetector: ScaleGestureDetector = ScaleGestureDetector(context, this)

    fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        return true
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        scaleFactor *= detector.scaleFactor
        scaleFactor = scaleFactor.coerceIn(minScaleFactor, maxScaleFactor)

        // Apply scale to your PDF rendering view or canvas
        // Example: pdfRendererView.setScaleFactor(scaleFactor)

        return true
    }
}
