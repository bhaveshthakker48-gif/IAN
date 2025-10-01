package org.bombayneurosciences.bna_2023.Activity

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class MatrixImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val matrix = Matrix()

    init {
        scaleType = ScaleType.MATRIX
    }

    override fun setImageMatrix(matrix: Matrix?) {
        super.setImageMatrix(matrix)
        this.matrix.set(matrix)
    }

    fun setMatrix(matrix: Matrix) {
        this.matrix.set(matrix)
        imageMatrix = matrix
        invalidate()
    }
}
