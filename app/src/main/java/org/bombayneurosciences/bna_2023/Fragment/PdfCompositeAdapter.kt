package org.bombayneurosciences.bna_2023.Fragment


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.R
import java.io.File


class PdfCompositeAdapter(
    private val context: Context,
    pdfFiles: List<File?>,
    private val startPosition: Int
) :
    RecyclerView.Adapter<PdfCompositeAdapter.PdfViewHolder>() {
    private val pdfRenderers: MutableList<PdfRenderer> = ArrayList()
    private val pageCounts: MutableList<Int> = ArrayList()
    private val bitmaps: MutableList<Array<Bitmap?>> =
        ArrayList()

    init {
        for (pdfFile in pdfFiles) {
            val fileDescriptor =
                ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(fileDescriptor)
            val pageCount = pdfRenderer.pageCount
            pdfRenderers.add(pdfRenderer)
            pageCounts.add(pageCount)
            bitmaps.add(arrayOfNulls(pageCount))
            Log.e("hhhhhhhhhhh",""+pdfFile)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.pdf_composite_page_layout, parent, false)
        return PdfViewHolder(view)
    }

   /* override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {

        resetPageState(holder.itemView)

        var cumulativePageCount = 0
        var pdfIndex = 0
        for (i in pageCounts.indices) {
            if (position < cumulativePageCount + pageCounts[i]) {
                pdfIndex = i
                break
            }
            cumulativePageCount += pageCounts[i]
        }

        val pageIndex = position - cumulativePageCount
        if (bitmaps[pdfIndex][pageIndex] == null) {
            val pdfRenderer = pdfRenderers[pdfIndex]
            val currentPage = pdfRenderer.openPage(pageIndex)
            val bitmap =
                Bitmap.createBitmap(currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888)
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            bitmaps[pdfIndex][pageIndex] = bitmap
            currentPage.close()
        }

       *//* if (bitmaps[pdfIndex][pageIndex] == null) {
            val pdfRenderer = pdfRenderers[pdfIndex]
            val currentPage = pdfRenderer.openPage(pageIndex)
            val bitmap = Bitmap.createBitmap(
                currentPage.width,
                currentPage.height,
                Bitmap.Config.ARGB_8888
            )
            bitmap.eraseColor(Color.WHITE)  // Fill the bitmap with white color
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            bitmaps[pdfIndex][pageIndex] = bitmap
            currentPage.close()
        }*//*
        holder.imageView.setImageBitmap(bitmaps[pdfIndex][pageIndex])



    }*/

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        resetPageState(holder.itemView)

        var cumulativePageCount = 0
        var pdfIndex = 0
        for (i in pageCounts.indices) {
            if (position < cumulativePageCount + pageCounts[i]) {
                pdfIndex = i
                break
            }
            cumulativePageCount += pageCounts[i]
        }

        val pageIndex = position - cumulativePageCount
        if (bitmaps[pdfIndex][pageIndex] == null) {
            val pdfRenderer = pdfRenderers[pdfIndex]
            val currentPage = pdfRenderer.openPage(pageIndex)

            // Calculate a suitable bitmap size for HD quality
            val displayMetrics = context.resources.displayMetrics
            val screenWidthPixels = displayMetrics.widthPixels
            val pageWidth = currentPage.width
            val pageHeight = currentPage.height
            val bitmapWidth = screenWidthPixels  // Adjust size as needed for optimal display
            val scale = bitmapWidth.toFloat() / pageWidth.toFloat()
            val bitmapHeight = (pageHeight.toFloat() * scale).toInt()

            val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(Color.WHITE)
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            bitmaps[pdfIndex][pageIndex] = bitmap




            currentPage.close()
        }

        holder.imageView.setImageBitmap(bitmaps[pdfIndex][pageIndex])
    }


    override fun getItemCount(): Int {
        var total = 0
        for (count in pageCounts) {
            total += count
        }
        return total
    }

    override fun onViewRecycled(holder: PdfViewHolder) {
        super.onViewRecycled(holder)
        holder.imageView.setImageBitmap(null)
    }

    class PdfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById<ImageView>(R.id.imageView)
    }

    private fun resetPageState(view: View) {
        // Reset text views


        // Reset image views
        val imageView1: ImageView = view.findViewById(R.id.imageView)
        imageView1.setImageResource(0) // or set to a default image resource

        // Reset other UI elements as needed
        // For example:
        // val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        // progressBar.visibility = View.GONE

        // Additional data binding or view resetting logic here
    }

}
