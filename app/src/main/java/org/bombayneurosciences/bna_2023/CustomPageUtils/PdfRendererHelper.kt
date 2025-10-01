package org.bombayneurosciences.bna_2023.CustomPageUtils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PdfRendererHelper(context: Context) {

    private val mContext: Context = context

    // Function to render PDF pages to bitmaps
    fun renderPdfToBitmaps(pdfPath: String): ArrayList<Bitmap> {
        val bitmaps = ArrayList<Bitmap>()
        var pdfRenderer: PdfRenderer? = null
        var fileDescriptor: ParcelFileDescriptor? = null

        try {
            // Open the PDF file
            fileDescriptor = ParcelFileDescriptor.open(File(pdfPath), ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(fileDescriptor)

            // Render each page to bitmap
            for (pageIndex in 0 until pdfRenderer.pageCount) {
                val page = pdfRenderer.openPage(pageIndex)
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmaps.add(bitmap)
                page.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                pdfRenderer?.close()
                fileDescriptor?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return bitmaps
    }
}
