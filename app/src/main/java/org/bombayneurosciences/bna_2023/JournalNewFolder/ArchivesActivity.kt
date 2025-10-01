package org.bombayneurosciences.bna_2023.JournalNewFolder

import android.app.Dialog
import android.app.ProgressDialog
import android.app.ProgressDialog.STYLE_HORIZONTAL
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfRenderer
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import org.bombayneurosciences.bna_2023.Activity.MainActivity
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData
import org.bombayneurosciences.bna_2023.CallBack.JournalDataClick
import org.bombayneurosciences.bna_2023.Data.RetrofitInstance
import org.bombayneurosciences.bna_2023.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ArchivesActivity : AppCompatActivity() , JournalDataClick ,SubSectionAdapter.OnItemClickListener{

    lateinit var viewPager: ViewPager2
    private val journalList = ArrayList<WelcomeElement>()
    private lateinit var caseAdapter: ViewPagerAdapter
    lateinit var backbutton_case: ImageView
    lateinit var journal_menuu1: ImageView
    lateinit var full_screen_back: ImageView
    var articlesList: ArrayList<WelcomeElement>? = null


    lateinit var journal_menu: ImageView
    lateinit var cardview: CardView


    lateinit var dialog:Dialog
    lateinit var coverPagelink:String
    lateinit var year_data:String
    lateinit var month_data:String
    lateinit var vol_data:String
    lateinit var issue_data:String
    lateinit var month:String
    lateinit var coverPage:String

    private lateinit var pdfFile: File
    private lateinit var progressDialog: ProgressDialog

    val handler = Handler(Looper.getMainLooper())
    private lateinit var myRunnable: Runnable



    // Declare globally
    lateinit var sortedByAge: ArrayList<WelcomeElement>
    // Declare globally
    lateinit var sortedByAge1: ArrayList<WelcomeElement>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_archives)


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



        /////


       /* // Retrieve the list from the Intent
         articlesList= intent.getParcelableArrayListExtra("EXTRA_ARTICLES_LIST")

        // Check if articlesList is not null and use it
        articlesList?.let { list ->
            for (article in list) {
                // Process each article
                Log.d("NewActivity", "Article ID: ${article.id}, Name: ${article.index_page}, Description: ${article.month}")
            }
        } ?: run {
            Log.e("NewActivity", "No articles received")
        }*/
         month = intent.getStringExtra("month")!!
         coverPage = intent.getStringExtra("coverPage")!!
         sortedByAge1 = intent.getParcelableArrayListExtra<WelcomeElement>("articles") ?: arrayListOf()


        Log.e("hhhhhhhhhhh ",""+ month)
        Log.e("hhhhhhhhhhh ",""+ coverPage)

        pdfFile = File(cacheDir, month)

     /*   sortedByAge1 = ArrayList(articlesList!!.sortedBy { it.index_page })

        println("People sorted by age: $sortedByAge1")
        val gson2 = Gson()
        val json2 = gson2.toJson(sortedByAge1)
        Log.e("brocodeCaheOfMonth0"," "+json2)*/


        /////

        viewPager = findViewById(R.id.viewPager2)

        backbutton_case = findViewById(R.id.backbutton_case)
        full_screen_back = findViewById(R.id.full_screen_back)

        journal_menu = findViewById(R.id.journal_menuu)
        cardview = findViewById(R.id.cardview)
        journal_menuu1 = findViewById(R.id.journal_menuu1)



        progressDialog=ProgressDialog(this)
        progressDialog.setTitle("please wait...")
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(STYLE_HORIZONTAL)
        progressDialog.isIndeterminate = true
//        progressDialog.setProgressStyle(STYLE_HORIZONTAL)
        progressDialog.show()


        caseAdapter = ViewPagerAdapter(this, mutableListOf(),null,this ,this)
        viewPager.adapter = caseAdapter


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

