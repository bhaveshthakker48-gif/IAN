package org.bombayneurosciences.bna_2023.Fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.org.wfnr_2024.ViewModel.BNAProviderFactory
import com.org.wfnr_2024.ViewModel.BNARespository
import com.org.wfnr_2024.ViewModel.BNA_ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.notify
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_Repository
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_ViewModel
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_ViewModelFactory
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RoomDatabase
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DAO.Journal_DAO
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData
import org.bombayneurosciences.bna_2023.CallBack.JournalDataClick
import org.bombayneurosciences.bna_2023.CustomPageUtils.BookPageFlipTransformer
import org.bombayneurosciences.bna_2023.CustomPageUtils.BookPageTurnTransformer1
import org.bombayneurosciences.bna_2023.CustomPageUtils.CurlView
import org.bombayneurosciences.bna_2023.CustomPageUtils.CurlView1
import org.bombayneurosciences.bna_2023.CustomPageUtils.CustomBookFlipAnimation
import org.bombayneurosciences.bna_2023.CustomPageUtils.CustomBookFlipAnimation1
import org.bombayneurosciences.bna_2023.CustomPageUtils.FrontBackTransformer
import org.bombayneurosciences.bna_2023.CustomPageUtils.PageFlipTransformer
import org.bombayneurosciences.bna_2023.CustomPageUtils.PageFlipTransformer1
import org.bombayneurosciences.bna_2023.CustomPageUtils.PageFlipTransformer2
import org.bombayneurosciences.bna_2023.CustomPageUtils.PageFlipTransformer3
import org.bombayneurosciences.bna_2023.CustomPageUtils.PageTurnTransformer
import org.bombayneurosciences.bna_2023.JournalNewFolder.WelcomeElement
import org.bombayneurosciences.bna_2023.Model.PdfFileInfo
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.adapter.JournalBottomAdapter

import org.bombayneurosciences.bna_2023.databinding.FragmentLatestBinding
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp.Companion.getFileNameFromUrl
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.LinkedList
import java.util.Queue

class JournalLastestFragment2 : Fragment(), View.OnClickListener, JournalDataClick {

    private var adapter: PdfCompositeAdapter? = null
    private var pdfFiles: MutableList<File>? = null
    var startFileName: String? = null

   // private lateinit var curlView: CurlView

    private lateinit var viewModel: BNA_ViewModel
    private lateinit var viewModel1: BNA_RD_ViewModel
    private lateinit var progressDialog: ProgressDialog
    private lateinit var sessionManager: SessionManager1

    private lateinit var binding: FragmentLatestBinding

    private val downloadQueue: Queue<Pair<String, String>> = LinkedList()
    private val PDF_FILE_NAMES = mutableListOf<String>()

    private var filesDownloadedCount = 0

    lateinit var dialog: Dialog

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLatestBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pdfFiles = ArrayList()

        getViewModel()
        createRoomDatabase()

        init()
        setupObservers()

        if (sessionManager.getIsDownload().equals("1")) {

            // Example usage in your Fragment or Activity
            val pdfFileInfoList = sessionManager.getPdfFileInfos()


            // Sort the response by indexPage initially
            val sortedResponse = pdfFileInfoList.sortedBy { it.indexPage }

            // Find the top item (first in sorted list)
            val topItem = sortedResponse.firstOrNull()

            topItem?.let { top ->
                // Get the top issue file name
                val topIssueFileName = top.issue_fileName

                // Use LinkedHashSet to maintain order and remove duplicates
                val filteredArticles = LinkedHashSet<String>()

                // Add the top issue file name first
                filteredArticles.add(topIssueFileName)

                // Filter articles for the top month and year, sorted by indexPage
                sortedResponse
                    .filter { it.month.equals(top.month, ignoreCase = true) && it.year == top.year }
                    .sortedBy { it.indexPage }
                    .forEach { data ->
                        val articleFileName = data.article_fileName
                        filteredArticles.add(articleFileName)
                    }

                // Convert to a list if needed
                val filteredArticlesList = filteredArticles.toList()

                // Now filteredArticlesList contains the top issue file and unique article file names, sorted by indexPage
                Log.d(ConstanstsApp.tag, filteredArticlesList.toString())

                setCustomPageViewer(filteredArticlesList, startFileName)






            }


        } else {
            downloadFiles()

        }

