package org.bombayneurosciences.bna_2023.Activity

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
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.org.wfnr_2024.ViewModel.BNA_ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_Repository
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_ViewModel
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_ViewModelFactory
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RoomDatabase
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DAO.Journal_DAO
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData
import org.bombayneurosciences.bna_2023.CallBack.JournalDataClick
import org.bombayneurosciences.bna_2023.Fragment.PdfCompositeAdapter
import org.bombayneurosciences.bna_2023.JournalNewFolder.WelcomeElement
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.adapter.JournalBottomAdapter
import org.bombayneurosciences.bna_2023.databinding.ActivityPdfZoomFullBinding
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp.Companion.getFileNameFromUrl
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import java.io.File
import java.io.IOException
import java.util.LinkedList
import java.util.Queue

class PdfZoomFullActivity : AppCompatActivity(), View.OnClickListener, JournalDataClick {

    private lateinit var sessionManager: SessionManager1
    var startFileName: String? = null
    private lateinit var pdfFiles: ArrayList<File>

    private var adapter: PdfCompositeAdapter? = null

    lateinit var dialog:Dialog

    var index:Int?=0

    private lateinit var viewModel1: BNA_RD_ViewModel
    private val PDF_FILE_NAMES = mutableListOf<String>()
    private var currentPdfPageIndex: Int = 0

    private lateinit var binding: ActivityPdfZoomFullBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_pdf_zoom_full)

        // Inflate the binding object
        binding = ActivityPdfZoomFullBinding.inflate(layoutInflater)

        // Set the content view to the root view of the binding object
        setContentView(binding.root)


        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = true
        window.statusBarColor = Color.WHITE

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply padding to the activity content (this handles all root layouts properly)
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }


        createRoomDatabase()

        sessionManager = SessionManager1(this)


         index = intent.getIntExtra("indexPage",0)

        Log.e("getIndex",""+index)

        if (index!!.equals(-1)){
            val pdfFileNames = intent.getStringArrayListExtra("pdf_file_names")!!
            val journalData = intent.getSerializableExtra("journal_data") as? JournalLoacalData

            val pdfFileName = ConstanstsApp.getFileNameFromUrl(journalData!!.articleFile)

            setCustomPageViewer1(pdfFileNames,pdfFileName)

        }else{
            val indexPageCount = intent.getIntExtra("indexPage",0)
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
                            binding.eventheader.text=data.month +" "+data.year

                        }

                    // Convert to a list if needed
                    val filteredArticlesList = filteredArticles.toList()

                    // Now filteredArticlesList contains the top issue file and unique article file names, sorted by indexPage
                    Log.d(ConstanstsApp.tag, filteredArticlesList.toString())
                  Log.e("sunilmodi",""+indexPageCount)
                    setCustomPageViewer(filteredArticlesList, startFileName,indexPageCount)






                }



            }



        }






        // Retrieve PDF file names from intent extras
        // val pdfFileNames = intent.getStringArrayListExtra("pdf_file_names")!!.reversed()
/*
        val pdfFileNames = intent.getStringArrayListExtra("pdf_file_names")!!



        val journalData = intent.getSerializableExtra("journal_data") as? JournalLoacalData

//        binding.eventheader.text=journalData!!.title

        val pdfFileName = ConstanstsApp.getFileNameFromUrl(journalData!!.articleFile)*/



        binding.backbutton.setOnClickListener(this)
        binding.journalMenu.setOnClickListener(this)

