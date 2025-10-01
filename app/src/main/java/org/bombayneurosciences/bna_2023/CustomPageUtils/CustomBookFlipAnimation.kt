package org.bombayneurosciences.bna_2023.CustomPageUtils

import android.view.View
import androidx.annotation.NonNull
import androidx.viewpager2.widget.ViewPager2

class CustomBookFlipAnimation : ViewPager2.PageTransformer {

    private val LEFT = -1
    private val RIGHT = 1
    private val CENTER = 0
    private var scaleAmountPercent = 5f
    private var enableScale = true

    override fun transformPage(@NonNull page: View, position: Float) {
        val percentage = 1 - Math.abs(position)
        // Don't move pages once they are on left or right
        if (position > CENTER && position <= RIGHT) {
            // This is behind page
            page.translationX = -position * page.width
            page.translationY = 0f
            page.rotation = 0f
            if (enableScale) {
                val amount = ((100 - scaleAmountPercent) + (scaleAmountPercent * percentage)) / 100
                setSize(page, position, amount)
            }
        } else {
            // Otherwise flip the current page
            page.visibility = View.VISIBLE
            flipPage(page, position, percentage)
        }
    }

    private fun flipPage(page: View, position: Float, percentage: Float) {
        // Flip this page
        page.cameraDistance = -12000f
        setVisibility(page, position)
        setTranslation(page)
        setPivot(page, 0f, page.height * 0.5f)
        setRotation(page, position, percentage)
    }

    private fun setPivot(page: View, pivotX: Float, pivotY: Float) {
        page.pivotX = pivotX
        page.pivotY = pivotY
    }

    private fun setVisibility(page: View, position: Float) {
        page.visibility = if (position < 0.5 && position > -0.5) View.VISIBLE else View.INVISIBLE
    }

    private fun setTranslation(page: View) {
        val viewPager = page.parent.parent as ViewPager2
        val scroll = viewPager.scrollX - page.left
        page.translationX = scroll.toFloat()
    }

    private fun setSize(page: View, position: Float, percentage: Float) {
        page.scaleX = if (position != 0f && position != 1f) percentage else 1f
        page.scaleY = if (position != 0f && position != 1f) percentage else 1f
    }

    private fun setRotation(page: View, position: Float, percentage: Float) {
        page.rotationY = if (position > 0) -180 * (percentage + 1) else 180 * (percentage + 1)
    }

    fun getScaleAmountPercent(): Float {
        return scaleAmountPercent
    }

    fun setScaleAmountPercent(scaleAmountPercent: Float) {
        this.scaleAmountPercent = scaleAmountPercent
    }

    fun isEnableScale(): Boolean {
        return enableScale
    }

    fun setEnableScale(enableScale: Boolean) {
        this.enableScale = enableScale
    }
}
