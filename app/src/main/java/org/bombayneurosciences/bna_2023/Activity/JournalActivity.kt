package org.bombayneurosciences.bna_2023.Activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.org.wfnr_2024.ViewModel.BNAProviderFactory
import com.org.wfnr_2024.ViewModel.BNARespository
import com.org.wfnr_2024.ViewModel.BNA_ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.bombayneurosciences.bna.Model.Journal.JournalEntry
import org.bombayneurosciences.bna.Model.Journal.JournalResponse
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_Repository
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_ViewModel
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_ViewModelFactory
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RoomDatabase
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DAO.Journal_DAO
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData
import org.bombayneurosciences.bna_2023.Data.RetrofitInstance
import org.bombayneurosciences.bna_2023.Data.SharedPreferencesManager
import org.bombayneurosciences.bna_2023.Fragment.JournalArchievesFragment
import org.bombayneurosciences.bna_2023.Fragment.JournalLastestFragment2
import org.bombayneurosciences.bna_2023.Fragment.JournalLatestFragment
import org.bombayneurosciences.bna_2023.Fragment.JournalLatestFragment1
import org.bombayneurosciences.bna_2023.Fragment.LatestFragment
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.adapter.JournalAdapter
import org.bombayneurosciences.bna_2023.databinding.ActivityJournalBinding
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp.Companion.getFileNameFromUrl
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.LinkedList
import java.util.Queue

class JournalActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JournalAdapter
    private lateinit var radioGroup: RadioGroup
    private lateinit var latestRadio: RadioButton
    private lateinit var archiveRadio: RadioButton
    private val journalList = ArrayList<JournalEntry>()
    private lateinit var noDataTextView: TextView
    private lateinit var gifImageView: GifImageView
    private lateinit var viewModel: BNA_ViewModel
    private lateinit var viewModel1: BNA_RD_ViewModel
    private lateinit var progressDialog: ProgressDialog
    private lateinit var sessionManager: SessionManager1
    private val PDF_FILE_NAMES = mutableListOf<String>()
    private var filesDownloadedCount = 0
    private lateinit var pdfFiles: ArrayList<File>

    private val downloadQueue: Queue<Pair<String, String>> = LinkedList()

    // Define a custom order for months
    private val customMonthOrder = arrayOf("January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )


    private var isLatestSelected: Boolean = true

    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var journalDataList: List<JournalEntry>
    private lateinit var sharedPreferencesManager1 : SessionManager1
    lateinit var binding: ActivityJournalBinding


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityJournalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_journal)


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



//
        // Check if it's not coming from a back press event
        if (!intent.getBooleanExtra("isBackPressed", false) && ConstanstsApp.isInternetAvailable(this)) {
            showCustomProgressDialog()
        }
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        gifImageView = findViewById(R.id.gifImageView)

        sharedPreferencesManager = SharedPreferencesManager(applicationContext)
        sharedPreferencesManager1 = SessionManager1(this)

        radioGroup = findViewById(R.id.radio_group)
        latestRadio = findViewById(R.id.latest_radio_button)
        archiveRadio = findViewById(R.id.archives_radio_button)
        noDataTextView = findViewById(R.id.no_data_text_view)

//        setFragment1(JournalLatestFragment(),"")

       setFragment1(JournalLatestFragment1(),"")

