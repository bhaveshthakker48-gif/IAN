package org.bombayneurosciences.bna_2023.Fragment

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.org.wfnr_2024.ViewModel.BNAProviderFactory
import com.org.wfnr_2024.ViewModel.BNARespository
import com.org.wfnr_2024.ViewModel.BNA_ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.*
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DAO.Journal_DAO
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData
import org.bombayneurosciences.bna_2023.CallBack.JournalDataClick
import org.bombayneurosciences.bna_2023.CustomPageUtils.CurlPage
import org.bombayneurosciences.bna_2023.CustomPageUtils.CurlView
import org.bombayneurosciences.bna_2023.JournalNewFolder.WelcomeElement
import org.bombayneurosciences.bna_2023.Model.PdfFileInfo
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.adapter.JournalBottomAdapter
import org.bombayneurosciences.bna_2023.databinding.FragmentJournalLatestBinding
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import java.io.*
import java.net.URL
import java.util.*

class JournalLatestFragment : Fragment(), View.OnClickListener, JournalDataClick {

    private lateinit var binding: FragmentJournalLatestBinding

    private val pdfRendererList = mutableListOf<PdfRenderer>()
    private val PDF_FILE_NAMES = mutableListOf<String>()

    lateinit var dialog:Dialog


    private lateinit var viewModel: BNA_ViewModel
    private lateinit var viewModel1: BNA_RD_ViewModel
    private lateinit var progressDialog: ProgressDialog

    lateinit var SessionManager1: SessionManager1

    private val downloadQueue: Queue<Pair<String, String>> = LinkedList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJournalLatestBinding.inflate(inflater, container, false)

       /* binding.webView!!.webViewClient  = WebViewClient() // To handle navigation within WebView

        // Enable JavaScript if needed
        binding.webView!!.settings!!.javaScriptEnabled  = true

        // URL of the PDF file
        val pdfUrl = "https://telemedocket.com/BNA/public/uploads/Articles_file/article_20240419173925.pdf"

        // Load PDF in WebView
        binding.webView!!.loadUrl(pdfUrl)*/

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        SessionManager1=SessionManager1(requireContext())
        getViewModel()
        createRoomDatabase()
        init()
        setupObservers()

