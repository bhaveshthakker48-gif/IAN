package org.bombayneurosciences.bna_2023.Activity

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.R

class DashLineItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val paint = Paint()
    private val path = Path()


    init {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f // Adjust the dash line width as needed
        paint.color = ContextCompat.getColor(context, R.color.dashline)
        paint.pathEffect = DashPathEffect(floatArrayOf(15f, 15f), 0f) // Adjust the dash length and gap as needed
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val startX = child.left.toFloat()
            val stopX = startX

            val startY = child.top + child.translationY
            val stopY = child.bottom + child.translationY

            path.reset()
            path.moveTo(startX, startY)
            path.lineTo(stopX, stopY)

            c.drawPath(path, paint)
        }
    }
}
