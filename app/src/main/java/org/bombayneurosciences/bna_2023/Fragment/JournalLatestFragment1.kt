package org.bombayneurosciences.bna_2023.Fragment

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.gson.Gson
import com.org.wfnr_2024.ViewModel.BNAProviderFactory
import com.org.wfnr_2024.ViewModel.BNARespository
import com.org.wfnr_2024.ViewModel.BNA_ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bombayneurosciences.bna_2023.Activity.MainActivity
import org.bombayneurosciences.bna_2023.Activity.PdfZoomFullActivity
import org.bombayneurosciences.bna_2023.Activity.Voting_videoview
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_Repository
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_ViewModel
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_ViewModelFactory
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RoomDatabase
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DAO.Journal_DAO
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData
import org.bombayneurosciences.bna_2023.CallBack.JournalDataClick
import org.bombayneurosciences.bna_2023.CustomPageUtils.BookFlipPageTransformer
import org.bombayneurosciences.bna_2023.JournalNewFolder.WelcomeElement
import org.bombayneurosciences.bna_2023.Model.PdfFileInfo
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.adapter.JournalBottomAdapter
import org.bombayneurosciences.bna_2023.databinding.FragmentLastest1Binding
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.LinkedList
import java.util.Queue


 class JournalLatestFragment1 : Fragment(), View.OnClickListener, JournalDataClick {

    private lateinit var binding: FragmentLastest1Binding
    var startFileName: String? = null
    private lateinit var viewModel: BNA_ViewModel
    private lateinit var viewModel1: BNA_RD_ViewModel
    private lateinit var progressDialog: ProgressDialog
    private lateinit var sessionManager: SessionManager1
    private val downloadQueue: Queue<Pair<String, String>> = LinkedList()
    private val PDF_FILE_NAMES = mutableListOf<String>()
    private lateinit var pdfFiles: ArrayList<File>

    private var currentPdfPageIndex: Int = 0 // Track the current page index across all PDF files

    val filteredArticles = mutableListOf<String>()

    private var isDownloadInitiated = false // Flag to track download initiation

    lateinit var dialog:Dialog

    // Track number of files downloaded
    private var filesDownloadedCount = 0
    private var indexpage = 0
    private val FILES_TO_DOWNLOAD_FIRST = 1 // Number of files to download and display first

    /////////////////////
    private val pdfRendererList = mutableListOf<PdfRenderer>()

    ////////////////////

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLastest1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pdfFiles = ArrayList()

        sessionManager = SessionManager1(requireContext())
        getViewModel()
        createRoomDatabase()



        binding.journalMenu.setOnClickListener(this)




        binding.journalZoom.setOnClickListener(View.OnClickListener {

            /*val bundle = Bundle()
            bundle.putInt("indexPage", indexpage)
//            bundle.putStringArrayList("pdf_file_names", java.util.ArrayList(PDF_FILE_NAMES))

//              bundle.putStringArrayList("pdf_file_names", java.util.ArrayList(filteredArticles))
//

            val intent = Intent(requireContext(), PdfZoomFullActivity::class.java)
                .apply {
                putExtras(bundle)
            }

            startActivity(intent)*/

Log.e("mmmmmmm",""+indexpage)
            val intent = Intent(requireContext(), PdfZoomFullActivity::class.java)
            intent.putExtra("indexPage", indexpage)
            startActivity(intent)

        })


    }



    override fun onResume() {
        super.onResume()


        init()
        setupObservers()


        if (sessionManager.getIsDownload().equals("1")) {
            val pdfNames = sessionManager.getPdfFileNames()

            if (pdfNames.isNotEmpty()) {
                Log.d(ConstanstsApp.tag, "pdfNames: $pdfNames")
            } else {
                Log.d(ConstanstsApp.tag, "pdfNames is empty or null")
            }

            // val pdfData:List<PdfFileInfo> =sessionManager.getPdfFileInfos()

            // Example usage in your Fragment or Activity
            val pdfFileInfoList = sessionManager.getPdfFileInfos()



            // val topItem=pdfFileInfoList.first()


// Now you can work with pdfFileInfoList in your application
           /* pdfFileInfoList.forEach { fileInfo ->

                val issueFileName = fileInfo.issue_fileName
                val article_fileName = fileInfo.article_fileName

                if (filteredArticles.add(issueFileName)) {
                    filteredArticles.add(issueFileName)
                }
                // Perform actions with each PdfFileInfo object

                filteredArticles.add(article_fileName)
            }*/


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

                loadLocalPdfs(filteredArticlesList)

//                initializeCurlView(filteredArticlesList.toString(),startFileName)


//                Toast.makeText(activity,"show data",Toast.LENGTH_SHORT).show()

            }


        }
        else
        {
            downloadFiles()

//            Toast.makeText(activity,"not data",Toast.LENGTH_SHORT).show()
        }










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
           // ConstanstsApp.showCustomToast(requireContext(), "No Internet Connection")

            val pdfNames=sessionManager.getPdfFileNames()


            Log.d(ConstanstsApp.tag,"pdfNames=>"+pdfNames)


            loadLocalPdfs(pdfNames)

            showNoInternetAlert("Please connect to the internet")

        }
    }

    private fun loadLocalPdfs(pdfNames: List<String>) {

        Log.d(ConstanstsApp.tag,"pdfNames in loadLocalPdfs=>"+pdfNames)
        // Get the directory path for BNA_App_PDF
        val appFolder = File(requireContext().getExternalFilesDir(null), "BNA_App_PDF")

        if (appFolder.exists() && appFolder.isDirectory) {
            // List all files in the directory
            val files = appFolder.listFiles()

            // Iterate through the files
            files?.forEach { file ->
                // Check if the file is a PDF and its name is in pdfNames list
                if (file.extension.equals("pdf", ignoreCase = true) && pdfNames.contains(file.name)) {
                    // Add the file to pdfFiles list
                    pdfFiles.add(file)
                    // Add the file name to PDF_FILE_NAMES list
                    PDF_FILE_NAMES.add(file.name)
                }
            }
        }

        // Initialize ViewPager if PDF files are found
        if (pdfFiles.isNotEmpty()) {
            val gson = Gson()
            val json = gson.toJson(pdfFiles)
            Log.e("modimodi4",""+json)
            initializeLocalViewPager(pdfFiles)
        } else {
            Log.e(ConstanstsApp.tag, "No PDF files found or PDF File Names not found in session manager")
            // Handle case where no PDF files are found or handle error
        }
    }





    private fun initializeLocalViewPager(pdfFileNames: ArrayList<File>) {

//Toast.makeText(requireContext(),"one",Toast.LENGTH_SHORT).show()

        // Set up ViewPager with PdfPagerAdapter
        binding.pdfViewPager.adapter = PdfPagerAdapter(pdfFileNames)
       // (binding.pdfViewPager.adapter as PdfPagerAdapter).notifyDataSetChanged()

        binding.pdfViewPager.adapter?.notifyDataSetChanged()

//        Toast.makeText(activity," data",Toast.LENGTH_SHORT).show()


        // Set the OnPageChangeListener
        binding.pdfViewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // Called when the current page is scrolled
                Log.d("ViewPager", "Page Scrolled: $position, Offset: $positionOffset")


            }

            override fun onPageSelected(position: Int) {
                // Called when a new page becomes selected
                Log.d("ViewPager1", "Page Selected: $position")
                indexpage = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                // Called when the scroll state changes
                // State can be either SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, or SCROLL_STATE_SETTLING
                Log.d("ViewPager", "Scroll State Changed: $state")
            }
        })



