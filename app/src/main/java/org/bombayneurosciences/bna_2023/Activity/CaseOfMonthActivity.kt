package org.bombayneurosciences.bna_2023.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import org.bombayneurosciences.bna_2023.CallBack.CaseOfMonthInterface.CaseItemClickListenerCaseOfMonth
import org.bombayneurosciences.bna_2023.Data.RetrofitInstance
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.adapter.CaseAdapter
import org.bombayneurosciences.bna_2023.Model.CaseofMonth.CaseOfMonth
import org.bombayneurosciences.bna_2023.Model.CaseofMonth.DataX
import org.bombayneurosciences.bna_2023.Model.CaseofMonth.Section
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CaseOfMonthActivity : AppCompatActivity(),CaseItemClickListenerCaseOfMonth {

    private lateinit var caseAdapter: CaseAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var noDataTextView: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var currentRadioButton: RadioButton
    private lateinit var archiveRadioButton: RadioButton
    private lateinit var sharedPreferencesManager1: SessionManager1
    private lateinit var gifImageView: GifImageView
    var tabClickId = 0
    private lateinit var progressBar: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_of_month)


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


        sharedPreferencesManager1 = SessionManager1(this)
        gifImageView = findViewById(R.id.gifImageView)

        val backButton = findViewById<ImageView>(R.id.backbutton_case)
        recyclerView = findViewById(R.id.recyclerview_case)
        noDataTextView = findViewById(R.id.no_data_text_view)
        radioGroup = findViewById(R.id.radio_group)
        currentRadioButton = findViewById(R.id.current_radio_button)
        archiveRadioButton = findViewById(R.id.archive_radio_button)
        val refreshButton = findViewById<ImageView>(R.id.refreshbutton)
        progressBar = findViewById<ProgressBar>(R.id.progressBar_card)

       /* currentRadioButton.setBackgroundResource(R.drawable.radio_flat_regular1)
        currentRadioButton.setTextColor(Color.WHITE)*/

        backButton.setOnClickListener {

            sharedPreferencesManager1.setBackState(true)
            sharedPreferencesManager1.setBottomMenuBar("CaseOfMonth")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            //sharedPreferencesManager1.setAppKill("Case_Of_Month")


        }
        refreshButton.setOnClickListener {
            // Call the method to handle the refresh logic
//            refreshPage()

            if (tabClickId==0){
                loadCurrentCases()
            }else if (tabClickId==1){
                loadArchiveCases()
            }else{
                loadCurrentCases()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        caseAdapter = CaseAdapter(this, mutableListOf(), this)
        recyclerView.adapter = caseAdapter
//        2131362034  2131361909
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.current_radio_button -> {
                    loadCurrentCases()
                    tabClickId = 0
                }
                R.id.archive_radio_button ->{
                    loadArchiveCases()
                    tabClickId = 1
                }




            }
        }

        //setDefaultRadioButton()
        tabClickId = 0
        loadCurrentCases()
    }

    private fun refreshPage() {
        recreate()

    }

    private fun setDefaultRadioButton() {
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = sdf.format(currentDate)

        val today = sdf.parse(formattedDate) ?: return

        if (today.after(sdf.parse("2023-12-25")!!)) {
            archiveRadioButton.isChecked = true
        } else {
            currentRadioButton.isChecked = true
        }
    }
    private fun loadCurrentCases() {
        makeApiCall(true)

       // currentRadioButton.setBackgroundResource(R.drawable.background_unselected)

        currentRadioButton.setBackgroundResource(R.drawable.radio_flat_regular1)
        currentRadioButton.setTextColor(Color.WHITE)


        archiveRadioButton.setBackgroundResource(R.drawable.radio_flat_regular)
        archiveRadioButton.setTextColor(Color.BLACK)
    }

    private fun loadArchiveCases() {
        makeApiCall(false)

        currentRadioButton.setBackgroundResource(R.drawable.radio_flat_regular)
        currentRadioButton.setTextColor(Color.BLACK)


        archiveRadioButton.setBackgroundResource(R.drawable.radio_flat_regular1)
        archiveRadioButton.setTextColor(Color.WHITE)
    }

    private fun makeApiCall(isCurrent: Boolean) {
        progressBar.visibility = ProgressBar.VISIBLE

        val call = RetrofitInstance.apiServiceCases.getCases()

        call.enqueue(object : Callback<CaseOfMonth> {
            override fun onResponse(call: Call<CaseOfMonth>, response: Response<CaseOfMonth>) {
                if (response.isSuccessful) {
                    val caseOfMonth = response.body()

                    val gson = Gson()
                    val json = gson.toJson(caseOfMonth)

                    Log.e("brocodeCaheOfMonth",""+json)

                    Log.d(ConstanstsApp.tag, "API Response: ${caseOfMonth!!.data}")

                    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val filteredData = if (isCurrent) {
                        caseOfMonth?.data?.filter { it.end_date!! >= currentDate }
                    } else {
                        caseOfMonth?.data?.filter { it.end_date!! < currentDate }
                    }

                    if (filteredData != null && filteredData.isNotEmpty()) {
                        caseAdapter.updateData(filteredData)
                        showRecyclerView()
                        sharedPreferencesManager1.saveCases(filteredData) // Save the cases for offline use
                        Log.d("MyTag", "Stored Cases: $filteredData")

                        progressBar.visibility = ProgressBar.GONE

                    } else {

                        showNoData()
                    }

                } else {
                    showNoData()
                }
            }

            override fun onFailure(call: Call<CaseOfMonth>, t: Throwable) {
                // Load offline cases if available
                val offlineCases = sharedPreferencesManager1.getCases()
                progressBar.visibility = ProgressBar.GONE
                if (offlineCases.isNotEmpty()) {
                    caseAdapter.updateData(offlineCases)
                    showRecyclerView()

                    // Print stored cases and size for debugging
                    Log.d("MyTag", "Stored Cases: $offlineCases, Size: ${offlineCases.size}")
                } else {
                    // Log when no offline cases are available
                    Log.d("MyTag", "No offline cases available")
                    showNoData()
                }
            }


        })
    }


    private fun showRecyclerView() {
        recyclerView.visibility = RecyclerView.VISIBLE
        noDataTextView.visibility = TextView.GONE
    }

    private fun showNoData() {
        recyclerView.visibility = RecyclerView.GONE
        noDataTextView.visibility = TextView.VISIBLE
        progressBar.visibility = ProgressBar.GONE
    }

    override fun onCaseItemClick(position: Int, data: DataX) {
        val intent = Intent(this, CaseOfMonthActivity2::class.java)

        // Put the 'data' object directly using putExtra
        intent.putExtra("data", data)

        // Put other individual values if needed
        intent.putExtra("id", data.case_id)

        // Accessing data within the sections
        val sections: ArrayList<Section>? = data.sections

        // Check if sections is not null and not empty
        if (sections != null && sections.isNotEmpty()) {
            // Create an ArrayList to store serialized Section data
            val serializedSections = ArrayList<Parcelable>()

            // Iterate through each Section object
            for (section in sections) {
                // Access the properties of the Section object
                val description = section.description
                val descriptionPlain = section.description_plain
                val title = section.title
                val titlePlain = section.title_plain
                val media = section.media
                Log.d("mytag", "Media data: $media")

                // Create a Bundle to store Section data
                val sectionBundle = Bundle().apply {
                    putString("description", description)
                    putString("description_plain", descriptionPlain)
                    putString("title", title)
                    putString("title_plain", titlePlain)
                    putParcelableArrayList("media", media)  // Put the media data

                }
                // Add the Bundle to the ArrayList
                serializedSections.add(sectionBundle)
            }

            // Put the ArrayList of serialized Section data
            intent.putParcelableArrayListExtra("sections", serializedSections)
        }

        intent.putExtra("isArchived", isCaseArchived(data.end_date.toString()))

        startActivity(intent)
    }


    private fun isCaseArchived(endDate: String): Boolean {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return endDate < currentDate
    }

    override fun onBackPressed() {
        super.onBackPressed()

        sharedPreferencesManager1.setBackState(true)
        //sharedPreferencesManager1.setBottomMenuBar("CaseOfMonth")

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }
}
