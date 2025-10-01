package org.bombayneurosciences.bna_2023.CustomPageUtils

import android.graphics.Bitmap


class MyPageProvider(// List of bitmaps for each page
    private val pageBitmaps: List<Bitmap>
) : PageProvider {
    override val pageCount: Int
        get() = pageBitmaps.size

    override fun updatePage(page: CurlPage?, index: Int) {

    }

    override fun updatePage(page: CurlPage?, width: Int, height: Int, index: Int) {
        if (index >= 0 && index < pageBitmaps.size) {
            val bitmap = pageBitmaps[index]
            page!!.setTexture(bitmap, CurlPage.SIDE_BOTH) // Set bitmap for both sides of the page
        }
    }


}