        downloadFiles()
        binding.journalMenu.setOnClickListener(this)





    }

    private fun createRoomDatabase() {
        val database = BNA_RoomDatabase.getDatabase(requireContext())
        val journalDAO: Journal_DAO = database.Journal_DAO()
        val repository = BNA_RD_Repository(journalDAO, database)
        viewModel1 = ViewModelProvider(this, BNA_RD_ViewModelFactory(repository)).get(BNA_RD_ViewModel::class.java)
    }

    private fun getViewModel() {
        val bnaRepository = BNARespository()
        val bnaProviderFactory = BNAProviderFactory(bnaRepository, requireActivity().application)
        viewModel = ViewModelProvider(this, bnaProviderFactory).get(BNA_ViewModel::class.java)

        progressDialog = ProgressDialog(context).apply {
            setCancelable(false)
            setMessage(getString(R.string.please_wait))
        }
    }

    private fun init() {
        if (ConstanstsApp.checkInternetConenction(requireContext())) {
            viewModel1.deleteJournal()
            viewModel.fetchJournalData()
        } else {
            ConstanstsApp.showCustomToast(requireContext(), R.string.no_internet_connection.toString())
        }
    }

    private fun setupObservers() {
        viewModel.journalData.observe(viewLifecycleOwner, Observer { response ->
            val journalLocalDataList = response.map { data ->
                JournalLoacalData(
                    0,
                    id = data.id,
                    issueId = data.issue_id,
                    month = data.month,
                    year = data.year,
                    articleType = data.article_type ?: "",
                    title = data.title,
                    author = data.author,
                    reference = data.reference ?: "",
                    indexPage = data.index_page,
                    noOfPage = data.no_of_page,
                    articleFile = data.articleFile,
                    isArchive = data.is_archive,
                    isActive = data.is_active,
                    isDeleted = data.is_deleted,
                    createdAt = data.created_at,
                    updatedAt = data.updated_at,
                    issueFile = data.issueFile,
                    volume = data.volume,
                    issue_no = data.issue_no
                )
            }.toMutableList()

            Log.d(ConstanstsApp.tag,"journalLocalDataList=>"+journalLocalDataList)
            viewModel1.insertJournal(journalLocalDataList)
           // downloadFilesSequentially()
        })

       /* viewModel1.get_All_journal.observe(viewLifecycleOwner, Observer { response ->
            val seenIssueFile = mutableSetOf<String>()
            response.forEach { data ->
                val issueFileName = getFileNameFromUrl(data.issueFile)
                val articleFileName = getFileNameFromUrl(data.articleFile)

                if (seenIssueFile.add(issueFileName)) {
                    PDF_FILE_NAMES.add(issueFileName)
                }

                PDF_FILE_NAMES.add(articleFileName)
            }
            Log.d(ConstanstsApp.tag, "PDF_FILE_NAMES => $PDF_FILE_NAMES")

            SessionManager1.setPdfFileNames(PDF_FILE_NAMES)


            initializeCurlView()
        })*/
    }

    private fun downloadFiles() {
        viewModel1.get_All_journal.observe(viewLifecycleOwner, Observer { response ->
            val seenIssueFile = mutableSetOf<String>()

            val PdfFileInfoList = mutableSetOf<PdfFileInfo>()


            val sortedResponse = response.sortedBy { it.indexPage }


            sortedResponse.forEach { data ->
                val issueFileName = getFileNameFromUrl(data.issueFile)
                val articleFileName = getFileNameFromUrl(data.articleFile)

                if (seenIssueFile.add(issueFileName)) {
                    PDF_FILE_NAMES.add(issueFileName)
                }

                PDF_FILE_NAMES.add(articleFileName)
                PdfFileInfoList.add(PdfFileInfo(data.month,data.year,data.indexPage,issueFileName,articleFileName))


                downloadQueue.add(Pair(data.issueFile, getFileNameFromUrl(data.issueFile)))
                downloadQueue.add(Pair(data.articleFile, getFileNameFromUrl(data.articleFile)))



            }

            Log.d(ConstanstsApp.tag,"PDF_FILE_NAMES11=>"+PDF_FILE_NAMES)

            SessionManager1.setPdfFileNames(PDF_FILE_NAMES)
            SessionManager1.setPdfFileNames1(PdfFileInfoList.toList())


            downloadFilesSequentially()

            //initializeCurlView()


            // Initialize ViewPager if files are already downloaded
           /* if (pdfFiles.isNotEmpty()) {
                initializeViewPager()
            }*/
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.journalMenu -> {
                setJournalBottomDialog()
            }
        }
    }

    private fun setJournalBottomDialog() {
        dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_journal)

        val recyclerViewJournalLatest = dialog.findViewById<RecyclerView>(R.id.RecyclerView_journal_lastest)
        val TextView_no_data = dialog.findViewById<TextView>(R.id.TextView_no_data)

        viewModel1.get_All_journal.observe(viewLifecycleOwner, Observer { response ->
            if (response.isNotEmpty()) {
                TextView_no_data.visibility = View.GONE
                Log.e("responcem_menu",""+response)
                
                val adapter = JournalBottomAdapter(response, requireContext(),this)
                recyclerViewJournalLatest.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                recyclerViewJournalLatest.adapter = adapter
                adapter.notifyDataSetChanged()
            } else {
                TextView_no_data.visibility = View.VISIBLE
            }
        })

        dialog.show()
        val window = dialog.window ?: return
        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val dialogHeight = (screenHeight * 0.5).toInt()

        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dialogHeight
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.attributes.windowAnimations = R.style.DialogAnimation
        window.setGravity(Gravity.BOTTOM)
    }

    private fun getFileNameFromUrl(url: String): String {
        val lastSlashIndex = url.lastIndexOf('/')
        return if (lastSlashIndex != -1 && lastSlashIndex < url.length - 1) {
            url.substring(lastSlashIndex + 1)
        } else {
            ""
        }
    }

    private fun downloadFilesSequentially() {
        GlobalScope.launch(Dispatchers.IO) {
            while (downloadQueue.isNotEmpty()) {
                val (url, fileName) = downloadQueue.poll()
                try {
                    val urlConnection = URL(url).openConnection()
                    urlConnection.connect()

                    val inputStream = BufferedInputStream(urlConnection.getInputStream())
                    val folderName = "BNA_App_PDF"
                    val appFolder = File(requireContext().getExternalFilesDir(null), folderName)

                    if (!appFolder.exists()) {
                        appFolder.mkdirs()
                    }

                    val outputFile = File(appFolder, fileName)

                    if (outputFile.exists()) {
                        outputFile.delete()
                    }

                    val outputStream = FileOutputStream(outputFile)
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }

                    outputStream.flush()
                    outputStream.close()
                    inputStream.close()

                    Log.d(ConstanstsApp.tag, "File saved successfully: $outputFile")
                } catch (e: IOException) {
                    Log.e(ConstanstsApp.tag, "Error downloading file from $url: ${e.message}", e)
                }

                withContext(Dispatchers.Main) {
                    if (isAdded && view != null) {
                        initializeCurlView()
                    }
                }
            }
        }
    }

    private fun initializeCurlView() {
        val pdfDir = File(requireContext().getExternalFilesDir(null), "BNA_App_PDF")
        if (!pdfDir.exists()) {
            pdfDir.mkdirs()
        }

        for (pdfFileName in PDF_FILE_NAMES) {
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

        binding.curl.setPageProvider(PageProvider())
        binding.curl.setSizeChangedObserver(SizeChangedObserver())
    }

    private fun copyPdfFromAssets(pdfFileName: String, outputFile: File) {
        try {
            val assetManager = requireContext().assets
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

    /*inner class PageProvider : CurlView.PageProvider {
        override fun getPageCount(): Int = pdfRendererList.sumOf { it.pageCount }

        override fun updatePage(curlPage: CurlPage?, width: Int, height: Int, index: Int) {
            var pageIndex = index
            var rendererIndex = 0

            while (pageIndex >= pdfRendererList[rendererIndex].pageCount) {
                pageIndex -= pdfRendererList[rendererIndex].pageCount
                rendererIndex++
            }

            val pdfRenderer = pdfRendererList[rendererIndex]
            val currentPage = pdfRenderer.openPage(pageIndex)

            val bitmap = Bitmap.createBitmap(currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888)
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            currentPage.close()

            curlPage?.setTexture(bitmap, CurlPage.SIDE_BOTH)
        }
    }*/


    inner class PageProvider : CurlView.PageProvider {
        override fun getPageCount(): Int {
            // Calculate total page count across all PDF renderers
            return pdfRendererList.sumBy { it.pageCount }
        }

        override fun updatePage(curlPage: CurlPage?, width: Int, height: Int, index: Int) {
            var pageIndex = index

            var cumulativePageIndex = 0
            var rendererIndex = 0

            // Find the correct PDF renderer and page index
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


   /* inner class PageProvider : CurlView.PageProvider {
        override fun getPageCount(): Int {
            // Calculate total page count across all PDF renderers
            return pdfRendererList.sumBy { it.pageCount }
        }

        override fun updatePage(curlPage: CurlPage?, width: Int, height: Int, index: Int) {
            var pageIndex = index
            var rendererIndex = 0

            // Adjust index to start from BNA_Journal_Cover_Pic.pdf
            var totalPageCount = 0
            for (i in pdfRendererList.indices) {
                val pageCount = pdfRendererList[i].pageCount
                if (PDF_FILE_NAMES[i] == "BNA_Journal_Cover_Pic.pdf") {
                    rendererIndex = i
                    pageIndex = index - totalPageCount
                    break
                }
                totalPageCount += pageCount
            }

            // Find the correct renderer and page index within that renderer
            while (pageIndex >= pdfRendererList[rendererIndex].pageCount) {
                pageIndex -= pdfRendererList[rendererIndex].pageCount
                rendererIndex++
            }

            val pdfRenderer = pdfRendererList[rendererIndex]
            val currentPage = pdfRenderer.openPage(pageIndex)

            val bitmap = Bitmap.createBitmap(currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888)
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            currentPage.close()

            curlPage?.setTexture(bitmap, CurlPage.SIDE_BOTH)
        }
    }*/



    inner class SizeChangedObserver : CurlView.SizeChangedObserver {
        override fun onSizeChanged(width: Int, height: Int) {
            val margin = 0.1f
           // binding.curl.setViewMode(CurlView.SHOW_TWO_PAGES)
            binding.curl.setViewMode(CurlView.SHOW_ONE_PAGE)
            binding.curl.setMargins(margin, margin, margin, margin)
        }
    }

    override fun JournalItemClicked(data: JournalLoacalData, position: Int, view: View) {
       // setFragment(JournalViewFragment(),data)

        dialog.dismiss()

        val bundle = Bundle()
        bundle.putSerializable("journal_data", data)
        bundle.putStringArrayList("pdf_file_names", ArrayList(PDF_FILE_NAMES))

        val intent = Intent(requireContext(), JournalViewActivity::class.java).apply {
            putExtras(bundle)
        }

        startActivity(intent)
    }

    override fun JournalItemClicked1(data: WelcomeElement, position: Int, view: View) {
        TODO("Not yet implemented")
    }

    override fun ItemClicked(data: Bitmap, position: Int) {
        TODO("Not yet implemented")
    }


    fun setFragment(fragment: Fragment, data: JournalLoacalData)
    {

        val bundle = Bundle()
        bundle.putSerializable("journal_data", data)
        bundle.putStringArrayList("pdf_file_names", ArrayList(PDF_FILE_NAMES))// Assuming 'data' is an instance of JournalLoacalData

// Set arguments to the fragment
        fragment.arguments = bundle

// Start FragmentTransaction to replace the current fragment with the new one
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.addToBackStack(null) // Optional: Add to back stack for fragment navigation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE) // Optional: Fragment transition animation
        transaction.commit()
    }
}

