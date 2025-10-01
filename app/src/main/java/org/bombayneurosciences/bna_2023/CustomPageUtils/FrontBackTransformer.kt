package org.bombayneurosciences.bna_2023.CustomPageUtils

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import java.lang.Math.abs

class FrontBackTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        if (position < -1 || position > 1) {
            page.alpha = 0f
        } else {
            val scaleFactor = 1 - abs(position)
            page.alpha = scaleFactor
            // Move the pages slightly for depth effect
            page.translationX = page.width * position * -0.25f
            // Scale the pages for front and back effect
            page.scaleX = scaleFactor
            page.scaleY = scaleFactor
        }
    }
}
