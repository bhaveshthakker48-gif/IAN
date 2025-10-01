package org.bombayneurosciences.bna_2023.CustomPageUtils



import android.view.View
import androidx.viewpager2.widget.ViewPager2

/*class PageFlipTransformer1 : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width.toFloat()

        when {
            position < -1 -> { // This page is way off-screen to the left
                page.alpha = 0f
            }
            position <= 0 -> { // This page is moving out to the left
                page.alpha = 1f
                page.translationX = 0f
                page.translationZ = 0f
                page.pivotX = 0f
                page.rotationY = -90f * Math.abs(position)
            }
            position <= 1 -> { // This page is moving in from the right
                page.alpha = 1f
                page.translationX = -pageWidth * position
                page.translationZ = -1f
                page.pivotX = pageWidth
                page.rotationY = 10f * Math.abs(position)
            }
            else -> { // This page is way off-screen to the right
                page.alpha = 0f
            }
        }
    }
}*/




class PageFlipTransformer1 : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width.toFloat()

        when {
            position < -1 -> { // This page is way off-screen to the left
                page.alpha = 0f
            }
            position <= 0 -> { // This page is moving out to the left
                page.alpha = 1f
                page.translationX = 0f
                page.translationZ = 0f
                page.pivotX = 0f
                page.rotationY = -90f * Math.abs(position)
                page.scaleX = 1f
                page.scaleY = 1f
            }
            position <= 1 -> { // This page is moving in from the right
                page.alpha = 1f
                page.translationX = -pageWidth * position
                page.translationZ = -1f
                page.pivotX = pageWidth
                page.rotationY = 90f * Math.abs(position)
                page.scaleX = 1f
                page.scaleY = 1f
            }
            else -> { // This page is way off-screen to the right
                page.alpha = 0f
            }
        }
    }
}


