package org.bombayneurosciences.bna_2023.Fragment



import android.annotation.SuppressLint
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import org.bombayneurosciences.bna_2023.R
import java.io.File
import java.io.IOException

class LatestFragment1 : Fragment() {
    private var viewPager: ViewPager2? = null
    private var adapter: PdfCompositeAdapter? = null
    private var pdfFiles: MutableList<File>? = null
    private var startFileName: String? = "article_20240419173647.pdf"

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_latest, container, false)

        viewPager = view.findViewById(R.id.viewPager)

        val pdfFileNames: List<String> = mutableListOf(
            "issue_20240629082804.pdf",
            "article_20240629130016.pdf",
            "BNA_Journal_Cover_Pic.pdf",
            "article_20240418062902.pdf",
            "article_20240629140813.pdf",
            "article_20240629140857.pdf",
            "article_20240629142940.pdf",
            "article_20240418071151.pdf",
            "article_20240629143644.pdf",
            "article_20240418074109.pdf",
            "article_20240420082757.PDF",
            "article_20240418080549.pdf",
            "article_20240418081148.pdf",
            "article_20240418085519.pdf",
            "article_20240419112704.pdf",
            "article_20240419112916.pdf",
            "article_20240419113109.pdf",
            "article_20240419113155.pdf",
            "article_20240629143854.pdf",
            "article_20240419113228.pdf",
            "article_20240629144243.pdf",
            "article_20240419113501.pdf",
            "article_20240419113604.pdf",
            "article_20240629145216.pdf",
            "article_20240419074901.pdf",
            "article_20240419112326.pdf",
            "article_20240419114033.pdf",
            "article_20240419173515.pdf",
            "article_20240629145425.pdf",
            "article_20240419173647.pdf",
            "article_20240629145535.pdf",
            "article_20240419173813.pdf",
            "article_20240419173925.pdf"
        )

        pdfFiles = ArrayList()
        for (fileName in pdfFileNames) {
            (pdfFiles as ArrayList<File>).add(File("/storage/emulated/0/Android/data/org.bombayneurosciences.bna_2023/files/BNA_App_PDF/$fileName"))
        }

        if (startFileName.isNullOrEmpty()) {
            startFileName = "issue_20240629082804.pdf"
        }

        try {
            val startPosition = calculateStartPosition(pdfFileNames, startFileName!!)
            adapter = context?.let { PdfCompositeAdapter(it, pdfFiles as ArrayList<File>, startPosition) }
            viewPager!!.adapter = adapter
            viewPager!!.setCurrentItem(startPosition, false)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return view
    }

    private fun calculateStartPosition(pdfFileNames: List<String>, startFileName: String): Int {
        val startIndex = pdfFileNames.indexOf(startFileName)
        if (startIndex == -1) {
            return 0 // Default to the first file if not found
        }
        var startPosition = 0
        for (i in 0 until startIndex) {
            val pdfFile = File("/storage/emulated/0/Android/data/org.bombayneurosciences.bna_2023/files/BNA_App_PDF/" + pdfFileNames[i])
            try {
                val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = PdfRenderer(fileDescriptor)
                startPosition += pdfRenderer.pageCount
                pdfRenderer.close()
                fileDescriptor.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return startPosition
    }
}
