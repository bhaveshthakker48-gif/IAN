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
import android.view.GestureDetector
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bombayneurosciences.bna_2023.Activity.MainActivity
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData
import org.bombayneurosciences.bna_2023.CallBack.JournalDataClick
import org.bombayneurosciences.bna_2023.Data.RetrofitInstance
import org.bombayneurosciences.bna_2023.R
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class StartActivity : AppCompatActivity(), JournalDataClick, SubSectionAdapter.OnItemClickListener  {

    private val journalList = ArrayList<WelcomeElement>()
    private lateinit var caseAdapter: ViewPagerAdapter
    lateinit var latest_radio_button: TextView
    lateinit var archives_radio_button: TextView
    lateinit var backbutton_case: ImageView
    lateinit var pdfView: ImageView
    lateinit var commeteText: TextView

    lateinit var journal_menu_arch: ImageView
    lateinit var backbutton_case1: ImageView

    lateinit var cardview: CardView
    lateinit var layout_radio: MaterialCardView
    lateinit var journal_menu: ImageView
    lateinit var main: RelativeLayout

    lateinit var full_screen_back: ImageView
    lateinit var viewPager: ViewPager2
    lateinit var cardView: MaterialCardView
    lateinit var dialog:Dialog
    private lateinit var progressDialog: ProgressDialog
     lateinit var coverPagelink:String
     lateinit var year_data:String
     lateinit var month_data:String
     lateinit var vol_data:String
     lateinit var issue_data:String
      var indexPage:Int = 0
//    private val file = File(cacheDir, "pdf")

    private lateinit var pdfFile: File
    private lateinit var pdfFile1: File


    // Declare globally
    lateinit var sortedByAge: ArrayList<WelcomeElement>
    lateinit var filterData: ArrayList<MonthArticles1>
    lateinit var sortedByAge1: ArrayList<WelcomeElement>

    private lateinit var gestureDetector: GestureDetector
    val handler = Handler(Looper.getMainLooper())
    private lateinit var myRunnable: Runnable

    companion object {
      public  var screenchange = "1"
    }

    var touchscreen = "1"

     lateinit var  recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_start)

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


        latest_radio_button = findViewById(R.id.latest_radio_buttonn)
        archives_radio_button = findViewById(R.id.archives_radio_buttonn)
        viewPager = findViewById(R.id.viewPager)
        cardView = findViewById(R.id.cardView)
        backbutton_case = findViewById(R.id.backbutton_case)
        backbutton_case1 = findViewById(R.id.backbutton_case1)
        recyclerView = findViewById(R.id.recyclerview)

        commeteText = findViewById(R.id.commeteText)
        pdfView = findViewById(R.id.pdfView)
        journal_menu_arch = findViewById(R.id.journal_menu_arch)
        full_screen_back = findViewById(R.id.full_screen_back)


        journal_menu = findViewById(R.id.journal_menu)
        cardview = findViewById(R.id.cardview)
        layout_radio = findViewById(R.id.layout_radio)
        main = findViewById(R.id.main)

        pdfFile = File(cacheDir, "pdf")
        pdfFile1 = File(cacheDir, "pdf1")

        pdfFile.delete()
        pdfFile1.delete()


        progressDialog=ProgressDialog(this)
        progressDialog.setTitle("please wait...")
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(STYLE_HORIZONTAL)
        progressDialog.isIndeterminate = true