//        setCustomPageViewer(pdfFileNames,pdfFileName)


    }



    override fun onBackPressed() {
        super.onBackPressed()

        val intent= Intent(this,JournalActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onClick(v: View?) {
        when(v)

        {
            binding.backbutton->
            {
                this.onBackPressed()
            }
            binding.journalMenu -> {
                binding.journalMenu.isEnabled = false
                setJournalBottomDialog()

             /*   // Retrieve the JSON string from the Bundle
                val json = intent.getStringExtra("data")
                val journalData = intent.getSerializableExtra("journal_data") as? JournalLoacalData


// Check if json is not null and not empty
                if (!json.isNullOrEmpty()) {
                    // Convert JSON string back to MutableList<JournalLoacalData>
                    val gson = Gson()
                    val listType = object : TypeToken<MutableList<JournalLoacalData>>() {}.type
                    val journalLocalDataList: MutableList<JournalLoacalData> = gson.fromJson(json, listType)

                    setJournalBottomDialog(journalLocalDataList,journalData)
                    // Now you have your MutableList<JournalLoacalData> restored from the JSON string
                    // Use journalLocalDataList as needed
                } else {
                    // Handle case where json is null or empty
                }*/

            }
        }
    }



    private fun setCustomPageViewer(filteredArticlesList: List<String>, initialStartFileName: String?,inder:Int) {
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
            adapter = this?.let { PdfCompositeAdapter(it, pdfFiles as ArrayList<File>, inder) }
            binding.viewPager.adapter = adapter



            binding.viewPager.setCurrentItem(inder, true)


            /* val transformer = CustomBookFlipAnimation1()
             transformer.setScaleAmountPercent(10f) // Adjust scale amount
             transformer.setEnableScale(true) // Enable scale effect

             // binding.viewPager.setPageTransformer(CustomBookFlipAnimation1())

             binding.viewPager.setPageTransformer(transformer)
 */
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

    private fun setCustomPageViewer1(filteredArticlesList: List<String>, initialStartFileName: String?) {
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
            adapter = this?.let { PdfCompositeAdapter(it, pdfFiles as ArrayList<File>, startPosition) }
            binding.viewPager.adapter = adapter



            binding.viewPager.setCurrentItem(startPosition, true)


            /* val transformer = CustomBookFlipAnimation1()
             transformer.setScaleAmountPercent(10f) // Adjust scale amount
             transformer.setEnableScale(true) // Enable scale effect

             // binding.viewPager.setPageTransformer(CustomBookFlipAnimation1())

             binding.viewPager.setPageTransformer(transformer)
 */
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


    private fun loadPdfPage(position: Int) {
        // Load the PDF page at the given position
        if (position >= 0 && position < pdfFiles!!.size) {
            val pdfFile = pdfFiles!![position]
            // Load the PDF file as needed
            // e.g., adapter?.notifyItemChanged(position)

            adapter?.notifyItemChanged(position)
        }
    }

  /*  private fun setJournalBottomDialog(
        response: MutableList<JournalLoacalData>,
        journalData: JournalLoacalData?
    ) {
        dialog = Dialog(this)
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



        if (response.isNotEmpty()) {
            TextView_no_data.visibility = View.GONE

            Log.d(ConstanstsApp.tag,"response bottom=>"+response.reversed())



            val topItem = journalData



            val year = topItem!!.year.toInt() // Replace with your actual year value from the database or model
            val lastTwoDigits = (year % 100).toString().padStart(2, '0')
            val month = topItem.month // Replace with your actual month value
            val abbreviatedMonth = month.take(3).toUpperCase()






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
            builder.appendColoredText(" $abbreviatedMonth $lastTwoDigits", ContextCompat.getColor(this,R.color.dark_red))

// Now `builder` contains the formatted text with different colors
            val formattedText = builder.toString()

// Use `formattedText` as needed, for example setting it to a TextView


            bottom_header_text.text=builder


            // Filter the list to show only items with month "June"
            // val filteredList = response.filter { it.month.equals(topItem.month, ignoreCase = true) }.sortedBy { it.indexPage }

            val seenTitles = mutableSetOf<String>()
            val filteredList = response
                .filter { it.month.equals(journalData!!.month, ignoreCase = true)&& it.year == journalData.year  } // Filter by month
                .filter {
                    val title = it.author
                    seenTitles.add(title) // Add to set and filter out if already present
                }
                .sortedBy { it.indexPage } // Sort by indexPage



            for(data in filteredList)
            {
                Log.d(ConstanstsApp.tag,"index=>"+data.indexPage+" title=>"+data.title)
            }

            val adapter = JournalBottomAdapter(filteredList, this,this)
            recyclerViewJournalLatest.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerViewJournalLatest.adapter = adapter
            adapter.notifyDataSetChanged()
        } else {
            TextView_no_data.visibility = View.VISIBLE
        }

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
    }*/

    override fun JournalItemClicked(data: JournalLoacalData, position: Int, view: View) {

        dialog.dismiss()

       /* val pdfFileNames = intent.getStringArrayListExtra("pdf_file_names")!!
        setCustomPageViewer(pdfFileNames, getFileNameFromUrl(data.articleFile))*/

       /* val pdfFileName = ConstanstsApp.getFileNameFromUrl(data!!.articleFile)
        val fileDir = getExternalFilesDir("BNA_App_PDF")
        val pdfFile = File(fileDir, pdfFileName)
        initializeViewPager1(pdfFileName)*/

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

                setCustomPageViewer(filteredArticlesList, startFileName,data.indexPage-1)






            }



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


    ///////////////////////////


    private fun setJournalBottomDialog() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.journalMenu.isEnabled = true
        }, 3000)
        dialog = Dialog(this)
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

        viewModel1.get_All_journal.observe(this, Observer { response ->
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

// Append "Vol." + topItem.volume in black color
                builder.appendColoredText("Vol.${topItem.volume}", Color.RED)

// Append " | " in gray color
                builder.appendColoredText(" | ", Color.RED)

// Append "Issue" + topItem.issue_no in black color
                builder.appendColoredText("Issue${topItem.issue_no}", Color.RED)

                builder.appendColoredText(" | ", Color.RED)

// Append " " + abbreviatedMonth + " " + lastTwoDigits in red color
                builder.appendColoredText(" $abbreviatedMonth $lastTwoDigits", Color.RED)

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
                val adapter = JournalBottomAdapter(filteredList, this,this)
                recyclerViewJournalLatest.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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

    private fun createRoomDatabase() {
        val database = BNA_RoomDatabase.getDatabase(this)
        val journalDAO: Journal_DAO = database.Journal_DAO()
        val repository = BNA_RD_Repository(journalDAO, database)
        viewModel1 = ViewModelProvider(this, BNA_RD_ViewModelFactory(repository)).get(BNA_RD_ViewModel::class.java)
    }



   /* private fun  initializeViewPager1(startingPdfFileName: String) {


        val appFolder = File(getExternalFilesDir(null), "BNA_App_PDF")

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
                binding.viewPager.adapter = PdfPagerAdapter(ArrayList(pdfFiles))
                binding.viewPager.adapter?.notifyDataSetChanged()

                // Set current item and apply page transformer
                binding.viewPager.setCurrentItem(currentPdfPageIndex, false) // false for smooth scroll
//               binding.pdfViewPager.setPageTransformer(true, BookFlipPageTransformer())
            }
        }
    }*/

    /////////////////////////////

}