package org.bombayneurosciences.bna_2023.CustomPageUtils

import android.graphics.PointF


/**
 * Class to manage and track pointer positions.
 */
class PointerPosition {
    /**
     * Get the start position of the pointer.
     *
     * @return PointF object representing the start position.
     */
    val startPosition: PointF = PointF()

    var mPos: PointF = PointF() // Assuming PointF is a class that holds x and y coordinates
    var mPressure: Float = 0f

    /**
     * Get the current position of the pointer.
     *
     * @return PointF object representing the current position.
     */
    val currentPosition: PointF = PointF()

    /**
     * Set the start position of the pointer.
     *
     * @param x X-coordinate of the start position.
     * @param y Y-coordinate of the start position.
     */
    fun setStartPosition(x: Float, y: Float) {
        startPosition[x] = y
    }

    /**
     * Update the current position of the pointer.
     *
     * @param x X-coordinate of the current position.
     * @param y Y-coordinate of the current position.
     */
    fun updateCurrentPosition(x: Float, y: Float) {
        currentPosition[x] = y
    }

    val delta: PointF
        /**
         * Calculate the difference vector between start and current positions.
         *
         * @return PointF object representing the difference vector.
         */
        get() = PointF(currentPosition.x - startPosition.x, currentPosition.y - startPosition.y)

    /**
     * Reset the pointer positions.
     */
    fun reset() {
        startPosition[0f] = 0f
        currentPosition[0f] = 0f
    }
}