        binding.journalMenu.setOnClickListener(this)


    }


   /* private fun loadPdfPage(position: Int) {
        // Load the PDF page at the given position
        if (position >= 0 && position < pdfFiles!!.size) {
            val pdfFile = pdfFiles!![position]
            // Load the PDF file as needed
            // e.g., adapter?.notifyItemChanged(position)

            adapter?.notifyItemChanged(position)
        }
    }*/


    private fun calculateStartPosition(pdfFileNames: List<String>, startFileName: String): Int {
        val startIndex = pdfFileNames.indexOf(startFileName)
        if (startIndex == -1) {
            return 0 // Default to the first file if not found
        }
        var startPosition = 0
        for (i in 0 until startIndex) {
            val pdfFile =
                File("/storage/emulated/0/Android/data/org.bombayneurosciences.bna_2023/files/BNA_App_PDF/" + pdfFileNames[i])
            try {
                val fileDescriptor =
                    ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
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

    private fun createRoomDatabase() {
        val database = BNA_RoomDatabase.getDatabase(requireContext())
        val journalDAO: Journal_DAO = database.Journal_DAO()
        val repository = BNA_RD_Repository(journalDAO, database)
        viewModel1 = ViewModelProvider(
            this,
            BNA_RD_ViewModelFactory(repository)
        ).get(BNA_RD_ViewModel::class.java)

        sessionManager = SessionManager1(requireContext())
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
            // ConstanstsApp.showCustomToast(requireContext(), "No Internet Connection")

            val pdfNames = sessionManager.getPdfFileNames()


            Log.d(ConstanstsApp.tag, "pdfNames=>" + pdfNames)


            /* loadLocalPdfs(pdfNames)

            showNoInternetAlert("Please connect to the internet")*/

        }
    }

    private fun setupObservers() {
        viewModel.journalData.observe(viewLifecycleOwner, Observer { response ->

            Log.d(ConstanstsApp.tag, "journalData=>" + response)


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

            Log.d(ConstanstsApp.tag, "journalLocalDataList=>" + journalLocalDataList)
            viewModel1.insertJournal(journalLocalDataList)

        })


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
                PdfFileInfoList.add(
                    PdfFileInfo(
                        data.month,
                        data.year,
                        data.indexPage,
                        issueFileName,
                        articleFileName
                    )
                )


                downloadQueue.add(Pair(data.issueFile, getFileNameFromUrl(data.issueFile)))
                downloadQueue.add(Pair(data.articleFile, getFileNameFromUrl(data.articleFile)))


            }

            Log.d(ConstanstsApp.tag, "PDF_FILE_NAMES11=>" + PDF_FILE_NAMES)

            sessionManager.setPdfFileNames(PDF_FILE_NAMES)
            sessionManager.setPdfFileNames1(PdfFileInfoList.toList())


            downloadFilesSequentially()


           /* // Initialize ViewPager if files are already downloaded
            if (pdfFiles!!.isNotEmpty()) {
               // initializeViewPager()
                Log.d(ConstanstsApp.tag,"PDF_FILE_NAMES11"+PDF_FILE_NAMES)
                setCustomPageViewer(PDF_FILE_NAMES, startFileName)
            }*/
        })
    }


    private fun downloadFilesSequentially() {
        progressDialog.show()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val appFolder = createAppFolder("BNA_App_PDF")
            filesDownloadedCount = 0 // Reset the counter

            while (downloadQueue.isNotEmpty()) {
                val (url, fileName) = downloadQueue.poll()
                try {
                    downloadFile(url, fileName, appFolder)
                    filesDownloadedCount++
                } catch (e: IOException) {
                    Log.e(ConstanstsApp.tag, "Error downloading file from $url: ${e.message}", e)
                }
            }

            withContext(Dispatchers.Main) {
                progressDialog.dismiss()
                initializeViewPager()
                setPdf()


            }
        }
    }

    private fun createAppFolder(folderName: String): File {
        val appFolder = File(requireContext().getExternalFilesDir(null), folderName)
        if (!appFolder.exists()) {
            appFolder.mkdirs()
        }
        return appFolder
    }

    private fun downloadFile(url: String, fileName: String, appFolder: File) {
        val urlConnection = URL(url).openConnection()
        urlConnection.connect()

        val inputStream = urlConnection.getInputStream()
        val outputFile = File(appFolder, fileName)

        try {
            val outputStream = FileOutputStream(outputFile)
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            // Verify downloaded file
            if (verifyFile(outputFile)) {
                Log.d(ConstanstsApp.tag, "File saved successfully: $outputFile")
                // Add downloaded file to pdfFiles if it exists and is verified
                if (outputFile.exists()) {
                    pdfFiles!!.add(outputFile)
                    filesDownloadedCount++
                }
            } else {
                Log.e(
                    ConstanstsApp.tag,
                    "Error: Downloaded file is corrupted or invalid: $outputFile"
                )
                // Handle the error (e.g., delete the corrupted file, retry download, etc.)
                outputFile.delete()
            }
        } catch (e: IOException) {
            Log.e(ConstanstsApp.tag, "Error downloading file from $url: ${e.message}", e)
        }
    }

    private fun verifyFile(file: File): Boolean {
        // Implement your file verification logic here
        // For example, you can check file size, content, checksum, etc.
        // Return true if the file is valid, false otherwise
        // Example simple validation: check if the file exists and has non-zero size
        return file.exists() && file.length() > 0
    }

    private fun initializeViewPager() {

        sessionManager.setIsDownload("1")

        val appFolder = File(requireContext().getExternalFilesDir(null), "BNA_App_PDF")
        val existingPdfFiles = mutableListOf<File>()


        // Check existence of each PDF file in PDF_FILE_NAMES
        for (pdfName in PDF_FILE_NAMES) {
            val pdfFile = File(appFolder, pdfName)
            if (pdfFile.exists()) {
                existingPdfFiles.add(pdfFile)
            }
        }

        // Use synchronized block to safely modify pdfFiles
        synchronized(pdfFiles!!) {
            pdfFiles!!.clear()
            pdfFiles!!.addAll(existingPdfFiles)
        }


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

        val recyclerViewJournalLatest =
            dialog.findViewById<RecyclerView>(R.id.RecyclerView_journal_lastest)
        val TextView_no_data = dialog.findViewById<TextView>(R.id.TextView_no_data)

        val TextView_index = dialog.findViewById<ImageView>(R.id.TextView_index)

        val bottom_header_text = dialog.findViewById<TextView>(R.id.bottom_header_text)

        TextView_index.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dialog.dismiss()
            }

        })

        viewModel1.get_All_journal.observe(viewLifecycleOwner, Observer { response ->
            if (response.isNotEmpty()) {
                TextView_no_data.visibility = View.GONE

                Log.d(ConstanstsApp.tag, "response bottom=>" + response.reversed())


                val topItem = response.first()


                val year =
                    topItem.year.toInt() // Replace with your actual year value from the database or model
                val lastTwoDigits = (year % 100).toString().padStart(2, '0')
                val month = topItem.month // Replace with your actual month value
                val abbreviatedMonth = month.take(3).toUpperCase()

                val text =
                    "Vol." + topItem.volume + " | " + "Issue" + topItem.issue_no + " | " + abbreviatedMonth + " " + lastTwoDigits


                //  bottom_header_text.text=topItem.month+" "+topItem.year


                // Create a SpannableStringBuilder to format the text with different colors
                val builder = SpannableStringBuilder()

// Append "Vol." + topItem.volume in black color
                builder.appendColoredText("Vol.${topItem.volume}", Color.BLACK)

// Append " | " in gray color
                builder.appendColoredText(" | ", Color.GRAY)

// Append "Issue" + topItem.issue_no in black color
                builder.appendColoredText("Issue${topItem.issue_no}", Color.BLACK)

                builder.appendColoredText(" | ", Color.GRAY)

// Append " " + abbreviatedMonth + " " + lastTwoDigits in red color
                builder.appendColoredText(" $abbreviatedMonth $lastTwoDigits", ContextCompat.getColor(requireContext(),R.color.dark_red))

// Now `builder` contains the formatted text with different colors
                val formattedText = builder.toString()

// Use `formattedText` as needed, for example setting it to a TextView


                bottom_header_text.text = builder


                // Filter the list to show only items with month "June"
                // val filteredList = response.filter { it.month.equals(topItem.month, ignoreCase = true) }.sortedBy { it.indexPage }

                val seenTitles = mutableSetOf<String>()
                val filteredList = response
                    .filter { it.month.equals(topItem.month, ignoreCase = true) } // Filter by month
                    .filter {
                        val title = it.author
                        seenTitles.add(title) // Add to set and filter out if already present
                    }
                    .sortedBy { it.indexPage } // Sort by indexPage



                for (data in filteredList) {
                    Log.d(ConstanstsApp.tag, "index=>" + data.indexPage + " title=>" + data.title)
                }


                //val adapter = JournalBottomAdapter(response.reversed(), requireContext(),this)
                val adapter = JournalBottomAdapter(filteredList, requireContext(), this)
                recyclerViewJournalLatest.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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
        val dialogHeight = (screenHeight * 0.9).toInt()

        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dialogHeight
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.attributes.windowAnimations = R.style.DialogAnimation
        window.setGravity(Gravity.BOTTOM)
    }

    override fun JournalItemClicked(data: JournalLoacalData, position: Int, view: View) {
        // setFragment(JournalViewFragment(),data)

        dialog.dismiss()

        val pdfFileInfoList = sessionManager.getPdfFileInfos()

        // Sort the response by indexPage initially
        val sortedResponse = pdfFileInfoList.sortedBy { it.indexPage }

        // Find the top item (first in sorted list)
        val topItem = sortedResponse.firstOrNull()

        topItem?.let { top ->
            // Get the top issue file name
            val topIssueFileName = top.issue_fileName

            // Use LinkedHashSet to maintain order and remove duplicates
            val filteredArticles = LinkedHashSet<String>()

            // Add the top issue file name first
            filteredArticles.add(topIssueFileName)

            // Filter articles for the top month and year, sorted by indexPage
            sortedResponse
                .filter { it.month.equals(top.month, ignoreCase = true) && it.year == top.year }
                .sortedBy { it.indexPage }
                .forEach { data ->
                    val articleFileName = data.article_fileName
                    filteredArticles.add(articleFileName)
                }

            // Convert to a list if needed
            val filteredArticlesList = filteredArticles.toList()

            // Now filteredArticlesList contains the top issue file and unique article file names, sorted by indexPage
            Log.d("Filtered Articles", filteredArticlesList.toString())

            Log.d(ConstanstsApp.tag, "articleFile=>" + data.articleFile)

            setCustomPageViewer(filteredArticlesList, getFileNameFromUrl(data.articleFile))


        }
    }

    override fun JournalItemClicked1(data: WelcomeElement, position: Int, view: View) {
        TODO("Not yet implemented")
    }

    override fun ItemClicked(data: Bitmap, position: Int) {
        TODO("Not yet implemented")
    }

    fun SpannableStringBuilder.appendColoredText(text: String, color: Int) {
        val start = length
        append(text)
        setSpan(ForegroundColorSpan(color), start, length, 0)
    }




    private fun setCustomPageViewer(
        filteredArticlesList: List<String>,
        initialStartFileName: String?
    ) {
        var startFileName = initialStartFileName

        pdfFiles = ArrayList()
        for (fileName in filteredArticlesList) {
            (pdfFiles as ArrayList<File>).add(File("/storage/emulated/0/Android/data/org.bombayneurosciences.bna_2023/files/BNA_App_PDF/$fileName"))
        }

        if (startFileName.isNullOrEmpty()) {
            startFileName = filteredArticlesList.firstOrNull() ?: ""

            Log.d(ConstanstsApp.tag,"startFileName.isNullOrEmpty()=>"+startFileName)
            Log.d(ConstanstsApp.tag,"startFileName.isNullOrEmpty()=>"+pdfFiles)
        }

        try {
            val startPosition = calculateStartPosition(filteredArticlesList, startFileName!!)
            adapter =
                context?.let { PdfCompositeAdapter(it, pdfFiles as ArrayList<File>, startPosition) }

            binding.viewPager.adapter = adapter

          /*  // Example of setting a listener to update the curl effect
            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(position: Int, offset: Float, offsetPixels: Int) {
                    // Update page curl view based on scroll position
                    binding.pageCurlView.curlOffset = offset * 50
                }
            })*/



            binding.viewPager.setCurrentItem(startPosition, false)


//            binding.viewPager.setPageTransformer(PageFlipTransformer2())
//            binding.viewPager.setPageTransformer(PageFlipTransformer3())

     /*       binding.viewPager.setPageTransformer { page, position ->
                page.translationX = -position * page.width
                page.alpha = 1 - Math.abs(position)
                page.scaleX = 1 - 0.3f * Math.abs(position)
                page.scaleY = 1 - 0.3f * Math.abs(position)
            }*/

            binding.viewPager.setPageTransformer(PageFlipTransformer())

//            binding.viewPager.setPageTransformer(PageFlipTransformer1())



//            binding.viewPager.setPageTransformer(FrontBackTransformer())
//            binding.viewPager.setPageTransformer(PageTurnTransformer())
//            binding.viewPager.setPageTransformer(BookPageFlipTransformer())
//            binding.viewPager.setPageTransformer(BookPageTurnTransformer1())

            // curlView.setCurrentIndex(startPosition)


            // Set the page transformer
          /*  val transformer = CustomBookFlipAnimation1().apply {
                setScaleAmountPercent(10f) // Adjust scale amount
                setEnableScale(true) // Enable scale effect
            }
            binding.viewPager.setPageTransformer(transformer)*/

           /* binding.viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // Handle page selection
                  *//*  if (position > 0 && position < pdfFiles!!.size - 1) {
                        // Load the previous, current, and next pages
                        loadPdfPage(position - 1)
                        loadPdfPage(position)
                        loadPdfPage(position + 1)
                    }*//*

                    if (position >= -1 && position < pdfFiles!!.size) {
                        // Load the current page
                        loadPdfPage(position)

                        // Preload adjacent pages if applicable
                        if (position > 0) {
                            loadPdfPage(position - 1) // Load previous page
                        }
                        if (position < pdfFiles!!.size - 1) {
                            loadPdfPage(position + 1) // Load next page
                        }
                    }

                    // If last page is reached, reset to the first page (position 0)
                   *//* if (position == pdfFiles!!.size - 1) {
                        binding.viewPager.setCurrentItem(0, false)
                    }*//*
                }
            })*/

            /*binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    if (position >= 0 && position < pdfFiles!!.size) {
                        loadPdfPage(position)

                        // Preload adjacent pages if applicable
                        if (position > 0) {
                            loadPdfPage(position - 1) // Load previous page
                        }
                        if (position < pdfFiles!!.size - 1) {
                            loadPdfPage(position + 1) // Load next page
                        }
                    } else {
                        Log.e(ConstanstsApp.tag, "Invalid page index: $position, pageCount: ${pdfFiles!!.size}")
                        // Handle invalid page index, possibly show an error or log message
                    }
                }
            })*/

            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // Handle page selection
                    if (position > 0 && position < pdfFiles!!.size - 1) {
                        // Load the previous, current, and next pages
                        loadPdfPage(position - 1)
                        loadPdfPage(position)
                        loadPdfPage(position + 1)
                    }
                }
            })


            binding.viewPager.offscreenPageLimit = 1 // Preload adjacent pages
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


   /* private fun setCustomPageViewer(
        filteredArticlesList: List<String>,
        initialStartFileName: String?
    ) {
        var startFileName = initialStartFileName

        pdfFiles = ArrayList()
        for (fileName in filteredArticlesList) {
            (pdfFiles as ArrayList<File>).add(File("/storage/emulated/0/Android/data/org.bombayneurosciences.bna_2023/files/BNA_App_PDF/$fileName"))
        }

        if (startFileName.isNullOrEmpty()) {
            startFileName = filteredArticlesList.first()
        }

        try {
            val startPosition = calculateStartPosition(filteredArticlesList, startFileName!!)
            Log.d(ConstanstsApp.tag, "Calculated start position: $startPosition for file: $startFileName")

            adapter = context?.let { PdfCompositeAdapter(it, pdfFiles as ArrayList<File>, startPosition) }
            binding.viewPager.adapter = adapter

            val transformer = CustomBookFlipAnimation1().apply {
                setScaleAmountPercent(10f) // Adjust scale amount
                setEnableScale(true) // Enable scale effect
            }
            binding.viewPager.setPageTransformer(transformer)

            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.d(ConstanstsApp.tag, "Page selected at position: $position")
                    // Ensure the loadPdfPage method properly handles edge cases
                    if (position > 0 && position < pdfFiles!!.size - 1) {
                        loadPdfPage(position - 1)
                        loadPdfPage(position)
                        loadPdfPage(position + 1)
                    } else if (position == 0) {
                        loadPdfPage(position)
                        loadPdfPage(position + 1)
                    } else if (position == pdfFiles!!.size - 1) {
                        loadPdfPage(position - 1)
                        loadPdfPage(position)
                    }
                }
            })

            binding.viewPager.offscreenPageLimit = 1 // Preload adjacent pages

            binding.viewPager.post {
                binding.viewPager.setCurrentItem(startPosition, false)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }*/

  /*  private fun setCustomPageViewer(
        filteredArticlesList: List<String>,
        initialStartFileName: String?
    ) {
        var startFileName = initialStartFileName

        pdfFiles = ArrayList()
        for (fileName in filteredArticlesList) {
            val filePath = "/storage/emulated/0/Android/data/org.bombayneurosciences.bna_2023/files/BNA_App_PDF/$fileName"
            Log.d(ConstanstsApp.tag, "Adding file: $filePath")
            (pdfFiles as ArrayList<File>).add(File(filePath))
        }

        if (startFileName.isNullOrEmpty()) {
            startFileName = filteredArticlesList.first()
        }

        try {
            val startPosition = calculateStartPosition(filteredArticlesList, startFileName!!)
            Log.d(ConstanstsApp.tag, "Calculated start position: $startPosition for file: $startFileName")

            adapter = context?.let { PdfCompositeAdapter(it, pdfFiles as ArrayList<File>, startPosition) }
            binding.viewPager.adapter = adapter

            val transformer = CustomBookFlipAnimation1().apply {
                setScaleAmountPercent(10f) // Adjust scale amount
                setEnableScale(true) // Enable scale effect
            }
           // binding.viewPager.setPageTransformer(transformer)

         *//*   binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.d(ConstanstsApp.tag, "Page selected at position: $position")
                    when (position) {
                        0 -> {
                            loadPdfPage(position)
                            if (pdfFiles!!.size > 1) {
                                loadPdfPage(position + 1)
                            }
                        }
                        pdfFiles!!.size - 1 -> {
                            loadPdfPage(position - 1)
                            loadPdfPage(position)
                        }
                        else -> {
                            loadPdfPage(position - 1)
                            loadPdfPage(position)
                            loadPdfPage(position + 1)
                        }
                    }
                }
            })*//*

            *//*binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.d(ConstanstsApp.tag, "Page selected at position: $position")

                    // Load the current page
                    loadPdfPage(position)

                    // Preload adjacent pages
                    if (position > 0) {
                        loadPdfPage(position - 1) // Load previous page
                    }
                    if (position < pdfFiles!!.size - 1) {
                        loadPdfPage(position + 1) // Load next page
                    }

                }
            })*//*

            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.d(ConstanstsApp.tag, "Page selected at position: $position")

                    // Load the current page
                    loadPdfPage(position)

                    // Preload adjacent pages
                    if (position > 0) {
                        loadPdfPage(position - 1) // Load previous page
                    }
                    if (position < pdfFiles!!.size - 1) {
                        loadPdfPage(position + 1) // Load next page
                    }

                    // Ensure page at position 0 is loaded when selected
                    if (position == 0) {
                        loadPdfPage(0)
                    }
                }
            })



            binding.viewPager.offscreenPageLimit = 1 // Preload adjacent pages

            binding.viewPager.post {
                Log.d(ConstanstsApp.tag, "Setting current item to start position: $startPosition")
                binding.viewPager.setCurrentItem(startPosition, false)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }*/

/*    private fun loadPdfPage(position: Int) {
        if (position < 0 || position >= pdfFiles!!.size) {
            Log.d(ConstanstsApp.tag, "Invalid page position: $position")
            return
        }

        val file = pdfFiles!![position]
        Log.d(ConstanstsApp.tag, "Loading PDF page at position: $position, file: ${file.path}")

        // Notify the adapter if it's not null
        adapter?.notifyDataSetChanged()

        // Implement your logic to load the PDF page from the file
        // For example:
        try {
            // Open the PDF file and load the page content
            // Replace this with your actual implementation to load PDF content
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }*/


  /*  private fun loadPdfPage(position: Int) {
        if (position < 0 || position >= pdfFiles!!.size) {
            Log.d(ConstanstsApp.tag, "Invalid page position: $position")
            return
        }

        val file = pdfFiles!![position]
        Log.d(ConstanstsApp.tag, "Loading PDF page at position: $position, file: ${file.path}")

        var pdfRenderer: PdfRenderer? = null
        var parcelFileDescriptor: ParcelFileDescriptor? = null

        try {
            // Open a parcel file descriptor for the PDF file
            parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

            // Create a PdfRenderer from the parcel file descriptor
            pdfRenderer = PdfRenderer(parcelFileDescriptor)

            // Check the number of pages in the PDF
            val pageCount = pdfRenderer.pageCount

            if (pageCount > 0 && position < pageCount) {
                // Open the specific page for rendering
                val page = pdfRenderer.openPage(position)

                // Example: Render the page onto a bitmap or extract text/image content
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                // Use the bitmap or extract text/image content as needed
                // For example, display the bitmap in an ImageView or process text content

                // Close the page
                page.close()

                // Notify adapter of data set changes
                adapter?.notifyDataSetChanged()
            } else {
                Log.d(ConstanstsApp.tag, "Invalid page index: $position, pageCount: $pageCount")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            // Close PdfRenderer and ParcelFileDescriptor to release resources
            pdfRenderer?.close()
            parcelFileDescriptor?.close()
        }
    }*/



   /* private fun loadPdfPage(position: Int) {
        if (position < 0 || position >= pdfFiles!!.size) {
            Log.d(ConstanstsApp.tag, "Invalid page position: $position")

            // Display the PDF at position 0 if the index is out of bounds
            if (pdfFiles!!.isNotEmpty()) {
                val file = pdfFiles!![0]
                Log.d(ConstanstsApp.tag, "Loading PDF page at position: 0, file: ${file.path}")

                var pdfRenderer: PdfRenderer? = null
                var parcelFileDescriptor: ParcelFileDescriptor? = null

                try {
                    // Open a parcel file descriptor for the PDF file
                    parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

                    // Create a PdfRenderer from the parcel file descriptor
                    pdfRenderer = PdfRenderer(parcelFileDescriptor)

                    // Open the specific page for rendering (page 0)
                    val page = pdfRenderer.openPage(0)

                    // Example: Render the page onto a bitmap or extract text/image content
                    val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                    // Use the bitmap or extract text/image content as needed
                    // For example, display the bitmap in an ImageView or process text content

                    // Close the page
                    page.close()

                    // Notify adapter of data set changes
                    adapter?.notifyDataSetChanged()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    // Close PdfRenderer and ParcelFileDescriptor to release resources
                    pdfRenderer?.close()
                    parcelFileDescriptor?.close()
                }
            }
            return
        }

        val file = pdfFiles!![position]
        Log.d(ConstanstsApp.tag, "Loading PDF page at position: $position, file: ${file.path}")

        var pdfRenderer: PdfRenderer? = null
        var parcelFileDescriptor: ParcelFileDescriptor? = null

        try {
            // Open a parcel file descriptor for the PDF file
            parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

            // Create a PdfRenderer from the parcel file descriptor
            pdfRenderer = PdfRenderer(parcelFileDescriptor)

            // Check the number of pages in the PDF
            val pageCount = pdfRenderer.pageCount

            // Ensure position is within valid range
            if (pageCount > 0 && position < pageCount) {
                // Open the specific page for rendering
                val page = pdfRenderer.openPage(position)

                // Example: Render the page onto a bitmap or extract text/image content
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                // Use the bitmap or extract text/image content as needed
                // For example, display the bitmap in an ImageView or process text content

                // Close the page
                page.close()

                // Notify adapter of data set changes
                adapter?.notifyDataSetChanged()
            } else {
                Log.d(ConstanstsApp.tag, "Invalid page index: $position, pageCount: $pageCount")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            // Close PdfRenderer and ParcelFileDescriptor to release resources
            pdfRenderer?.close()
            parcelFileDescriptor?.close()
        }
    }*/


    private fun loadPdfPage(position: Int) {
        // Load the PDF page at the given position
        if (position >= 0 && position < pdfFiles!!.size) {
            val pdfFile = pdfFiles!![position]
            // Load the PDF file as needed
            // e.g., adapter?.notifyItemChanged(position)

            adapter?.notifyItemChanged(position)
        }
    }












    private fun setPdf() {
        // Example usage in your Fragment or Activity
        val pdfFileInfoList = sessionManager.getPdfFileInfos()


        // Sort the response by indexPage initially
        val sortedResponse = pdfFileInfoList.sortedBy { it.indexPage }

        // Find the top item (first in sorted list)
        val topItem = sortedResponse.firstOrNull()

        topItem?.let { top ->
            // Get the top issue file name
            val topIssueFileName = top.issue_fileName

            // Use LinkedHashSet to maintain order and remove duplicates
            val filteredArticles = LinkedHashSet<String>()

            // Add the top issue file name first
            filteredArticles.add(topIssueFileName)

            // Filter articles for the top month and year, sorted by indexPage
            sortedResponse
                .filter { it.month.equals(top.month, ignoreCase = true) && it.year == top.year }
                .sortedBy { it.indexPage }
                .forEach { data ->
                    val articleFileName = data.article_fileName
                    filteredArticles.add(articleFileName)
                }

            // Convert to a list if needed
            val filteredArticlesList = filteredArticles.toList()

            // Now filteredArticlesList contains the top issue file and unique article file names, sorted by indexPage
            Log.d(ConstanstsApp.tag, filteredArticlesList.toString())

            setCustomPageViewer(filteredArticlesList, startFileName)
        }


    }
}