package org.bombayneurosciences.bna_2023.CustomPageUtils



import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class PageFlipTransformer2 : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width.toFloat()

        when {
            position < -1 -> { // This page is way off-screen to the left
                page.alpha = 0f
            }
            position <= 0 -> { // This page is moving out to the left
                page.alpha = 1f
                page.pivotX = pageWidth * 0.5f
                page.pivotY = page.height * 0.5f
                page.rotationY = 180 * (1 - abs(position) + 1)
                page.translationX = pageWidth * position
                page.scaleX = 1f
                page.scaleY = 1f
                applyFlipEffect(page, position)
            }
            position <= 1 -> { // This page is moving in from the right
                page.alpha = 1f
                page.pivotX = pageWidth * 0.5f
                page.pivotY = page.height * 0.5f
                page.rotationY = -180 * (1 - abs(position) + 1)
                page.translationX = pageWidth * position
                page.scaleX = 1f
                page.scaleY = 1f
                applyFlipEffect(page, position)
            }
            else -> { // This page is way off-screen to the right
                page.alpha = 0f
            }
        }
    }

    private fun applyFlipEffect(page: View, position: Float) {
        val absPosition = abs(position)
        val scale = 1f - 0.25f * absPosition
        page.scaleX = scale
        page.scaleY = scale
        page.alpha = 1f - absPosition
        val rotation = if (position < 0) 180 * absPosition else -180 * absPosition
        page.rotationY = rotation
        if (absPosition < 0.5) {
            page.translationZ = 1f
        } else {
            page.translationZ = -1f
        }
    }
}
