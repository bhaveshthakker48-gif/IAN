package org.bombayneurosciences.bna_2023.CustomPageUtils

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent




import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.bombayneurosciences.bna_2023.R


class ZoomCurlView : CurlView, CurlView.PageProvider, CurlView.SizeChangedObserver {

    private var currentPage = 0
    private val MIN_ZOOM_DIST = 10f
    private var prevDist = 0f
    private var zooming = false
    private var isAnimating = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    prevDist = getDist(event)
                    zooming = true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (zooming && event.pointerCount > 1) {
                    val dist = getDist(event)
                    val delta = dist - prevDist
                    if (Math.abs(delta) > MIN_ZOOM_DIST) {
                        if (delta > 0) {
                            // Zoom in
                            // Implement your zoom in logic here if needed
                        } else {
                            // Zoom out
                            // Implement your zoom out logic here if needed
                        }
                        prevDist = dist
                    }
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                if (event.pointerCount == 2) {
                    zooming = false
                }
            }
        }

        // If zooming, do not handle the event for page flipping
        return if (zooming) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    private fun getDist(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    override fun getPageCount(): Int {
        // Implement this method to return the total number of pages
        // Example:
        // return getPageProvider().getPageCount()
        return 10 // Placeholder value, replace with actual implementation
    }

    override fun onSizeChanged(newWidth: Int, newHeight: Int) {
        // Handle size changed event
    }

    private fun curlForward() {
        val pageCount = getPageCount()
        if (!isAnimating && currentPage < pageCount - 1) {
            setViewMode(SHOW_ONE_PAGE)
            setMargins(0.1f, 0.1f, 0.1f, 0.1f)
            currentPage++
            setCurrentIndex(currentPage)
        }
    }

    private fun curlBackward() {
        if (!isAnimating && currentPage > 0) {
            setViewMode(SHOW_ONE_PAGE)
            setMargins(0.1f, 0.1f, 0.1f, 0.1f)
            currentPage--
            setCurrentIndex(currentPage)
        }
    }

    override fun updatePage(page: CurlPage?, width: Int, height: Int, index: Int) {
        if (page == null) return

        // Load the bitmap for the given index (this is just an example, adjust as needed)
        val bitmap = getBitmapForIndex(index)

        // Set the bitmap as the front side of the page
        page.setTexture(bitmap, CurlPage.SIDE_FRONT)
    }

    private fun getBitmapForIndex(index: Int): Bitmap {
        // Replace this with your logic to load the bitmap for the given index
        // For example, you could load images from resources or files
        val resourceId = when (index) {
            0 -> R.drawable.baseline_30fps_24
            1 -> R.drawable.baseline_30fps_24
            2 -> R.drawable.baseline_30fps_24
            // Add cases for other pages
            else -> R.drawable.baseline_30fps_24 // Default page
        }
        return BitmapFactory.decodeResource(resources, resourceId)
    }
}


