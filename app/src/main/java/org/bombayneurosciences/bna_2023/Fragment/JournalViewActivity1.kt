package org.bombayneurosciences.bna_2023.Fragment



import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData
import org.bombayneurosciences.bna_2023.CustomPageUtils.CurlPage
import org.bombayneurosciences.bna_2023.CustomPageUtils.CurlRenderer1
import org.bombayneurosciences.bna_2023.CustomPageUtils.CurlView1
import org.bombayneurosciences.bna_2023.CustomPageUtils.PdfRendererHelper
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import java.io.File

class JournalViewActivity1 : AppCompatActivity() {

    private lateinit var curlView: CurlView1
    private lateinit var pdfRendererHelper: PdfRendererHelper
    private lateinit var pdfBitmaps: ArrayList<Bitmap>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_view_1)

        curlView = findViewById(R.id.curlView)

        // Initialize PdfRendererHelper
        pdfRendererHelper = PdfRendererHelper(this)



        // Retrieve data from the bundle
        val journalData = intent.getSerializableExtra("journal_data") as? JournalLoacalData
        val pdfFileNames = intent.getStringArrayListExtra("pdf_file_names")
        //val articleFilePath = intent.getStringExtra("article_file_path")

        val pdfFileName = ConstanstsApp.getFileNameFromUrl(journalData!!.articleFile)
        val fileDir = getExternalFilesDir("BNA_App_PDF")
        val pdfFile = File(fileDir, pdfFileName)



        if (pdfFile.exists()) {
            val filePath = ConstanstsApp.getFilePathPDF(pdfFileName)
            Log.d(ConstanstsApp.tag, "filePath => $filePath")

            // Load PDF and render pages to bitmaps

            pdfBitmaps = pdfRendererHelper.renderPdfToBitmaps(filePath)




        } else {
            Log.e(ConstanstsApp.tag, "File does not exist: ${pdfFile.absolutePath}")

        }

        // Configure CurlView1 settings
        curlView.setViewMode(CurlView1.SHOW_ONE_PAGE)
        curlView.setMaxScaleFactor(5.0f)
        curlView.setMinScaleFactor(1.0f)

        // Set the bitmaps to CurlView1
        setPdfPagesToCurlView()
    }

    private fun setPdfPagesToCurlView() {
        // Assume pdfBitmaps has been loaded with bitmaps
        if (pdfBitmaps.isNotEmpty()) {
            // Assuming CurlView1 can handle loading of multiple pages
            for (bitmap in pdfBitmaps) {
                val curlPage = CurlPage()
                curlPage.setTexture(bitmap,0)
                // Assuming addPage method exists and adds a page to CurlView1
                curlView.addPage(curlPage, CurlRenderer1.PAGE_RIGHT) // or PAGE_LEFT based on your implementation
            }
        }

        curlView.setCurrentIndex(0) // Set initial page index
    }


    override fun onResume() {
        super.onResume()
        curlView.onResume()
    }

    override fun onPause() {
        super.onPause()
        curlView.onPause()
    }
}
