package org.bombayneurosciences.bna_2023.CustomPageUtils



import android.graphics.Path
import android.view.View
import androidx.core.view.ViewCompat


import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max

import android.graphics.Canvas
import android.graphics.*
import android.graphics.drawable.BitmapDrawable


import kotlin.math.min

class BookPageTurnTransformer1 : ViewPager2.PageTransformer {

    private val halfWidth = 0.5f
    private val cornerRadius = 20f // Adjust for desired corner curl

    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width.toFloat()
        val pageHeight = page.height.toFloat()

        // Handle pages off-screen
        if (position < -1 || position > 1) {
            page.alpha = 0f
            return
        }

        // Calculate rotation based on position
        val rotation = max(-180f, min(180f, position * 180))

        page.translationX = -position * pageWidth

        // Set pivot point for the flip effect (experiment with values for best result)
        page.pivotY = pageHeight * 0.5f

        // Determine visibility based on position
        if (position < 0.5 && position > -0.5) {
            page.visibility = View.VISIBLE
        } else {
            page.visibility = View.INVISIBLE
        }

        // Apply transformations
        page.rotationY = rotation

        // Apply corner clipping for a more realistic page turn effect
        if (position < 0) {
            val bitmap = viewToBitmap(page)
            val canvas = Canvas(bitmap)

            val clipPath = createCornerClipPath(pageWidth, pageHeight, position)
            canvas.clipPath(clipPath)

            page.background = BitmapDrawable(page.resources, bitmap)
        }
    }

    private fun createCornerClipPath(pageWidth: Float, pageHeight: Float, position: Float): Path {
        val path = Path()
        path.moveTo(0f, 0f)
        path.lineTo(pageWidth, 0f)
        path.lineTo(pageWidth, pageHeight)
        path.lineTo(0f, pageHeight)
        path.close()

        // Adjust corner clipping based on position
        val clipX = pageWidth * position
        val clipY = pageHeight * 0.5f
        path.moveTo(clipX, clipY)
        path.lineTo(pageWidth, 0f)
        path.lineTo(pageWidth, pageHeight)
        path.lineTo(clipX, pageHeight)
        path.close()

        return path
    }

    private fun viewToBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}



