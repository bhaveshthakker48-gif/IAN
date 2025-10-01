package org.bombayneurosciences.bna_2023.CustomPageUtils

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.MotionEvent
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

import android.util.AttributeSet


class PDFView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var pdfFile: File? = null
    private var currentPage = 0
    private var pageCount = 0
    private var startX = 0f
    private var startY = 0f
    private var isTurning = false

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
    }

    fun loadPdf(pdfFileName: String) {
        val file = File(pdfFileName)

        if (!file.exists()) {
            throw IOException("File not found: $pdfFileName")
        }

        try {
            // Assign the file to the pdfFile variable
            pdfFile = file

            // Open the PDF file and get the page count
            val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(fileDescriptor)
            pageCount = pdfRenderer.pageCount

            // Clean up resources
            pdfRenderer.close()
            fileDescriptor.close()
        } catch (e: IOException) {
            // Handle IOException
            e.printStackTrace()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isTurning && startX < width / 4 && event.x > width / 2) {
                    val dx = event.x - startX
                    val absDx = Math.abs(dx)
                    val threshold = width / 10 // Adjust this value to make the gesture more sensitive
                    if (absDx > threshold) {
                        isTurning = true
                        // Determine the direction of the swipe
                        val nextPage = if (dx > 0) currentPage + 1 else currentPage - 1
                        // Ensure nextPage is within bounds
                        currentPage = nextPage.coerceIn(0, pageCount - 1)
                        invalidate()
                        startX = event.x // Update startX for smooth page turning
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isTurning) {
                    isTurning = false
                    // Update the currentPage variable here
                    currentPage = (currentPage + 1).coerceAtMost(pageCount - 1)
                    invalidate()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (currentPage < pageCount) {
            // Open the PDF file and render the current page onto a bitmap
            val renderer = PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))
            val page = renderer.openPage(currentPage)

            // Create a bitmap to render the PDF page onto
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val bitmapCanvas = Canvas(bitmap)
            bitmapCanvas.drawColor(Color.WHITE) // Set background color if needed

            // Render the PDF page onto the bitmap
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            // Draw the bitmap onto the canvas
            canvas.drawBitmap(bitmap, 0f, 0f, null)

            page.close()
            renderer.close()
        }
    }
}
