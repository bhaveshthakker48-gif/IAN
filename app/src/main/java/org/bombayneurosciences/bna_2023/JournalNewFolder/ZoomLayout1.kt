package org.bombayneurosciences.bna_2023.JournalNewFolder

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.webkit.WebView
import android.widget.FrameLayout

class ZoomLayout1 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ScaleGestureDetector.OnScaleGestureListener {

    private var scaleFactor = 1.0f
    private val scaleGestureDetector = ScaleGestureDetector(context, this)

    // WebView reference to disable zooming on WebView
    var webView: WebView? = null

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            // If touch is within the bounds of the WebView, prevent intercepting the event for zoom
            webView?.let { webView ->
                val webViewRect = Rect()
                webView.getGlobalVisibleRect(webViewRect)

                if (webViewRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    // Prevent zooming when interacting with WebView
                    return false
                }
            }
        }

        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Pass touch events to ScaleGestureDetector
        scaleGestureDetector.onTouchEvent(event)
        return true
    }





    override fun onScale(detector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        detector?.let {
            // Scale factor handling (ensure limits for zoom)
            scaleFactor *= it.scaleFactor
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 4.0f)) // Min and Max zoom levels

            // Apply the scaling to this layout
            this.scaleX = scaleFactor
            this.scaleY = scaleFactor
        }
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {

    }

}
