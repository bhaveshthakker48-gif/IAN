package org.bombayneurosciences.bna_2023.CustomPageUtils

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import java.lang.Math.abs

class PageTurnTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width.toFloat()
        val pageHeight = page.height.toFloat()

        if (position < -1) { // Page is way off-screen to the left
            page.alpha = 0f
        } else if (position <= 0) { // Page is moving out to the right
            page.alpha = 1f
            page.translationX = -pageWidth * position
            // Apply some rotation for a page turn effect (optional)
            page.rotationY = -90 * abs(position)
        } else if (position <= 1) { // Page is centered or moving in to the left
            page.alpha = 1f
            // Move the incoming page to the right initially
            page.translationX = pageWidth * (1 - position)
            // Apply some rotation for a page turn effect (optional)
            page.rotationY = 90 * abs(position)
        } else { // Page is way off-screen to the right
            page.alpha = 0f
        }
    }
}