//        setFragment1(JournalLastestFragment2(),"")



        //setFragment1(JournalArchievesFragment(),"")
        
        
       


        binding.latestRadioButton.setBackgroundResource(R.drawable.radio_flat_regular1)
        binding.latestRadioButton.setTextColor(Color.WHITE)

        val backButton = findViewById<ImageView>(R.id.backbutton)

        // Set a click listener for the back button
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("isBackPressed", true)

            startActivity(intent)

            sharedPreferencesManager1.setBackState(true)
            finish()
            //sharedPreferencesManager1.setBottomMenuBar("journal")

        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->

            when(checkedId)
            {
                R.id.latest_radio_button->
                {

                   // isLatestSelected = true
                    binding.latestRadioButton.setBackgroundResource(R.drawable.radio_flat_regular1)
                    binding.latestRadioButton.setTextColor(Color.WHITE)


                    binding.archivesRadioButton.setBackgroundResource(R.drawable.radio_flat_regular)
                    binding.archivesRadioButton.setTextColor(Color.BLACK)

//                    setFragment1(JournalLastestFragment2(),"")

                    setFragment1(JournalLatestFragment1(),"")

//                    setFragment1(JournalLatestFragment(),"")

                }
                R.id.archives_radio_button->
                {
                  //  isLatestSelected = false
                     binding.archivesRadioButton.setBackgroundResource(R.drawable.radio_flat_regular1)
                     binding.archivesRadioButton.setTextColor(Color.WHITE)

                    binding.latestRadioButton.setTextColor(Color.BLACK)
                    binding.latestRadioButton.setBackgroundResource(R.drawable.radio_flat_regular)

                    setFragment1(JournalArchievesFragment(),"")

                }

            }

        }

        adapter = JournalAdapter(this, journalList, isLatestSelected)
        recyclerView.adapter = adapter

        // Check if the activity was started with isLatestSelected flag
        if (intent.hasExtra("isLatestSelected")) {
            isLatestSelected = intent.getBooleanExtra("isLatestSelected", true)
            if (isLatestSelected) {
                // Select the "Latest" radio button
                latestRadio.isChecked = true
            } else {
                // Select the "Archive" radio button
                archiveRadio.isChecked = true
            }

            // Load and display data based on the selected section
            loadAndDisplayData(isLatestSelected)
        } else {
            // Ensure initial data is loaded and displayed
            loadAndDisplayData(isLatestSelected)
        }

        adapter.setOnItemClickListener(object : JournalAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, isLatestSelected: Boolean) {
                val selectedJournalEntry = journalList[position]

                if (isLatestSelected) {
                    // Handle click in the Latest section
                    handleLatestItemClick(selectedJournalEntry)
                } else {
                    // Handle click in the Archive section
                    handleArchiveItemClick(selectedJournalEntry)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        pdfFiles = ArrayList()

        getViewModel()
        createRoomDatabase()

        //init()
        //setupObservers()
    }

    private fun createRoomDatabase() {
        val database = BNA_RoomDatabase.getDatabase(this)
        val journalDAO: Journal_DAO = database.Journal_DAO()
        val repository = BNA_RD_Repository(journalDAO, database)
        viewModel1 = ViewModelProvider(this, BNA_RD_ViewModelFactory(repository)).get(
            BNA_RD_ViewModel::class.java)
    }

    private fun getViewModel() {
        val bnaRepository = BNARespository()
        val bnaProviderFactory = BNAProviderFactory(bnaRepository, application)
        viewModel = ViewModelProvider(this, bnaProviderFactory).get(BNA_ViewModel::class.java)

        progressDialog = ProgressDialog(this).apply {
            setCancelable(false)
            setMessage(getString(R.string.please_wait))
        }

        sessionManager = SessionManager1(this)
    }

    private fun init() {
        if (ConstanstsApp.checkInternetConenction(this)) {
            viewModel1.deleteJournal()
            viewModel.fetchJournalData()
        } else {
            // ConstanstsApp.showCustomToast(requireContext(), "No Internet Connection")

            val pdfNames=sessionManager.getPdfFileNames()

            Log.d(ConstanstsApp.tag,"pdfNames=>"+pdfNames)

           // loadLocalPdfs(pdfNames)

           // showNoInternetAlert("Please connect to the internet")

        }
    }

    private fun setupObservers() {
        viewModel.journalData.observe(this, Observer { response ->

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

        viewModel1.get_All_journal.observe(this, Observer { response ->
            val seenIssueFile = mutableSetOf<String>()

            val filteredList = if (response.isNotEmpty() && response.first().month.equals(response.first().month, ignoreCase = true)) {
                response.filter { it.month.equals(response.first().month, ignoreCase = true) }
            } else {
                response
            }
            filteredList.forEach { data ->
                val issueFileName = getFileNameFromUrl(data.issueFile)
                val articleFileName = getFileNameFromUrl(data.articleFile)

                if (seenIssueFile.add(issueFileName)) {
                    PDF_FILE_NAMES.add(issueFileName)
                }

                PDF_FILE_NAMES.add(articleFileName)


                downloadQueue.add(Pair(data.issueFile, getFileNameFromUrl(data.issueFile)))
                downloadQueue.add(Pair(data.articleFile, getFileNameFromUrl(data.articleFile)))


            }

            sessionManager.setPdfFileNames(PDF_FILE_NAMES)

           // showProgressDialog()

          //  downloadFilesSequentially()


        })
    }

    private fun showProgressDialog() {
        progressDialog = ProgressDialog(this).apply {
            setMessage("Please wait while files are downloading...")
            setCancelable(false)
            show()
        }
    }

    private fun downloadFilesSequentially() {
        this.lifecycleScope.launch(Dispatchers.IO) {
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
                // Optionally, show a toast or message indicating that all files are downloaded
                Toast.makeText(this@JournalActivity, "All files downloaded successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createAppFolder(folderName: String): File {
        val appFolder = File(getExternalFilesDir(null), folderName)
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
    }


    private fun showCustomProgressDialog() {
        val progressDialog = Dialog(this)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set layout parameters to center the dialog
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(progressDialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER

        progressDialog.window?.attributes = layoutParams

        progressDialog.setContentView(R.layout.dialog_progress)
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Example: Dismiss the dialog after a delay (simulating a task completion)
        val imageViewLoading: ImageView = progressDialog.findViewById(R.id.imageViewLoading)

        Glide.with(this)
            .asGif()
            .load(R.raw.loderbna)
            .into(imageViewLoading)

        imageViewLoading.postDelayed({
            progressDialog.dismiss()
        }, 2000)
    }


    private fun handleLatestItemClick(selectedJournalEntry: JournalEntry) {
        if (selectedJournalEntry.articleFile.isNotEmpty()) {
            // Create an Intent to open JournalActivity2
            val monthYear = selectedJournalEntry.month
            val archiveData = journalList.filter { it.month == monthYear }

            val intent = Intent(this@JournalActivity, JournalActivity2::class.java)
            val bundle = Bundle()
            bundle.putParcelableArrayList("journalEntries", ArrayList(archiveData))
            bundle.putString("title",selectedJournalEntry.title)
            // Pass the articleFile data to JournalActivity2
            bundle.putString("articleFile", selectedJournalEntry.articleFile)


            intent.putExtras(bundle)
            startActivity(intent)
        } else {
            // If articleFile is empty, show a message to the user
            Toast.makeText(
                this@JournalActivity,
                "No Data available",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleArchiveItemClick(selectedJournalEntry: JournalEntry) {
        // Create an Intent to open JournalActiivtyyy
        val intent = Intent(this@JournalActivity, JournalActiivtyyy::class.java)
        intent.putExtra("selectedJournalEntry", selectedJournalEntry)


        // Filter entries for the selected month and year
        val monthYear = selectedJournalEntry.month

        val archiveData = journalList.filter { it.month == monthYear }

        // Pass the filtered entries to JournalActiivtyyy
        val bundle = Bundle()
        bundle.putParcelableArrayList("journalEntries", ArrayList(archiveData))
        intent.putExtras(bundle)

        // Pass the articleFile of the selected entry to JournalActiivtyyy
        intent.putExtra("articleFile", selectedJournalEntry.articleFile)
        intent.putExtra("title",selectedJournalEntry.title)
        intent.putExtra("month",selectedJournalEntry.month)
        startActivity(intent)
    }

    private fun loadAndDisplayData(latestSelected: Boolean) {
        if (journalList.isEmpty()) {
            // If journalList is empty, fetch data
            fetchData(isLatestSelected)
        } else {
            // If journalList is not empty, load offline data and update adapter
            loadOfflineData(isLatestSelected)
            updateAdapter(isLatestSelected)

            // Print which month's data is in the "Latest" section and which is in the "Archive" section
            val latestMonths = mutableListOf<String>()
            val archiveMonths = mutableListOf<String>()

            for (journalEntry in journalList) {
                if (isLatestSelected) {
                    latestMonths.add("${journalEntry.month} ${journalEntry.year}")
                } else {
                    archiveMonths.add("${journalEntry.month} ${journalEntry.year}")
                }
            }

            Log.d(ConstanstsApp.tag, "Latest Months: $latestMonths")
            Log.d(ConstanstsApp.tag, "Archive Months: $archiveMonths")

            // Additional code to print which month is in "Latest" and which is in "Archive"
            if (isLatestSelected) {
                Log.d(ConstanstsApp.tag, "Latest Month: ${getCurrentMonthYear().first}")
            } else {
                val currentMonthYear = getCurrentMonthYear()
                Log.d(ConstanstsApp.tag, "Archive Month: ${currentMonthYear.first} ${currentMonthYear.second}")

                // Add this block to fetch data for the selected month when in "Archive" section
                val selectedMonthData = journalList.filter { it.month == currentMonthYear.first && it.year == currentMonthYear.second }
                Log.d(ConstanstsApp.tag, "Selected Month: ${currentMonthYear.first} ${currentMonthYear.second}")

                if (selectedMonthData.isEmpty()) {
                    fetchData(isLatestSelected)
                } else {
                    updateAdapter(isLatestSelected)
                }
            }
        }
    }

    private fun loadOfflineData(isLatestSelected: Boolean) {
        // Load offline data based on the section
        journalList.clear()
        journalList.addAll(sharedPreferencesManager.getJournalData(isLatestSelected))
    }



    private fun updateAdapter(isLatestSelected: Boolean) {
        // Update the adapter based on the section
        adapter.isLatestSection = isLatestSelected

        if (isLatestSelected) {
            adapter.setData(journalList)
        } else {
            val uniqueMonthYears = mutableSetOf<String>()
            val filteredList = mutableListOf<JournalEntry>()

            for (journalEntry in journalList) {
                val monthYear = "${journalEntry.month} ${journalEntry.year}"
                if (uniqueMonthYears.add(monthYear)) {
                    // If successfully added to the set, add to the filtered list
                    filteredList.add(journalEntry)
                }
            }
            adapter.setData(filteredList)
        }

        adapter.notifyDataSetChanged()

        if (adapter.itemCount == 0) {


            binding.noDataTextView.visibility = View.VISIBLE
        } else {
            binding.noDataTextView.visibility = View.GONE
        }
    }

    private fun fetchData(isLatestSelected: Boolean) {
        val apiServiceJournal = RetrofitInstance.apiServiceJournal
        val call = apiServiceJournal.getJournalEntries()

        call.enqueue(object : Callback<JournalResponse> {
            override fun onResponse(call: Call<JournalResponse>, response: Response<JournalResponse>) {
                if (response.isSuccessful) {
                    val journalResponse = response.body()
                    if (journalResponse != null) {
                        // Clear the list
                        journalList.clear()
                        val currentMonthYear = getCurrentMonthYear()



                        if (isLatestSelected) {
                            // Get the latest month with data available
                           // val latestMonthWithData = journalResponse.data.maxByOrNull { it.year.toInt() * 12 + customMonthOrder.indexOf(it.month) }

                            val latestMonthWithData = journalResponse.data
                                .filter { it.year != null && it.month != null }
                                .maxByOrNull {
                                    val year = it.year?.toIntOrNull() ?: 0
                                    val monthIndex = customMonthOrder.indexOf(it.month)
                                    year * 12 + monthIndex
                                }


                            // Add all entries for the latest month with data to the list
                            if (latestMonthWithData != null) {
                                journalList.addAll(latestMonthWithData.data)
                            }
                        } else {
                            // Add all other entries to the "Archive" section
                            val archiveData = journalResponse.data.flatMap { it.data }
                                .filter { it.month + it.year != currentMonthYear.first + currentMonthYear.second }
                            journalList.addAll(archiveData)

                            journalList.sortBy { it.year.toInt() * 12 + customMonthOrder.indexOf(it.month) }


                            // Remove entries for the latest month with data from the "Archive" section
                            if (journalList.isNotEmpty()) {
                                //val latestMonth = journalResponse.data.maxByOrNull { it.year.toInt() * 12 + customMonthOrder.indexOf(it.month) }

                                val latestMonth = journalResponse.data.maxByOrNull {
                                    val year = it.year?.toIntOrNull() ?: 0 // Default to 0 if year is null or not a number
                                    val monthIndex = customMonthOrder.indexOf(it.month).takeIf { it != -1 } ?: 0 // Default to 0 if month is not found
                                    year * 12 + monthIndex
                                }


                                journalList.removeAll(latestMonth?.data ?: emptyList())
                            }
                        }


                        // Save the updated journal data in SharedPreferences
                        sharedPreferencesManager.saveJournalData(journalList, isLatestSelected)
                        // Update the adapter
                        updateAdapter(isLatestSelected)
                    }
                } else {
                    Log.e(ConstanstsApp.tag, "API Error: ${response.code()}")
                    // If no data is available, show a message to the user
                    Toast.makeText(
                        this@JournalActivity,
                        "No data available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<JournalResponse>, t: Throwable) {
                Log.e("JournalActivity", "Network Error: ${t.message}", t)
            }
        })
    }

    fun getCurrentMonthYear(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        // Convert the numeric month to a string representation
        val monthString = DateFormatSymbols().months[month]

        return Pair(monthString, year.toString())
    }
    override fun onBackPressed() {
       // super.onBackPressed()
        sharedPreferencesManager1.setBackState(true)


        val packageName = applicationContext.packageName
        val subsection_details = "$packageName.Fragment.JournalLatestFragment1"
        val archive_details = "$packageName.Fragment.JournalArchievesFragment"
        val f = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if(f?.javaClass?.name==subsection_details)
        {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else if(f?.javaClass?.name==archive_details)
        {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else
        {
            super.onBackPressed()
        }
    }

    fun setFragment1(fragment: Fragment, link:String)
    {

        val bundle = Bundle()
        bundle.putString("data",link)
        val transaction = supportFragmentManager.beginTransaction()
        fragment.arguments = bundle
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
        //requireActivity().supportFragmentManager.beginTransaction().replace(R.id.relativelayout_container,fragment).commit()
    }
}
