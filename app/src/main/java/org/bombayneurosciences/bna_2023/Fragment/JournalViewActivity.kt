package org.bombayneurosciences.bna_2023.Fragment

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData
import org.bombayneurosciences.bna_2023.CustomPageUtils.CurlPage
import org.bombayneurosciences.bna_2023.CustomPageUtils.CurlView
import org.bombayneurosciences.bna_2023.databinding.FragmentJournalViewBinding
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class JournalViewActivity : AppCompatActivity() {

    private lateinit var binding: FragmentJournalViewBinding
    private val pdfRendererList = mutableListOf<PdfRenderer>()
    private lateinit var sessionManager: SessionManager1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentJournalViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        sessionManager = SessionManager1(this)

        // Retrieve data from the bundle
        val journalData = intent.getSerializableExtra("journal_data") as? JournalLoacalData
        val pdfFileNames = intent.getStringArrayListExtra("pdf_file_names")

        val pdfFileName = ConstanstsApp.getFileNameFromUrl(journalData!!.articleFile)
        val fileDir = getExternalFilesDir("BNA_App_PDF")
        val pdfFile = File(fileDir, pdfFileName)

        // Use the data
        journalData?.let { data ->
            Log.d("YourActivity", "Journal Data: $data")
            binding.eventheader.text = data.title
        }

        pdfFileNames?.let { fileNames ->
            Log.d("YourActivity", "PDF File Names: $fileNames")
        }

        if (pdfFile.exists()) {
            val filePath = ConstanstsApp.getFilePathPDF(pdfFileName)
            Log.d(ConstanstsApp.tag, "filePath => $filePath")

            initializeCurlView(filePath, pdfFileNames)

        } else {
            Log.e(ConstanstsApp.tag, "File does not exist: ${pdfFile.absolutePath}")
        }
    }

    private fun initializeCurlView(articleFilePath: String, pdfFileNames: ArrayList<String>?) {
        // Load the primary article file
        val articleFile = File(articleFilePath)
        if (articleFile.exists()) {
            try {
                val fileDescriptor = ParcelFileDescriptor.open(articleFile, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = PdfRenderer(fileDescriptor)
                pdfRendererList.add(pdfRenderer)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Log.e("YourActivity", "PDF file does not exist at path: $articleFilePath")
        }

        // Load additional PDF files
        pdfFileNames?.let { fileNames ->
            val pdfDir = File(getExternalFilesDir(null), "BNA_App_PDF")
            if (!pdfDir.exists()) {
                pdfDir.mkdirs()
            }

            for (pdfFileName in fileNames) {
                val pdfFile = File(pdfDir, pdfFileName)
                if (!pdfFile.exists()) {
                    copyPdfFromAssets(pdfFileName, pdfFile)
                }
                try {
                    val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
                    val pdfRenderer = PdfRenderer(fileDescriptor)
                    pdfRendererList.add(pdfRenderer)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        binding.curl.setPageProvider(PageProvider())
        binding.curl.setSizeChangedObserver(SizeChangedObserver())
    }

    private fun copyPdfFromAssets(pdfFileName: String, outputFile: File) {
        try {
            val assetManager = assets
            val inputStream = assetManager.open(pdfFileName)
            val outputStream = FileOutputStream(outputFile)

            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    inner class PageProvider : CurlView.PageProvider {
        override fun getPageCount(): Int {
            return pdfRendererList.sumOf { it.pageCount }
        }

        override fun updatePage(curlPage: CurlPage?, width: Int, height: Int, index: Int) {
            var pageIndex = index
            var cumulativePageIndex = 0
            var rendererIndex = 0

            for (renderer in pdfRendererList) {
                if (pageIndex < cumulativePageIndex + renderer.pageCount) {
                    val localPageIndex = pageIndex - cumulativePageIndex
                    val currentPage = renderer.openPage(localPageIndex)

                    val bitmap = Bitmap.createBitmap(currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888)
                    currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    currentPage.close()

                    curlPage?.setTexture(bitmap, CurlPage.SIDE_BOTH)
                    break
                }
                cumulativePageIndex += renderer.pageCount
                rendererIndex++
            }
        }
    }

    inner class SizeChangedObserver : CurlView.SizeChangedObserver {
        override fun onSizeChanged(width: Int, height: Int) {
            val margin = 0.1f
            binding.curl.setViewMode(CurlView.SHOW_ONE_PAGE)
            binding.curl.setMargins(margin, margin, margin, margin)
        }
    }
}
