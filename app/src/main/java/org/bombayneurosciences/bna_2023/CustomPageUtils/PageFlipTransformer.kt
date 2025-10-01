package org.bombayneurosciences.bna_2023.CustomPageUtils


import android.view.View
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

class PageFlipTransformer : ViewPager2.PageTransformer {


    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width
        val pageHeight = page.height

        when {
            position < -1 -> {
                page.alpha = 0f
            }
            position <= 1 -> {
                val scaleFactor = Math.max(0.7f, 1 - Math.abs(position))
                val rotation = Math.abs(position) * 30f
                page.alpha = scaleFactor
                page.pivotX = pageWidth / 2f
                page.pivotY = pageHeight / 2f
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
                page.rotationY = rotation
            }
            else -> {
                page.alpha = 0f
            }
        }
    }

   /* override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width
        val pageHeight = page.height

        when {
            position < -1 -> { // Page is off-screen to the left
                page.alpha = 0f
            }
            position <= 0 -> { // Page is moving to the left
                page.alpha = 1f
                page.translationX = 0f
                page.rotationY = 180 * position
                page.pivotX = pageWidth * 0.5f
                page.pivotY = pageHeight * 0.5f
                page.scaleX = 1f
                page.scaleY = 1f
            }
            position <= 1 -> { // Page is moving to the right
                page.alpha = 1f
                page.translationX = -pageWidth * position
                page.rotationY = 180 * position
                page.pivotX = pageWidth * 0f
                page.pivotY = pageHeight * 0.5f
                page.scaleX = 1f
                page.scaleY = 1f
            }
            else -> { // Page is off-screen to the right
                page.alpha = 0f
            }
        }
    }

*/

/*
    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width

        when {
            position < -1 -> { // This page is way off-screen to the left
                page.alpha = 0f
            }
            position <= 0 -> { // This page is moving out to the left
                page.alpha = 1f
                page.translationX = 0f
                page.translationZ = 0f
                page.pivotX = 0f
                page.rotationY = 90f * Math.abs(position)
            }
            position <= 1 -> { // This page is moving in from the right
                page.alpha = 2f
                page.translationX = pageWidth * -position
                page.translationZ = -1f
                page.pivotX = pageWidth.toFloat()
                page.rotationY = -90f * Math.abs(position)
            }
            else -> { // This page is way off-screen to the right
                page.alpha = 0f
            }
        }
    }*/
}