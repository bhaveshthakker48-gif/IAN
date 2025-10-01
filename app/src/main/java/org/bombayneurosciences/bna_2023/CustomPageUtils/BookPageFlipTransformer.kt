package org.bombayneurosciences.bna_2023.CustomPageUtils



import android.view.View
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max

class BookPageFlipTransformer : ViewPager2.PageTransformer {

    private val halfWidth = 0.5f

    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width.toFloat()

        // Handle pages off-screen
        if (position < -1 || position > 1) {
            page.alpha = 0f
            return
        }

        // Calculate rotation based on position
        val rotation = max(0f, abs(position) - 1) * -180f

        // Set pivot point for the flip effect (experiment with values for best result)
        val pivotX = if (position < 0) pageWidth * halfWidth else 0f

        // Apply transformations
        ViewCompat.setPivotX(page, pivotX)
        ViewCompat.setRotationY(page, rotation)

        // Adjust scale for depth effect (optional)
        page.alpha = 1 - abs(position)
        page.scaleX = 1 - abs(position) * 0.1f // Adjust factor for desired scale
    }
}