//        binding.pdfViewPager.setPageTransformer(true, BookFlipPageTransformer())

    }



    private fun setupObservers() {
        viewModel.journalData.observe(viewLifecycleOwner, Observer { response ->

            Log.d(ConstanstsApp.tag,"journalData=>"+response)



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
                PdfFileInfoList.add(PdfFileInfo(data.month,data.year,data.indexPage,issueFileName,articleFileName))


                downloadQueue.add(Pair(data.issueFile, getFileNameFromUrl(data.issueFile)))
                downloadQueue.add(Pair(data.articleFile, getFileNameFromUrl(data.articleFile)))



            }

            Log.d(ConstanstsApp.tag,"PDF_FILE_NAMES11=>"+PDF_FILE_NAMES)

            sessionManager.setPdfFileNames(PDF_FILE_NAMES)
            sessionManager.setPdfFileNames1(PdfFileInfoList.toList())


            downloadFilesSequentially()


            // Initialize ViewPager if files are already downloaded
            if (pdfFiles.isNotEmpty()) {
                initializeViewPager()
            }
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

    /*private fun downloadFile(url: String, fileName: String, appFolder: File) {
        val urlConnection = URL(url).openConnection()
        urlConnection.connect()

        val inputStream = urlConnection.getInputStream()
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

        // Add downloaded file to pdfFiles if it exists
        if (outputFile.exists()) {
            pdfFiles.add(outputFile)
            filesDownloadedCount++
        }
    }*/

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
                    pdfFiles.add(outputFile)
                    filesDownloadedCount++
                }
            } else {
                Log.e(ConstanstsApp.tag, "Error: Downloaded file is corrupted or invalid: $outputFile")
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
//        Toast.makeText(requireContext(),"two",Toast.LENGTH_SHORT).show()
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
        synchronized(pdfFiles) {
            pdfFiles.clear()
            pdfFiles.addAll(existingPdfFiles)
        }

        // Set up ViewPager with PdfPagerAdapter
        binding.pdfViewPager.adapter = PdfPagerAdapter(ArrayList(pdfFiles))
        binding.pdfViewPager.adapter?.notifyDataSetChanged()

        //TODO
//        binding.pdfViewPager.setCurrentItem(1, false)
        // Apply the custom page flip transformer if needed

        //TODO
        binding.pdfViewPager.setPageTransformer(true, BookFlipPageTransformer())


//        binding.pdfViewPager.setPageTransformer(true, CustomBookFlipAnimation1())

//        Toast.makeText(activity,"ggg data",Toast.LENGTH_SHORT).show()
    }





    private fun getFileNameFromUrl(url: String): String {
        val lastSlashIndex = url.lastIndexOf('/')
        return if (lastSlashIndex != -1 && lastSlashIndex < url.length - 1) {
            url.substring(lastSlashIndex + 1)
        } else {
            ""
        }
    }




    inner class PdfPagerAdapter(private val pdfFiles: ArrayList<File>) : PagerAdapter() {

        private val rendererMap = mutableMapOf<Int, PdfRenderer>()

        override fun getCount(): Int {
            return pdfFiles.sumBy { getPdfPageCount(it) }
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(container.context)
            val view = inflater.inflate(R.layout.pdf_page1, container, false)

            val imageView = view.findViewById<ImageView>(R.id.pdfImageView)

            displayPdfPage(position, imageView)

            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
            closeRenderer(position)
        }

        private fun displayPdfPage(position: Int, imageView: ImageView) {
            try {
                var cumulativePageIndex = 0

                for ((index, file) in pdfFiles.withIndex()) {
                    val renderer = getRenderer(index, file)

                    if (position < cumulativePageIndex + renderer.pageCount) {
                        val localPageIndex = position - cumulativePageIndex
                        val page = renderer.openPage(localPageIndex)
                        val width = page.width
                        val height = page.height
                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        imageView.setImageBitmap(bitmap)
                        page.close()


                        break
                    } else {
                        cumulativePageIndex += renderer.pageCount


                    }
                }
            } catch (e: IOException) {
                Log.e(ConstanstsApp.tag, "Error rendering PDF file: ${e.message}", e)
            }
        }



        private fun getPdfPageCount(file: File): Int {
            return try {
                val renderer = PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
                val count = renderer.pageCount
                renderer.close()
                count
            } catch (e: IOException) {
                Log.e(ConstanstsApp.tag, "Error getting page count for PDF file: ${e.message}", e)
                0 // Return 0 or another default value when file is not a valid PDF or is corrupted
            }
        }


        private fun getRenderer(index: Int, file: File): PdfRenderer {
            return rendererMap.getOrPut(index) {
                val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                PdfRenderer(fileDescriptor)

            }
        }

        private fun closeRenderer(position: Int) {
            val iterator = rendererMap.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                val renderer = entry.value
                renderer.close()
                iterator.remove()
            }
        }
    }




    override fun onClick(v: View?) {
        when (v) {
            binding.journalMenu -> {
                binding.journalMenu.isEnabled = false
                setJournalBottomDialog()
            }

        }
    }

    private fun setJournalBottomDialog() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.journalMenu.isEnabled = true
        }, 3000)


        dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_journal)

        val recyclerViewJournalLatest = dialog.findViewById<RecyclerView>(R.id.RecyclerView_journal_lastest)
        val TextView_no_data = dialog.findViewById<TextView>(R.id.TextView_no_data)

        val TextView_index=dialog.findViewById<ImageView>(R.id.TextView_index)

        val bottom_header_text = dialog.findViewById<TextView>(R.id.bottom_header_text)

        TextView_index.setOnClickListener(object :View.OnClickListener
        {
            override fun onClick(v: View?) {
               dialog.dismiss()
            }

        })

        viewModel1.get_All_journal.observe(viewLifecycleOwner, Observer { response ->
            if (response.isNotEmpty()) {
                TextView_no_data.visibility = View.GONE

                Log.d(ConstanstsApp.tag,"response bottom=>"+response.reversed())



                val topItem = response.first()



                val year = topItem.year.toInt() // Replace with your actual year value from the database or model
                val lastTwoDigits = (year % 100).toString().padStart(2, '0')
                val month = topItem.month // Replace with your actual month value
                val abbreviatedMonth = month.take(3).toUpperCase()

                val text="Vol."+topItem.volume+" | "+"Issue"+topItem.issue_no+" | "+abbreviatedMonth+" "+lastTwoDigits


              //  bottom_header_text.text=topItem.month+" "+topItem.year




                // Create a SpannableStringBuilder to format the text with different colors
                val builder = SpannableStringBuilder()

                // Append " " + abbreviatedMonth + " " + lastTwoDigits in red color
                builder.appendColoredText(" $abbreviatedMonth $year", Color.RED)


                builder.appendColoredText(" : ", Color.RED)
// Append "Vol." + topItem.volume in black color
                builder.appendColoredText("Vol.${topItem.volume}", Color.RED)

// Append " | " in gray color
                builder.appendColoredText(" | ", Color.RED)

// Append "Issue" + topItem.issue_no in black color
                builder.appendColoredText("Issue${topItem.issue_no}", Color.RED)





// Now `builder` contains the formatted text with different colors
                val formattedText = builder.toString()

// Use `formattedText` as needed, for example setting it to a TextView


                bottom_header_text.text=builder


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



                for(data in filteredList)
                {
                    Log.d(ConstanstsApp.tag,"index=>"+data.indexPage+" title=>"+data.title)
                }






                //val adapter = JournalBottomAdapter(response.reversed(), requireContext(),this)
                val adapter = JournalBottomAdapter(filteredList, requireContext(),this)
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
        val dialogHeight = (screenHeight * 0.9).toInt()

        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dialogHeight
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.attributes.windowAnimations = R.style.DialogAnimation
        window.setGravity(Gravity.BOTTOM)
    }
/////////////////////////////////////////////////////////////////////////////////////
    override fun JournalItemClicked(data: JournalLoacalData, position: Int, view: View) {
        // setFragment(JournalViewFragment(),data)
          Log.e("show_data_list",""+data)
          Log.e("show_data_list",""+position)
          Log.e("show_data_list",""+view)
        dialog.dismiss()
      /*  val bundle = Bundle()
        bundle.putSerializable("journal_data", data)
      bundle.putStringArrayList("pdf_file_names", java.util.ArrayList(PDF_FILE_NAMES))
      bundle.putString("index", "1")

      //  bundle.putStringArrayList("pdf_file_names", java.util.ArrayList(filteredArticles))
    *//*    val intent = Intent(requireContext(), JournalViewActivity::class.java).apply {
            putExtras(bundle)
        }*//*

        val intent = Intent(requireContext(), PdfZoomFullActivity::class.java).apply {
            putExtras(bundle)
        }
        startActivity(intent)*/
        val pdfFileName = ConstanstsApp.getFileNameFromUrl(data!!.articleFile)
        val fileDir = requireContext().getExternalFilesDir("BNA_App_PDF")
        val pdfFile = File(fileDir, pdfFileName)
        initializeViewPager1(pdfFileName,data.indexPage-1)
   /* dialog.dismiss()
    Log.e("show_data_list",""+data.indexPage)
    updateViewPager(data.indexPage)*/
    }

    override fun JournalItemClicked1(data: WelcomeElement, position: Int, view: View) {
        TODO("Not yet implemented")
    }

     override fun ItemClicked(data: Bitmap, position: Int) {
         TODO("Not yet implemented")
     }


     //////////////////////////////////////////////////////////////////////////////////////

    private fun showNoInternetAlert(message:String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.intenet_alert)

        // Find views in the custom dialog layout
        val notificationTitle: TextView = dialog.findViewById(R.id.notificationTitle)
        val textViewLogoutConfirmation: TextView = dialog.findViewById(R.id.textViewLogoutConfirmation)
        val buttonYes: Button = dialog.findViewById(R.id.buttonYes)
        val buttonNo: Button = dialog.findViewById(R.id.buttonNo)

        // Set notification title and confirmation text
        notificationTitle.text = "Connectivity Issue"
        textViewLogoutConfirmation.text = message

        // Set click listener for the Yes button
        buttonYes.setOnClickListener {
            // Handle the Yes button click event
            dialog.dismiss()
        }

        // Set click listener for the No button
        buttonNo.setOnClickListener {
            // Handle the No button click event
            dialog.dismiss()
        }

        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
    }

    fun SpannableStringBuilder.appendColoredText(text: String, color: Int) {
        val start = length
        append(text)
        setSpan(ForegroundColorSpan(color), start, length, 0)
    }





    private fun  initializeViewPager1(startingPdfFileName: String,indexpage:Int) {


        val appFolder = File(requireContext().getExternalFilesDir(null), "BNA_App_PDF")

        // Use CoroutineScope to launch the initialization task
        lifecycleScope.launch {
            val existingPdfFiles = withContext(Dispatchers.IO) {
                // Check existence of each PDF file in PDF_FILE_NAMES
                PDF_FILE_NAMES.map { fileName ->
                    File(appFolder, fileName)
                }.filter { it.exists() }
            }

            // Determine starting PDF file and its index
            val startingPdfFile = existingPdfFiles.firstOrNull { it.name == startingPdfFileName }
            val startingPdfIndex = existingPdfFiles.indexOf(startingPdfFile)

            if (startingPdfFile == null) {
                Log.e(ConstanstsApp.tag, "Starting PDF file not found: $startingPdfFileName")
                return@launch  // Handle error if starting PDF file is not found
            }

            // Update pdfFiles and currentPdfPageIndex
            synchronized(pdfFiles) {
                pdfFiles.clear()
                pdfFiles.addAll(existingPdfFiles)
            }
            currentPdfPageIndex = startingPdfIndex

            // Update ViewPager on the main thread
            withContext(Dispatchers.Main) {
                // Set up ViewPager with PdfPagerAdapter
                binding.pdfViewPager.adapter = PdfPagerAdapter(ArrayList(pdfFiles))
                binding.pdfViewPager.adapter?.notifyDataSetChanged()

                // Set current item and apply page transformer
                binding.pdfViewPager.setCurrentItem(indexpage, false) // false for smooth scroll
//               binding.pdfViewPager.setPageTransformer(true, BookFlipPageTransformer())
//                Toast.makeText(activity,"not data",Toast.LENGTH_SHORT).show()
            }
        }
    }





    private fun updateViewPager(currentPageIndex: Int) {
       // val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val adapter = PdfPagerAdapter(pdfFiles)
        binding.pdfViewPager.adapter = adapter

        // Set the current item (page) in ViewPager
        binding.pdfViewPager.setCurrentItem(currentPageIndex, false) // false for smooth scroll

        // Optionally, notify adapter of data set change if needed
        adapter.notifyDataSetChanged()
    }





    private fun updateViewPager() {
        try {
            // Set up ViewPager with PdfPagerAdapter
            val adapter = PdfPagerAdapter(pdfFiles)
            binding.pdfViewPager.adapter = adapter
            adapter.notifyDataSetChanged()

            // Apply page transformer for flipping animation
            binding.pdfViewPager.setPageTransformer(true, BookFlipPageTransformer())

        } catch (e: Exception) {
            Log.e(ConstanstsApp.tag, "Error updating ViewPager: ${e.message}", e)
            // Handle error appropriately
        }
    }


    ///////////////////*
    //    private fun initializeCurlView(articleFilePath: String, pdfFileNames: ArrayList<String>?) {
    //        // Load the primary article file
    //        val articleFile = File(articleFilePath)
    //        if (articleFile.exists()) {
    //            try {
    //                val fileDescriptor = ParcelFileDescriptor.open(articleFile, ParcelFileDescriptor.MODE_READ_ONLY)
    //                val pdfRenderer = PdfRenderer(fileDescriptor)
    //                pdfRendererList.add(pdfRenderer)
    //            } catch (e: IOException) {
    //                e.printStackTrace()
    //            }
    //        } else {
    //            Log.e("YourActivity", "PDF file does not exist at path: $articleFilePath")
    //        }
    //
    //        // Load additional PDF files
    //        pdfFileNames?.let { fileNames ->
    //            val pdfDir = File(requireActivity().getExternalFilesDir(null), "BNA_App_PDF")
    //            if (!pdfDir.exists()) {
    //                pdfDir.mkdirs()
    //            }
    //
    //            for (pdfFileName in fileNames) {
    //                val pdfFile = File(pdfDir, pdfFileName)
    //                if (!pdfFile.exists()) {
    //                    copyPdfFromAssets(pdfFileName, pdfFile)
    //                }
    //                try {
    //                    val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
    //                    val pdfRenderer = PdfRenderer(fileDescriptor)
    //                    pdfRendererList.add(pdfRenderer)
    //                } catch (e: IOException) {
    //                    e.printStackTrace()
    //                }
    //            }
    //        }
    //
    //        binding.curl!!.setPageProvider(PageProvider())
    //        binding.curl!!.setSizeChangedObserver(SizeChangedObserver())
    //    }
    //
    //    private fun copyPdfFromAssets(pdfFileName: String, outputFile: File) {
    //        try {
    //            val assetManager = requireContext().assets
    //            val inputStream = assetManager.open(pdfFileName)
    //            val outputStream = FileOutputStream(outputFile)
    //
    //            val buffer = ByteArray(1024)
    //            var bytesRead: Int
    //            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
    //                outputStream.write(buffer, 0, bytesRead)
    //            }
    //
    //            inputStream.close()
    //            outputStream.close()
    //        } catch (e: IOException) {
    //            e.printStackTrace()
    //        }
    //    }
    //
    //    inner class PageProvider : CurlView.PageProvider {
    //        override fun getPageCount(): Int {
    //            return pdfRendererList.sumOf { it.pageCount }
    //        }
    //
    //        override fun updatePage(curlPage: CurlPage?, width: Int, height: Int, index: Int) {
    //            var pageIndex = index
    //            var cumulativePageIndex = 0
    //            var rendererIndex = 0
    //
    //            for (renderer in pdfRendererList) {
    //                if (pageIndex < cumulativePageIndex + renderer.pageCount) {
    //                    val localPageIndex = pageIndex - cumulativePageIndex
    //                    val currentPage = renderer.openPage(localPageIndex)
    //
    //                    val bitmap = Bitmap.createBitmap(currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888)
    //                    currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
    //                    currentPage.close()
    //
    //                    curlPage?.setTexture(bitmap, CurlPage.SIDE_BOTH)
    //                    break
    //                }
    //                cumulativePageIndex += renderer.pageCount
    //                rendererIndex++
    //            }
    //        }
    //    }
    //
    //    inner class SizeChangedObserver : CurlView.SizeChangedObserver {
    //        override fun onSizeChanged(width: Int, height: Int) {
    //            val margin = 0.1f
    //            binding.curl!!.setViewMode(CurlView.SHOW_ONE_PAGE)
    //            binding.curl!!.setMargins(margin, margin, margin, margin)
    //        }
    //    }
    //    *//////////


    ///////////////////////////////

}



