package org.bombayneurosciences.bna_2023.Fragment

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.org.wfnr_2024.ViewModel.BNAProviderFactory
import com.org.wfnr_2024.ViewModel.BNARespository
import com.org.wfnr_2024.ViewModel.BNA_ViewModel
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_Repository
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_ViewModel
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_ViewModelFactory
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RoomDatabase
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DAO.Journal_DAO
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData
import org.bombayneurosciences.bna_2023.CallBack.JournalDataClick
import org.bombayneurosciences.bna_2023.JournalNewFolder.WelcomeElement
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.adapter.JournalArchievesAdapter
import org.bombayneurosciences.bna_2023.databinding.FragmentJournalArchievesBinding
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp.Companion.getFileNameFromUrl

class JournalArchievesFragment: Fragment(), JournalDataClick {

    private var binding: FragmentJournalArchievesBinding? = null
   // private val binding get() = _binding!!

    lateinit var viewModel: BNA_ViewModel
    lateinit var viewModel1: BNA_RD_ViewModel
    private val PDF_FILE_NAMES = mutableListOf<String>()
    lateinit var progressDialog: ProgressDialog

    private var journalLocalDataList = mutableListOf<JournalLoacalData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // return super.onCreateView(inflater, container, savedInstanceState)
        //return inflater.inflate(R.layout.fragment_journal_archieves, container, false)

        binding = FragmentJournalArchievesBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getViewModel()
        createRoomDatabase()

       getJournalArchieves()
    }



    private fun createRoomDatabase() {
        val database = BNA_RoomDatabase.getDatabase(requireContext())
        val Journal_DAO: Journal_DAO =database.Journal_DAO()
        val repository = BNA_RD_Repository(Journal_DAO,database)
        viewModel1 = ViewModelProvider(this, BNA_RD_ViewModelFactory(repository)).get(
            BNA_RD_ViewModel::class.java)
    }

    private fun getViewModel() {
        val ianRespository= BNARespository()
        val ianProviderFactory= BNAProviderFactory(ianRespository,requireActivity().application)
        viewModel= ViewModelProvider(this,ianProviderFactory).get(BNA_ViewModel::class.java)

        progressDialog = ProgressDialog(context).apply {
            setCancelable(false)
            setMessage(getString(R.string.please_wait))
        }
    }

    private fun getJournalArchieves() {
        viewModel1.get_All_journal.observe(viewLifecycleOwner, Observer {
                response->

            if (response.size>0)
            {
                binding!!.TextViewArchieveNoData!!.visibility=View.GONE

                viewModel1.setJournalData(response)


                viewModel1.getFilteredJournalData().observe(viewLifecycleOwner, Observer { filteredData ->

                    Log.d(ConstanstsApp.tag,"filteredData=>"+filteredData.drop(1))

                    val responseData=filteredData.drop(1).sortedBy { it.indexPage }

                    val adapter = JournalArchievesAdapter(responseData, requireContext(),this)
                    binding!!.RecyclerViewAchives.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding!!.RecyclerViewAchives.adapter = adapter
                    adapter.notifyDataSetChanged()

                })

              /*  for (data in response)
                {
                    val month=data.month
                }

                val adapter = JournalArchievesAdapter(response, requireContext(),this)
                binding!!.RecyclerViewAchives.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding!!.RecyclerViewAchives.adapter = adapter
                adapter.notifyDataSetChanged()*/
            }

        })
    }

    override fun JournalItemClicked(data: JournalLoacalData, position: Int, view: View) {
       Log.d(ConstanstsApp.tag,"clicked data=>"+data)




        viewModel1.get_All_journal.observe(viewLifecycleOwner, Observer { response ->

            journalLocalDataList = response.map { data ->
                JournalLoacalData(
                    0,
                    id = data.id,
                    issueId = data.issueId,
                    month = data.month,
                    year = data.year,
                    articleType = data.articleType ?: "",
                    title = data.title,
                    author = data.author,
                    reference = data.reference ?: "",
                    indexPage = data.indexPage,
                    noOfPage = data.noOfPage,
                    articleFile = data.articleFile,
                    isArchive = data.isArchive,
                    isActive = data.isActive,
                    isDeleted = data.isDeleted,
                    createdAt = data.createdAt,
                    updatedAt = data.updatedAt,
                    issueFile = data.issueFile,
                    volume = data.volume,
                    issue_no = data.issue_no
                )
            }.toMutableList()


            // Set to track seen issue files
            val seenIssueFile = mutableSetOf<String>()
            // Set to track unique month/year combinations
            val seenMonthYear = mutableSetOf<Pair<String, String>>()

            response?.forEach { data ->

                // Extract file names
                val issueFileName = getFileNameFromUrl(data.issueFile)
                val articleFileName = getFileNameFromUrl(data.articleFile)


                // Check if the month/year combination is already seen
                val monthYearPair = Pair(data.month, data.year)
                if (!seenMonthYear.contains(monthYearPair)) {
                    // If not seen, add to the set and add the issue file if it's also unique
                    seenMonthYear.add(monthYearPair)
                    if (seenIssueFile.add(issueFileName)) {
                        PDF_FILE_NAMES.add(issueFileName)
                    }
                }

                // Add article file name regardless of month/year combination
                PDF_FILE_NAMES.add(articleFileName)
            }

            // Log the final PDF file names
            PDF_FILE_NAMES.forEach { fileName ->
                Log.d("mytag", "PDF_FILE_NAMES => $fileName")
            }
        })


        val gson = Gson()
        val json = gson.toJson(journalLocalDataList)

        val gson1 = Gson()
        val json1 = gson1.toJson(java.util.ArrayList(PDF_FILE_NAMES))

 Log.e("archivedata",""+json1)

        val bundle = Bundle()
        bundle.putSerializable("journal_data", data)
        bundle.putStringArrayList("pdf_file_names", java.util.ArrayList(PDF_FILE_NAMES))
        bundle.putString("data",json)


        val intent = Intent(requireContext(), JournalViewActivity3::class.java).apply {
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
       // Assuming 'data' is an instance of JournalLoacalData

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