//            private var lastPosition = 1

            private var currentPosition = 0
            private var nextPosition = 0

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Update the current position when the page changes
//                nextPosition = position
                // This is where you can get the current page index
                Log.d("ViewPager2", "Current page number: $position")



                if (position==0){

                }else{

                    handler.removeCallbacks(myRunnable)

                    cardview.visibility = View.GONE
                    full_screen_back.visibility = View.VISIBLE
                    journal_menu.visibility = View.VISIBLE
                    journal_menuu1.visibility = View.VISIBLE
                    refreshPageContent(position)

                    myRunnable = Runnable {

                        cardview.visibility = View.GONE
                        full_screen_back.visibility = View.GONE
                        journal_menu.visibility = View.GONE
                        journal_menuu1.visibility = View.GONE
                    }

                    handler.postDelayed(myRunnable, 5000)


                }
            }

        })




        makeApiCall()

        backbutton_case.setOnClickListener {
            finish()
        }
        full_screen_back.setOnClickListener {
            finish()
        }

        journal_menuu1.setOnClickListener {
            finish()
        }
        journal_menu.setOnClickListener {

            journal_menu.isEnabled = false
            setJournalBottomDialog()
        }


    }




    private fun refreshPageContent(position: Int) {
        // Implement your logic to refresh the content of the page
        // For example, you can reload data or update UI components based on the position
        Log.d("ViewPager2", "Refreshing content for page: $position")

        // Assuming you have a method in your adapter to refresh the content
        caseAdapter.refreshItem(position)
    }




    private fun makeApiCall() {


        val call = RetrofitInstance.apiServiceCases.getCases1()

        call.enqueue(object : Callback<Welcome> {
            override fun onResponse(call: Call<Welcome>, response: Response<Welcome>) {
                journalList.clear()

                //  years  filter
                val data = response.body()
//                val data = articleList


// Get the unique years from the data
                val years = data!!.map { it.year }.toSet().toList()

// Create an ArrayList to store the year articles
                val yearArticlesList = ArrayList<YearArticles1>()
                // Create an ArrayList to store the month articles


// Loop through each year
                years.forEach { year ->
                    // Filter the articles for the current year
                    val filteredData = data!!.filter { it.year == year }

                    val monthArticlesList = ArrayList<MonthArticles>()

                    // Get the unique months from the filtered data
                    val months = filteredData.map { it.month }.toSet().toList()

                    // Loop through each month
                    months.forEach { month ->
                        // Filter the articles for the current month
                        val filteredMonthData = filteredData.filter { it.month == month }


                        // Create a MonthArticles object for the current month
                        val monthArticles = MonthArticles(month, filteredMonthData)

                        // Add the MonthArticles object to the ArrayList
                        monthArticlesList.add(monthArticles)
                    }

                    // Create a YearArticles object for the current year
                    val yearArticles = YearArticles1(year, monthArticlesList)

                    // Add the YearArticles object to the ArrayList
                    yearArticlesList.add(yearArticles)
                }

// Now yearArticlesList contains all the articles from different years and months
                println(yearArticlesList)

// Convert the yearArticlesList to JSON
                val gson = Gson()
                val json = gson.toJson(yearArticlesList)

                Log.e("brocodeCaheOfMonth0"," "+json)


                ////////////////////





                // Find the latest year and month


                ///////////////////////



                // Get the latest year
                val latestYear = yearArticlesList.maxByOrNull { it.year.toInt() }?.year

// Filter the yearArticlesList to get the latest year's data
                val latestYearArticlesList = yearArticlesList.filter { it.year == latestYear }

// If you want to get the articles of the latest year, you can do it like this:
                val latestYearArticles = latestYearArticlesList.flatMap { it.months.flatMap { it.articles } }

// Now latestYearArticles contains all the articles from the latest year
                println(latestYearArticles)
                val gson2 = Gson()
                val json2 = gson2.toJson(latestYearArticles)
                Log.e("brocodeCaheOfMonth0"," "+json2)

// Convert the latestYearArticles to JSON


                //////////////////////////////////////////////////




                // Get the latest month from the latest year
                val latestMonth = latestYearArticlesList
                    .flatMap { it.months }
                    .minByOrNull { it.month.toString() } // Assuming the month is in a format like "01" for January
                    ?.month

// Filter the articles for the latest month
                val latestMonthArticlesNew = latestYearArticlesList
                    .flatMap { it.months }
                    .filter { it.month == month }
                    .flatMap { it.articles }
/////////////////////////////////////////////

////////////////////////////////


                // Sort by age
//                 sortedByAge = ArrayList(latestMonthArticlesNew.sortedBy { it.index_page })
                 sortedByAge = ArrayList(sortedByAge1.sortedBy { it.index_page })

                println("People sorted by age: $sortedByAge")

                val gson1 = Gson()
                val json1 = gson1.toJson(sortedByAge)

                /*  // Get the latest year
                  val latest = latestYearArticles.minByOrNull { it.index_page!!.toLong() }?.index_page

                  // Filter the latestYearArticles based on the "index_page" field
                  val filteredLatestYearArticles = latestYearArticles.filter { it.index_page == "latest" }

  // Save the filtered data in a separate ArrayList
                  val filteredArrayList = ArrayList(filteredLatestYearArticles)

  // Now filteredArrayList contains the filtered data
                  println(filteredArrayList)
  */

////////////////////////////////////////////////


                // Loop through the array and get each element starting from index 0
                for (i in 0 until sortedByAge.size) {
                    coverPagelink = sortedByAge[0].cover.toString()
                    year_data = sortedByAge[0].year.toString()
                    month_data = sortedByAge[0].month.toString()
                    vol_data = sortedByAge[0].volume.toString()
                    issue_data = sortedByAge[0].issue_no.toString()

                    Log.e("coverPage",""+coverPagelink)
                }



                // Always delete existing PDF
                if (pdfFile.exists()) {
                    val deleted = pdfFile.delete()


                    // PDF file has already been downloaded, render it directly
//                    renderPdf()
//                    renderPdf()?.let { caseAdapter.updateData(sortedByAge, it) }

//                    Toast.makeText(this@ArchivesActivity,"200", Toast.LENGTH_SHORT).show()


                    myRunnable = Runnable {

                        cardview.visibility = View.GONE
                        full_screen_back.visibility = View.GONE
                        journal_menu.visibility = View.GONE
                        journal_menuu1.visibility = View.GONE
                    }
                    handler.postDelayed(myRunnable,5000)
                    Log.d("PDF", "Old PDF deleted: $deleted")
                }

//                progressDialog.dismiss()
                // Always download latest PDF
                DownloadPdfTask().execute(coverPagelink)

                Log.e("exitPage", "downloading new PDF: $coverPagelink")


                //TODO CHNAGE NEW
               /* if (pdfFile.exists()) {
                    progressDialog.dismiss()
                    // PDF file has already been downloaded, render it directly
                    renderPdf()
                    renderPdf()?.let { caseAdapter.updateData(sortedByAge, it) }

//                    Toast.makeText(this@ArchivesActivity,"200", Toast.LENGTH_SHORT).show()


                    myRunnable = Runnable {

                        cardview.visibility = View.GONE
                        full_screen_back.visibility = View.GONE
                        journal_menu.visibility = View.GONE
                        journal_menuu1.visibility = View.GONE
                    }
                    handler.postDelayed(myRunnable,5000)

                    Log.e("exitPage ",""+"exitPage")

                } else {
                    // PDF file has not been downloaded, download it first
                    DownloadPdfTask().execute(coverPagelink)
                }*/
//                DownloadPdfTask().execute(coverPagelink)
                Log.e("exitPage ",""+"donwload")
                Log.e("exitPage ",""+coverPagelink)
                Log.e("brocodeCaheOfMonth0"," "+json1)
//                caseAdapter.updateData(sortedByAge,)

                ////////////////////


            }

            override fun onFailure(call: Call<Welcome>, t: Throwable) {
                Log.e("brocodeCaheOfMonth"," "+call.request())

            }


        })
    }


    private inner class DownloadPdfTask : AsyncTask<String, Void, Bitmap>() {

        override fun doInBackground(vararg params: String): Bitmap? {
            return downloadPdf(params[0])
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            super.onPostExecute(bitmap)
            if (bitmap != null) {
//                imageView.setImageBitmap(bitmap)

                Log.e("show_data_list",""+bitmap)
//                progressDialog.dismiss()
                caseAdapter.updateData(sortedByAge,bitmap)

                myRunnable = Runnable {

                    cardview.visibility = View.GONE
                    full_screen_back.visibility = View.GONE
                    journal_menu.visibility = View.GONE

                }


           handler.postDelayed(myRunnable, 5000)
                progressDialog.dismiss()
            }
        }

        private fun downloadPdf(url: String): Bitmap? {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()
                val inputStream = connection.inputStream
                val fileOutputStream = FileOutputStream(pdfFile)
                inputStream.copyTo(fileOutputStream)
                fileOutputStream.close()

                return renderPdf()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }

    private fun renderPdf(): Bitmap? {
        try {
            val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))
            val page = pdfRenderer.openPage(0)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            pdfRenderer.close()
            return bitmap
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    private fun setJournalBottomDialog() {
        Handler(Looper.getMainLooper()).postDelayed({
            journal_menu.isEnabled = true
        }, 3000)


        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_journal)

        val recyclerViewJournalLatest = dialog.findViewById<RecyclerView>(R.id.RecyclerView_journal_lastest)
        val TextView_no_data = dialog.findViewById<TextView>(R.id.TextView_no_data)

        val TextView_index=dialog.findViewById<ImageView>(R.id.TextView_index)

        val bottom_header_text = dialog.findViewById<TextView>(R.id.bottom_header_text)

        bottom_header_text.text =   month_data+" " + year_data+" : "+"Vol."+vol_data+" | Issue" +issue_data

        TextView_index.setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View?) {
                dialog.dismiss()
            }

        })


        val adapter = JournalBottomAdapterNew(sortedByAge, this,this)
        recyclerViewJournalLatest.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewJournalLatest.adapter = adapter
        adapter.notifyDataSetChanged()



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

    }

    override fun JournalItemClicked1(data: WelcomeElement, position: Int, view: View) {
        Log.e("show_data_list",""+position)
        viewPager.setCurrentItem(position+1, false)
        cardview.visibility = View.GONE
        full_screen_back.visibility = View.VISIBLE
        journal_menu.visibility = View.VISIBLE
        journal_menuu1.visibility = View.VISIBLE



        myRunnable = Runnable {


            cardview.visibility = View.GONE
            full_screen_back.visibility = View.GONE
            journal_menu.visibility = View.GONE
            journal_menuu1.visibility = View.GONE

        }

     handler.postDelayed(myRunnable, 5000)

        dialog.dismiss()


    }

    override fun ItemClicked(data: Bitmap, position: Int) {
        handler.removeCallbacks(myRunnable)

        cardview.visibility = View.GONE
        full_screen_back.visibility = View.VISIBLE
        journal_menu.visibility = View.VISIBLE
        journal_menuu1.visibility = View.VISIBLE


        myRunnable = Runnable {
            cardview.visibility = View.GONE
            full_screen_back.visibility = View.GONE
            journal_menu.visibility = View.GONE
            journal_menuu1.visibility = View.GONE

        }

    handler.postDelayed(myRunnable, 5000)
    }

    override fun onInnerItemClick(item: Subsection, position: Int) {

        handler.removeCallbacks(myRunnable)
        cardview.visibility = View.GONE
        full_screen_back.visibility = View.VISIBLE
        journal_menu.visibility = View.VISIBLE
        journal_menuu1.visibility = View.VISIBLE



        myRunnable = Runnable {

            cardview.visibility = View.GONE
            full_screen_back.visibility = View.GONE
            journal_menu.visibility = View.GONE
            journal_menuu1.visibility = View.GONE

        }
      handler.postDelayed(myRunnable, 5000)
    }
}