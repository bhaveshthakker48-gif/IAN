package org.bombayneurosciences.bna_2023
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator

public class PageCurlView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private var curlProgress = 0f // 0.0f to 1.0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawPageCurlEffect(canvas)
    }

    private fun drawPageCurlEffect(canvas: Canvas) {
        // Placeholder code for drawing page curl effect
        val width = width.toFloat()
        val height = height.toFloat()

        // Draw background
        canvas.drawColor(Color.GRAY)

        // Draw page curl (for simplicity, we're drawing a diagonal line as an effect)
        if (curlProgress > 0f) {
            paint.color = Color.LTGRAY
            canvas.drawRect(0f, 0f, width * curlProgress, height, paint)
        }
    }

    fun setCurlProgress(progress: Float) {
        curlProgress = progress
        invalidate() // Redraw the view
    }

    fun animatePageCurl() {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 600
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            setCurlProgress(value)
        }
        animator.start()
    }
}
