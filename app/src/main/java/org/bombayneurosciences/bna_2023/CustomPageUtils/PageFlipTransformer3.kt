package org.bombayneurosciences.bna_2023.CustomPageUtils



import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class PageFlipTransformer3 : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width.toFloat()

        when {
            position < -1 -> { // This page is way off-screen to the left
                page.alpha = 0f
            }
            position <= 0 -> { // This page is moving out to the left
                page.alpha = 1f
                page.translationX = pageWidth * -position
                applyFlipEffect(page, position)
            }
            position <= 1 -> { // This page is moving in from the right
                page.alpha = 1f
                page.translationX = pageWidth * -position
                applyFlipEffect(page, position)
            }
            else -> { // This page is way off-screen to the right
                page.alpha = 0f
            }
        }
    }

    private fun applyFlipEffect(page: View, position: Float) {
        val absPosition = abs(position)
        val scale = 1f - 0.1f * absPosition
        page.scaleX = scale
        page.scaleY = scale
        page.alpha = 1f - absPosition * 0.5f
    }
}