//        progressDialog.setProgressStyle(STYLE_HORIZONTAL)
        progressDialog.show()


        caseAdapter = ViewPagerAdapter(this, mutableListOf(),null,this,this )
        viewPager.adapter = caseAdapter


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // This is where you can get the current page index
                Log.d("ViewPager2", "Current page number: $position")

                if (position==0){

                    indexPage = position

                }else{
                    indexPage=position
                    handler.removeCallbacks(myRunnable)
                    cardview.visibility = View.GONE

                    //visible
                    full_screen_back.visibility = View.VISIBLE
                    layout_radio.visibility = View.GONE
                    journal_menu.visibility = View.VISIBLE
                    journal_menu_arch.visibility = View.VISIBLE
                    commeteText.visibility = View.GONE



                    myRunnable = Runnable {

                        cardview.visibility = View.GONE
                        layout_radio.visibility = View.GONE
                        journal_menu.visibility = View.GONE
                        journal_menu_arch.visibility = View.GONE
                        full_screen_back.visibility = View.GONE
                        commeteText.visibility = View.GONE
                    }

                    handler.postDelayed(  myRunnable,5000)

                    refreshPageContent(position)
                }
            }

        })




        latest_radio_button.setBackgroundResource(R.drawable.radio_flat_regular1)
        latest_radio_button.setTextColor(Color.WHITE)


        /////////////////////////

        //TODO

        val linearLayoutManager = LinearLayoutManager(baseContext)
        recyclerView.setHasFixedSize(true)
        val dividerItemDecoration =
            DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerView.setItemAnimator(DefaultItemAnimator())
        recyclerView.setLayoutManager(linearLayoutManager)


        ///////////////////////////


        makeApiCall()



        // Set a click listener for the back button
        latest_radio_button.setOnClickListener {

            viewPager.setCurrentItem(0, true)
            touchscreen = "1"
            latest_radio_button.setBackgroundResource(R.drawable.radio_flat_regular1)
            latest_radio_button.setTextColor(Color.WHITE)


            archives_radio_button.setBackgroundResource(R.drawable.radio_flat_regular)
            archives_radio_button.setTextColor(Color.BLACK)


            makeApiCall()
            viewPager.visibility = View.VISIBLE
            cardView.visibility = View.GONE
            journal_menu.visibility = View.VISIBLE

        }

        cardView.setOnClickListener {
            val intent = Intent(this, ArchivesActivity::class.java)
            startActivity(intent)
        }



        Log.e("screenchn1",""+ screenchange)





        backbutton_case.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        full_screen_back.setOnClickListener {

            if (indexPage==0){

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                Log.e("indexPage1 ",""+indexPage)

            }else{
                commeteText.text = " Journal"

                commeteText.visibility = View.VISIBLE
                cardview.visibility = View.VISIBLE
                layout_radio.visibility = View.VISIBLE
                backbutton_case.visibility = View.VISIBLE
                full_screen_back.visibility = View.GONE
                Log.e("indexPage ",""+indexPage)
                viewPager.setCurrentItem(0, true)
            }


        }
        journal_menu.setOnClickListener {
            journal_menu.isEnabled = false
            setJournalBottomDialog()
        }


        backbutton_case1.setOnClickListener {
            handler.removeCallbacks(myRunnable)
            touchscreen = "3"
            commeteText.text = " Journal"

            commeteText.visibility = View.VISIBLE
            backbutton_case1.visibility = View.GONE
            backbutton_case.visibility = View.VISIBLE
            latest_radio_button.callOnClick()
            /*latest_radio_button.setBackgroundResource(R.drawable.radio_flat_regular1)
            latest_radio_button.setTextColor(Color.WHITE)


            archives_radio_button.setBackgroundResource(R.drawable.radio_flat_regular)
            archives_radio_button.setTextColor(Color.BLACK)

            viewPager.visibility = View.VISIBLE
            cardView.visibility = View.GONE
            journal_menu.visibility = View.GONE
            backbutton_case1.visibility = View.GONE
            backbutton_case.visibility = View.VISIBLE
            makeApiCall()*/

    Log.e("touchscreen ",""+touchscreen)

        }
        journal_menu_arch.setOnClickListener {

            handler.removeCallbacks(myRunnable)
            indexPage =0
            touchscreen="2"
            archives_radio_button.setBackgroundResource(R.drawable.radio_flat_regular1)
            archives_radio_button.setTextColor(Color.WHITE)

            latest_radio_button.setTextColor(Color.BLACK)
            latest_radio_button.setBackgroundResource(R.drawable.radio_flat_regular)

            viewPager.visibility = View.GONE
            journal_menu.visibility = View.GONE
            cardView.visibility = View.VISIBLE
            cardview.visibility = View.VISIBLE
            layout_radio.visibility = View.VISIBLE
            backbutton_case1.visibility = View.VISIBLE
            backbutton_case.visibility = View.GONE
            full_screen_back.visibility = View.GONE
            journal_menu_arch.visibility = View.GONE

            commeteText.text = "        Journal"

            commeteText.visibility = View.VISIBLE



            if (pdfFile1.exists()) {
                progressDialog.dismiss()
                // PDF file has already been downloaded, render it directly
                renderPdf1()
                pdfView.setImageBitmap(renderPdf1())

            } else {
                // PDF file has not been downloaded, download it first
                DownloadPdfTask2().execute("https://telemedocket.com/BNA/public/uploads/Issues_file/BNA_Journal_Cover_Pic.pdf")
//
            }

           /* Log.d("TouchEventt  ", "touchscreen")
            Handler(Looper.getMainLooper()).postDelayed({
                // Hide loader after 3000 milliseconds (3 seconds) and start MainActivity

                cardview.visibility = View.VISIBLE
                layout_radio.visibility = View.VISIBLE
                journal_menu.visibility = View.GONE

            }, 4000)
*/

        }


        // Set a click listener for the back button
        archives_radio_button.setOnClickListener {

            handler.removeCallbacks(myRunnable)

            touchscreen="2"
            archives_radio_button.setBackgroundResource(R.drawable.radio_flat_regular1)
            archives_radio_button.setTextColor(Color.WHITE)

            latest_radio_button.setTextColor(Color.BLACK)
            latest_radio_button.setBackgroundResource(R.drawable.radio_flat_regular)

          /*  viewPager.visibility = View.GONE
            journal_menu.visibility = View.GONE
            cardView.visibility = View.VISIBLE
            cardview.visibility = View.VISIBLE
            layout_radio.visibility = View.VISIBLE*/


            viewPager.visibility = View.GONE
            journal_menu.visibility = View.GONE
            cardView.visibility = View.VISIBLE
            cardview.visibility = View.VISIBLE
            layout_radio.visibility = View.VISIBLE
            backbutton_case1.visibility = View.VISIBLE
            backbutton_case.visibility = View.GONE
            journal_menu_arch.visibility = View.GONE

            commeteText.text = "        Journal"

            commeteText.visibility = View.VISIBLE



            if (pdfFile1.exists()) {
                progressDialog.dismiss()
                // PDF file has already been downloaded, render it directly
                renderPdf1()
               pdfView.setImageBitmap(renderPdf1())

            } else {
                // PDF file has not been downloaded, download it first
                DownloadPdfTask2().execute("https://telemedocket.com/BNA/public/uploads/Issues_file/BNA_Journal_Cover_Pic.pdf")
//
            }

            Log.d("TouchEventt  ", "touchscreen")
           Handler(Looper.getMainLooper()).postDelayed({
                // Hide loader after 3000 milliseconds (3 seconds) and start MainActivity

                cardview.visibility = View.VISIBLE
                layout_radio.visibility = View.VISIBLE
                journal_menu.visibility = View.GONE
               commeteText.visibility = View.VISIBLE

            }, 5000)

//              makeApiCall()
        }




    }


    ////////////////////

    //TODO

    private fun showPatientList(patientList: List<MonthArticles1>) {

            val adapter = MOnthFilterDataAdapter(this, patientList)
            recyclerView.setAdapter(adapter)


    }


    private fun refreshPageContent(position: Int) {
        // Implement your logic to refresh the content of the page
        // For example, you can reload data or update UI components based on the position
        Log.d("ViewPager2", "Refreshing content for page: $position")

        // Assuming you have a method in your adapter to refresh the content
        caseAdapter.refreshItem(position)

//        viewPager.setCurrentItem(position, false)
    }


    ////////////////////////
