package org.bombayneurosciences.bna_2023.Fragment

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class PageCurlView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.GRAY
    }

    var curlOffset: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw a simple rectangle to visualize the curl effect
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        // Draw additional effects here to simulate curling
    }
}
