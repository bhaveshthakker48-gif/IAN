package org.bombayneurosciences.bna_2023.Activity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathEffect
import android.graphics.DashPathEffect
import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.R

class DottedLineItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val paint: Paint = Paint()
    private val path: Path = Path()
    private val dotRadius: Float = context.resources.getDimensionPixelSize(R.dimen.dot_radius).toFloat()

    init {
        paint.color = Color.BLACK // Set your desired color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f // Adjust the stroke width as needed
        paint.pathEffect = DashPathEffect(floatArrayOf(20f, 10f), 0f) // Adjust the dash effect as needed
    }

    override fun onDraw(@NonNull c: Canvas, @NonNull parent: RecyclerView, @NonNull state: RecyclerView.State) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val position = parent.getChildAdapterPosition(child)
            if (position != RecyclerView.NO_POSITION) {
                val left = child.left.toFloat() + child.translationX
                val top = child.top.toFloat() + child.translationY + child.height / 2
                val right = child.left.toFloat() + child.translationX
                val bottom = top

                // Draw the dotted line path
                path.reset()
                path.moveTo(left, top)
                path.lineTo(right, bottom)
                c.drawPath(path, paint)
            }
        }
    }

    override fun getItemOffsets(@NonNull outRect: android.graphics.Rect, @NonNull view: View, @NonNull parent: RecyclerView, @NonNull state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
    }
}