//TODO DEMI

    fun extractArticles(data: ArrayList<MonthArticles1>): ArrayList<WelcomeElement> {
        val extractedArticles = arrayListOf<WelcomeElement>()

        data.forEach { monthArticles ->
            extractedArticles.addAll(monthArticles.articles)
        }

        return extractedArticles
    }

    fun filterAndPrintAllArticlesInSequence(welcomeData: ArrayList<MonthArticles1>): ArrayList<MonthArticles1> {
        // Map to convert month names to numbers for sorting
        val monthMap = mapOf(
            "January" to 1, "February" to 2, "March" to 3,
            "April" to 4, "May" to 5, "June" to 6,
            "July" to 7, "August" to 8, "September" to 9,
            "October" to 10, "November" to 11, "December" to 12
        )

        // Group by year first, then by month within each year
        val groupedData = welcomeData.groupBy { it.year }.mapValues { entry ->
            entry.value.groupBy { it.month }
        }

        // Sort years in descending order
        val sortedYears = groupedData.keys.sortedDescending()

        // Create a sorted list of MonthArticles1
        val sortedArticles = arrayListOf<MonthArticles1>()

        sortedYears.forEach { year ->
            // For each year, sort months in descending order
            val sortedMonths = groupedData[year]?.keys?.sortedByDescending { monthMap[it] ?: 0 }

            sortedMonths?.forEach { month ->
                // Get articles for the given month and year
                val articles = groupedData[year]?.get(month) ?: emptyList()

                // Add the sorted articles to the final list
                sortedArticles.addAll(articles)
            }
        }

        return sortedArticles
    }


    ///////////////////////


    private fun makeApiCall() {
        if (touchscreen.equals("3")) {
            layout_radio.visibility = View.GONE
        }
        val call = RetrofitInstance.apiServiceCases.getCases1()

        call.enqueue(object : Callback<Welcome> {
            override fun onResponse(call: Call<Welcome>, response: Response<Welcome>) {
                journalList.clear()
///////////////////////////////////////////

                //  years  filter
                val data = response.body()

                //TODO
//                val data = welcomeData
//                val data = articleList

// Get the unique years from the data
                val years = data!!.map { it.year }.toSet().toList()

// Create an ArrayList to store the year articles
                val yearArticlesList = ArrayList<YearArticles2>()
                val monthArticlesList1 = ArrayList<MonthArticles1>()
                // Create an ArrayList to store the month articles


// Loop through each year
                years.forEach { year ->
                    // Filter the articles for the current year
                    val filteredData = data!!.filter { it.year == year }

                    val monthArticlesList = ArrayList<MonthArticles>()


                    // Get the unique months from the filtered data
                    val months = filteredData.map { it.month }.toSet().toList()
                    val cover = filteredData.map { it.cover }.toSet().toList()

                    // Loop through each month
                    months.forEach { month ->
                        // Filter the articles for the current month
                        val filteredMonthData = filteredData.filter { it.month == month }

                        // Extract the cover for the current month
                        val coverForMonth = filteredMonthData.map { it.cover }.distinct().firstOrNull()
                        val coverForyear = filteredMonthData.map { it.year }.distinct().firstOrNull()
                        val coverForIsssue = filteredMonthData.map { it.issue_no }.distinct().firstOrNull()
                        val coverForVolume = filteredMonthData.map { it.volume }.distinct().firstOrNull()

                        // Create a MonthArticles object for the current month
                        val monthArticles = MonthArticles1(month,coverForMonth!!,coverForyear!!,coverForIsssue!!.toString(),coverForVolume!!.toString(), filteredMonthData)

                        // Add the MonthArticles object to the ArrayList
                        monthArticlesList1.add(monthArticles)



                    }



                    // Create a YearArticles object for the current year
                    val yearArticles = YearArticles2(year, monthArticlesList1)

                    // Add the YearArticles object to the ArrayList
                    yearArticlesList.add(yearArticles)
                }


                //    right list index accroding all month with year
                val gsonn = Gson()
                val jsonnn = gsonn.toJson(monthArticlesList1)

                Log.e("brocodeCaheOfMonth0"," "+jsonnn)
//                showPatientList(monthArticlesList1)
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //TODO DEMI

                // Get the filtered articles
                val filteredArticles = filterAndPrintAllArticlesInSequence(monthArticlesList1)



                // Sort and print the articles
//                val sortedArticles = sortArticlesByYearAndMonth(monthArticlesList1)

                val gsonjjj = Gson()
                val jsonkkkk = gsonjjj.toJson(filteredArticles)
                Log.e("brocodeCaheOfMonth0"," "+jsonkkkk)
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Convert the yearArticlesList to JSON       list show difrant year 25 ,24 23 index according list with year
                val gson = Gson()
                val json = gson.toJson(yearArticlesList)

                Log.e("brocodeCaheOfMonth0"," "+json)


                ////////////////////

                val jsonString = json.toString()

                try {
                    // Parse the JSON array from the string
                    val jsonArray = JSONArray(jsonString)

                    // Extract the first object (index 0) from the JSON array
                    val firstObject = jsonArray.getJSONObject(0)

                    // Extract the 'months' array
                    val monthsArray = firstObject.getJSONArray("months")

                    val jsonArray1 = JSONArray(monthsArray.toString())
//                    val jsonArray1 = JSONArray(jsonkkkk)
                    val monthArticlesList1 = ArrayList<MonthArticles1>()

                    // Create a map of month names to their corresponding numerical values
                    val monthMap = mapOf(
                        "January" to 1,
                        "February" to 2,
                        "March" to 3,
                        "April" to 4,
                        "May" to 5,
                        "June" to 6,
                        "July" to 7,
                        "August" to 8,
                        "September" to 9,
                        "October" to 10,
                        "November" to 11,
                        "December" to 12
                    )



                        // Parse the JSON array from the string
//                        val jsonArray = JSONArray(monthsArray.toString())

                        // Track the index of the latest month
                        var latestMonthIndex = -1
                        var latestMonthValue = -1
                        var latestYear = -1



                    for (i in 0 until jsonArray1.length()) {
                        val articleObject = jsonArray1.getJSONObject(i)
//                        val articlesArray = articleObject.getJSONArray("articles")
                        val articlesArray = articleObject.getJSONArray("articles")
                        val month = articlesArray.getJSONObject(0).getString("month")
                        val year =  articlesArray.getJSONObject(0).getInt("year")

                        // Compare the current month with the latest month found

                        //TODO
                        val monthValue = monthMap[month] ?: 0
                        if (monthValue > latestMonthValue) {
                            latestMonthValue = monthValue
                            latestMonthIndex = i
                        }


                    }

                        // If a latest month was found, remove it
                        if (latestMonthIndex != -1) {
                            jsonArray1.remove(latestMonthIndex)
                        }

                        // Print the updated JSON array



                 /*   val gsonn = Gson()
                    val jsonnn = gsonn.toJson(monthArticlesList1)*/
//  latest year with month remove list
                    println("Updated JSON: $jsonArray1")



                    // Parse the JSON array
                    val gson = Gson()
                    val articleListType = object : TypeToken<List<MonthArticles1>>() {}.type
                    val articleList: ArrayList<MonthArticles1> = gson.fromJson(jsonArray1.toString(), articleListType)

//                    filterData = ArrayList(articleList..sortedBy { it.index_page })

                      val gsonn = Gson()
                    val jsonnn = gsonn.toJson(articleList)
                    println("Updated JSON: $jsonnn")

//                    showPatientList(articleList.reversed())


                    ///////////////////

                    //TODO
                    val filteredList = ArrayList(filteredArticles.drop(1))
                    val gson11 = Gson()
                    val json11 = gson11.toJson(filteredList)


                    /////////////////////


//                    showPatientList(articleList)
                    showPatientList(filteredList)



////////////////////////////////////////////

                } catch (e: Exception) {
                    e.printStackTrace()
                }



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
                    .maxByOrNull { it.month.toString() } // Assuming the month is in a format like "01" for January
                    ?.month

// Filter the articles for the latest month
                val latestMonthArticlesNew = latestYearArticlesList
                    .flatMap { it.months }
                    .filter { it.month == latestMonth }
                    .flatMap { it.articles }
/////////////////////////////////////////////



////////////////////////////////

                // Create a new ArrayList excluding the element at index 0
               /* val filteredList = ArrayList(filteredArticles.drop(1))
                val gson11 = Gson()
                val json11 = gson11.toJson(filteredList) */ //  0 index remove list



                // Check if the list is not empty to avoid IndexOutOfBoundsException
                val filteredList1 = ArrayList<MonthArticles1>()    // 0 index save in new arraylist
                if (filteredArticles.isNotEmpty()) {

                    // Add the element at index 0 to the new ArrayList
                    filteredList1.add(filteredArticles[0])
                }
                val gson10 = Gson()
                val json10 = gson10.toJson(filteredList1)


                val extractedArticles = extractArticles(filteredList1)

                val gson9 = Gson()
                val json9 = gson9.toJson(extractedArticles)




/////////////////////////////////////////////////////////////////////
                // Sort by age
                //TODO
//                 sortedByAge = ArrayList(latestMonthArticlesNew.sortedBy { it.index_page })
                 sortedByAge = ArrayList(extractedArticles.sortedBy { it.index_page })


                println("People sorted by age: $sortedByAge")






//                 sortedByAge1 = (listOf(newArticle)+sortedByAge) as ArrayList<WelcomeElement>

                val gson1 = Gson()
                val json1 = gson1.toJson(sortedByAge)

                // Loop through the array and get each element starting from index 0
                for (i in 0 until sortedByAge.size) {
                    coverPagelink = sortedByAge[0].cover.toString()
                    year_data = sortedByAge[0].year.toString()
                    month_data = sortedByAge[0].month.toString()
                    vol_data = sortedByAge[0].volume.toString()
                    issue_data = sortedByAge[0].issue_no.toString()

                    Log.e("coverPage",""+coverPagelink)
                }
//                DownloadPdfTask().execute(coverPagelink)


                if (pdfFile.exists()) {
                    progressDialog.dismiss()
                    // PDF file has already been downloaded, render it directly
//                    val deleted = pdfFile.delete()
                    renderPdf()
                    renderPdf()?.let { caseAdapter.updateData(sortedByAge, it) }

                    Log.e("one ",""+"1")

                    if (touchscreen.equals("1")){

//                        Toast.makeText(this@StartActivity,"200",Toast.LENGTH_SHORT).show()

                        myRunnable = Runnable {
                            cardview.visibility = View.GONE
                            layout_radio.visibility = View.GONE
                            journal_menu.visibility = View.GONE
                            journal_menu_arch.visibility = View.GONE


//                            handler.removeCallbacks(myRunnable)

                            //gone
                            cardview.visibility = View.GONE

                            //visible
                            full_screen_back.visibility = View.VISIBLE

                            layout_radio.visibility = View.GONE
                            journal_menu.visibility = View.VISIBLE
                            journal_menu_arch.visibility = View.VISIBLE
                            commeteText.visibility = View.GONE
                            myRunnable = Runnable {
                                cardview.visibility = View.GONE
                                layout_radio.visibility = View.GONE
                                journal_menu.visibility = View.GONE
                                journal_menu_arch.visibility = View.GONE
                                full_screen_back.visibility = View.GONE
                                commeteText.visibility = View.GONE
                            }
                            handler.postDelayed(myRunnable, 5000)


                        }

                       handler.postDelayed(myRunnable,5000)

                        ////////////////  5 sec icon show




                        Log.e("one ",""+"2")
                    }else if (touchscreen.equals("3")){
                        myRunnable = Runnable {
                            cardview.visibility = View.GONE
                            layout_radio.visibility = View.GONE
                            journal_menu.visibility = View.GONE
                            journal_menu_arch.visibility = View.GONE
                        }

                        handler.postDelayed(myRunnable,1000)
                        Log.e("one ",""+"3")
                    }
                    else if (touchscreen.equals("2")){

                        cardview.visibility = View.VISIBLE
                        layout_radio.visibility = View.VISIBLE
                        journal_menu.visibility = View.VISIBLE
                        journal_menu_arch.visibility = View.GONE

                        Log.e("one ",""+"4")

                    }



                }
                else {
//                progressDialog.dismiss()
                    // PDF file has not been downloaded, download it first
                    DownloadPdfTask().execute(coverPagelink)
                    Log.e("one ",""+"5")
                }
                Log.e("brocodeCaheOfMonth1"," "+json1)
//                caseAdapter.updateData(sortedByAge,coverPagelink)

                ////////////////////


            }

            override fun onFailure(call: Call<Welcome>, t: Throwable) {
                Log.e("brocodeCaheOfMonth"," "+call.request())
                      progressDialog.dismiss()
            }


        })
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

        TextView_index.setOnClickListener(object :View.OnClickListener
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

        // setFragment(JournalViewFragment(),data)

        Log.e("show_data_list",""+position)
        viewPager.setCurrentItem(position+1, false)



        cardview.visibility = View.GONE
        //visible
        full_screen_back.visibility = View.VISIBLE
        layout_radio.visibility = View.GONE
        journal_menu.visibility = View.VISIBLE
        journal_menu_arch.visibility = View.VISIBLE
        commeteText.visibility = View.GONE


        myRunnable = Runnable {

            cardview.visibility = View.GONE
            layout_radio.visibility = View.GONE
            journal_menu.visibility = View.GONE
            journal_menu_arch.visibility = View.GONE
            full_screen_back.visibility = View.GONE
            commeteText.visibility = View.GONE
        }

        handler.postDelayed(myRunnable, 5000)

        dialog.dismiss()
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
                progressDialog.dismiss()
                caseAdapter.updateData(sortedByAge,bitmap)
                if (touchscreen.equals("1")){

//                    Toast.makeText(this@StartActivity,"300",Toast.LENGTH_SHORT).show()


                    myRunnable = Runnable {
                        cardview.visibility = View.GONE
                        layout_radio.visibility = View.GONE
                        journal_menu.visibility = View.GONE
                        journal_menu_arch.visibility = View.GONE



//                        handler.removeCallbacks(myRunnable)
                        cardview.visibility = View.GONE

                        //visible
                        full_screen_back.visibility = View.VISIBLE

                        layout_radio.visibility = View.GONE
                        journal_menu.visibility = View.VISIBLE
                        journal_menu_arch.visibility = View.VISIBLE
                        commeteText.visibility = View.GONE
                        myRunnable = Runnable {
                            cardview.visibility = View.GONE
                            layout_radio.visibility = View.GONE
                            journal_menu.visibility = View.GONE
                            journal_menu_arch.visibility = View.GONE
                            full_screen_back.visibility = View.GONE
                            commeteText.visibility = View.GONE
                        }
                        handler.postDelayed(myRunnable, 5000)

                    }

                    handler.postDelayed(myRunnable,5000)
                }else if (touchscreen.equals("2")){

                    cardview.visibility = View.VISIBLE
                    layout_radio.visibility = View.VISIBLE
                    journal_menu.visibility = View.VISIBLE
                    journal_menu_arch.visibility = View.VISIBLE

                }


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

    private inner class DownloadPdfTask2 : AsyncTask<String, Void, Bitmap>() {

        override fun doInBackground(vararg params: String): Bitmap? {
            return downloadPdf(params[0])
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            super.onPostExecute(bitmap)
            if (bitmap != null) {
//                imageView.setImageBitmap(bitmap)

                Log.e("show_data_list",""+bitmap)
                progressDialog.dismiss()
                pdfView.setImageBitmap(bitmap)
            }
        }

        private fun downloadPdf(url: String): Bitmap? {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()
                val inputStream = connection.inputStream
                val fileOutputStream = FileOutputStream(pdfFile1)
                inputStream.copyTo(fileOutputStream)
                fileOutputStream.close()

                return renderPdf1()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }



    private fun renderPdf1(): Bitmap? {
        try {
            val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(pdfFile1, ParcelFileDescriptor.MODE_READ_ONLY))
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


    override fun ItemClicked(data: Bitmap, position: Int) {
//        Toast.makeText(this@StartActivity, "Long press detected - Action 1", Toast.LENGTH_SHORT).show()
        handler.removeCallbacks(myRunnable)
        cardview.visibility = View.GONE
        //visible
        full_screen_back.visibility = View.VISIBLE
        layout_radio.visibility = View.GONE
        journal_menu.visibility = View.VISIBLE
        journal_menu_arch.visibility = View.VISIBLE
        commeteText.visibility = View.GONE

         myRunnable = Runnable {

             cardview.visibility = View.GONE
             layout_radio.visibility = View.GONE
             journal_menu.visibility = View.GONE
             journal_menu_arch.visibility = View.GONE
             full_screen_back.visibility = View.GONE
             commeteText.visibility = View.GONE
         }

        handler.postDelayed(  myRunnable,5000)

    }


    override fun onInnerItemClick(item: Subsection, position: Int) {
//        Toast.makeText(this@StartActivity, "Long press detected - Action 2", Toast.LENGTH_SHORT).show()

         handler.removeCallbacks(myRunnable)
        cardview.visibility = View.GONE
        //visible
        full_screen_back.visibility = View.VISIBLE

        layout_radio.visibility = View.GONE
        journal_menu.visibility = View.VISIBLE
        journal_menu_arch.visibility = View.VISIBLE
        commeteText.visibility = View.GONE


        myRunnable = Runnable {

            cardview.visibility = View.GONE
            layout_radio.visibility = View.GONE
            journal_menu.visibility = View.GONE
            journal_menu_arch.visibility = View.GONE
            full_screen_back.visibility = View.GONE
            commeteText.visibility = View.GONE
        }

        handler.postDelayed(myRunnable, 5000)
    }

    override fun onResume() {
        super.onResume()

        Log.e("onResume ",""+"onResume")

//        makeApiCall() // Fetch the latest data from the API
    }
}