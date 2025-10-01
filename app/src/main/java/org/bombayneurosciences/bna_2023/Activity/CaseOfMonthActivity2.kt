package org.bombayneurosciences.bna_2023.Activity

import android.Manifest.permission.RECORD_AUDIO
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.createSpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import okhttp3.internal.notifyAll
import org.bombayneurosciences.bna_2023.CallBack.CaseOfMonthInterface.onImageClickCaseOfMonth
import org.bombayneurosciences.bna_2023.Data.SharedPreferencesActivity
import org.bombayneurosciences.bna_2023.Fragment.FullScreenBottomSheetFragment
import org.bombayneurosciences.bna_2023.ImageViewverCaseOfMonth
import org.bombayneurosciences.bna_2023.Model.CaseofMonth.DataX
import org.bombayneurosciences.bna_2023.Model.CaseofMonth.Media
import org.bombayneurosciences.bna_2023.Model.CaseofMonth.Section
import org.bombayneurosciences.bna_2023.Model.SendComment.Commentsend
import org.bombayneurosciences.bna_2023.Model.SendComment.SendResponse
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.adapter.ChatsAdapter
import org.bombayneurosciences.bna_2023.adapter.SectionAdapter
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.jar.Manifest


class CaseOfMonthActivity2 : AppCompatActivity(), onImageClickCaseOfMonth,
    TextToSpeech.OnInitListener {

    private lateinit var titleTextView: TextView
//    private lateinit var titleTextView1: TextView
//    private lateinit var titleTextView2: TextView
private var isScrollingUp = false
    private lateinit var descriptionTextView: TextView
    private lateinit var descriptionTextView1: TextView
    private lateinit var descriptionTextView2: TextView
    private lateinit var speak: ImageView
    private lateinit var speakUp: ImageView

    private lateinit var radiologyRecyclerView: RecyclerView
    private lateinit var pathologyRecyclerView: RecyclerView
    private lateinit var videoRecyclerView: RecyclerView
    private lateinit var data: DataX
    private lateinit var chatsAdapter: ChatsAdapter
    private lateinit var sectionAdapter: SectionAdapter
    private var isArchived: Boolean = false
    private lateinit var bottomSheetView:View
    private lateinit var recyclerViewChats:RecyclerView
    private lateinit var sectionRecyclerView:RecyclerView
    private var sectionsList = mutableListOf<Section>()
    lateinit var editTextChats:EditText
    private var isUpArrowClicked = false
    // val delegateId: String? = null
    var delegateId: String? = null
    var description1: String? = null
    var description2: String? = null
    var allAdapterString: String? = null
    private var caseId: String? = null
    private var comments = mutableListOf<org.bombayneurosciences.bna_2023.Model.chats.Data>()
    private var textToSpeech: TextToSpeech? = null
    private lateinit var bottomSheetDialog :BottomSheetDialog

    //private var sectionlist: List<Section> = emptyList() // Initialize as an empty list
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_of_month2)


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




        // Initialize TextToSpeech
        textToSpeech = TextToSpeech(this, this)


        val eventHeaderTextView: TextView = findViewById(R.id.eventheader)
        eventHeaderTextView.alpha = 0f

        // Call your animation function
        applyFadeInAnimation(eventHeaderTextView, 0)



        titleTextView = findViewById(R.id.title_case)
        descriptionTextView = findViewById(R.id.des_case)
        speakUp = findViewById(R.id.speakUp)
        speak = findViewById(R.id.speak)
//        titleTextView1 = findViewById(R.id.title_sec1)
//        titleTextView2 = findViewById(R.id.title_sec2)



     /// Assuming you receive the sections data from an Intent
        val sections: ArrayList<Bundle>? = intent.getParcelableArrayListExtra("sections")

        // Use the deserializeSections function to convert Bundles to Section objects
        val sectionsList: List<Section> = deserializeSections(sections)

//        val sectionRecyclerView: RecyclerView = findViewById(R.id.casesRecyclerView)
         sectionRecyclerView = findViewById(R.id.casesRecyclerView)


        // Initialize the class-level variable 'data'
        data = intent.getParcelableExtra("data")!!
        //  val sections = intent.getParcelableArrayListExtra<Section>("sections")

        if (data != null) {
            val title = data!!.title
            val description = data!!.description
            description1 = data!!.title_plain
            description2 = data!!.description_plain
//            val description3 = data!!.sections?.get(1)?.description_plain

            /* Log.e("datedetmm",""+description1)
             Log.e("datedetmm",""+description)
             Log.e("datedetmm",""+title)
             Log.e("datedetmm",""+description2)*/
//            Log.e("datedetmm",""+description3)


            //  Log.d("mytag","sections"+title1)
            //   val title1 = data.sections

            // Assuming title and description are nullable strings
            val safeTitle = title ?: ""
            val safeDescription = description ?: ""

            /*   titleTextView.text = Html.fromHtml(safeTitle, Html.FROM_HTML_MODE_LEGACY)
               descriptionTextView.text = Html.fromHtml(safeDescription, Html.FROM_HTML_MODE_LEGACY)*/

            titleTextView.text = description1.toString()
            descriptionTextView.text = description2.toString()


        }



// Initialize your adapter with the obtained sections list
         sectionAdapter = SectionAdapter(this, sectionsList, description1.toString(),description2.toString())

// Set up RecyclerView
        sectionRecyclerView.layoutManager = LinearLayoutManager(this)
        sectionRecyclerView.adapter = sectionAdapter


//        val imageIcon: ImageView = findViewById(R.id.up)
//        val imageIcon1: ImageView = findViewById(R.id.imageIcon)


//        descriptionTextView1 = findViewById(R.id.des_sec1)
//        descriptionTextView2 = findViewById(R.id.des_sec2)

        val chatsCard: LinearLayout = findViewById(R.id.chats)
        // Initialize the class-level variable 'caseId'
//        caseId = data.id.toString()
         isArchived = intent.getBooleanExtra("isArchived", false)


//        imageIcon.setOnClickListener {
//
//            showCustomDialog()
//        }
//        imageIcon1.setOnClickListener {
//
//            showCustomDialog()
//        }

        val backButton = findViewById<ImageView>(R.id.backbutton_case)
        backButton.setOnClickListener {

            /*val intent = Intent(this, CaseOfMonthActivity::class.java)
            startActivity(intent)*/
            finish()
        }
        // Assuming you have a reference to the connectivity manager
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

// Set OnClickListener for the Chats card
        chatsCard.setOnClickListener {
            if (isOnline(connectivityManager)) {
                // User is online, proceed with your action
                showBottomSheetDialog(data!!.case_id.toString(), delegateId)


                Log.d(ConstanstsApp.tag, "delegate ID chatid for chatcard: $delegateId" + data.case_id)
            } else {
                showNoInternetAlert("Please connect to the internet")

                // User is offline, show a message
            }
        }

        // Helper function to check internet connectivity


     /*   // Initialize the class-level variable 'data'
        data = intent.getParcelableExtra("data")!!
      //  val sections = intent.getParcelableArrayListExtra<Section>("sections")

        if (data != null) {
            val title = data!!.title
            val description = data!!.description
             description1 = data!!.title_plain
             description2 = data!!.description_plain
//            val description3 = data!!.sections?.get(1)?.description_plain

           *//* Log.e("datedetmm",""+description1)
            Log.e("datedetmm",""+description)
            Log.e("datedetmm",""+title)
            Log.e("datedetmm",""+description2)*//*
//            Log.e("datedetmm",""+description3)


          //  Log.d("mytag","sections"+title1)
         //   val title1 = data.sections

            // Assuming title and description are nullable strings
            val safeTitle = title ?: ""
            val safeDescription = description ?: ""

         *//*   titleTextView.text = Html.fromHtml(safeTitle, Html.FROM_HTML_MODE_LEGACY)
            descriptionTextView.text = Html.fromHtml(safeDescription, Html.FROM_HTML_MODE_LEGACY)*//*

            titleTextView.text = description1.toString()
            descriptionTextView.text = description2.toString()


        }*/




        Log.e("datedetmm1",""+description1+" "+description2+" "+allAdapterString)


        speak.setOnClickListener {
            var  data= " Acute cerebellar ataxia with COVID-19 infection COVID-19 infection can cause myriads of neurological manifestations including encephalopathy, encephalitis, ischemic strokes, intracerebral haemorrhage, encephalomyelitis, Bell’s palsy, Guillain-Barré syndrome and its variants, myalgia, myositis, rhabdomyolysis and rarely cerebellar ataxia.(1) Rare cases of opsoclonus-myoclonus ataxia syndrome have also been described(2).If the cerebellar ataxia occurs in the acute setting of an illness, the syndrome is labelled as acute or infectious. If there is a delay of a few weeks from the onset of infectious symptoms to the appearance of the ataxia, the term para-infectious or post-infectious is applied.(3) Case Descriptions Here we describe two such cases of acute infectious cerebellar ataxia with COVID-19 infection (Omicron variant).CASE 1: A 70-year-old male presented with a history of acute onset breathlessness and imbalance while walking since 4 days. On examination, he was afebrile and had respiratory distress with 90% saturation on room air. Oxygen was administered via nasal canula. He also had dysarthria, truncal and appendicular ataxia, dysmetria and he required support to sit or stand. Video 1So, clinical diagnosis of pan cerebellar ataxia was made. He tested positive for COVID-19. His MRI Brain with contrast was normal. CSF write full reportstudies revealed elevated proteins 67 mg/dl) and normal cells.He was treated with IV methylprednisolone pulse therapy for 5 days. The patient showed dramatic improvement and started walking without support on day 4 of treatment.video 2CASE 2: 70-year-old male with history of fever, cough and imbalance while walking and swaying to right since 3 days. He had mild dysarthria, right > no signs left appendicular ataxia and broad based gait with tendency to sway to the right. He tested positive for COVID-19.His MRI Brain with contrast revealed mild prominence in bilateral cerebellar folia. CSF studies revealed elevated proteins (78 mg/dl) and normal cells full report added tests. He was also treated with IV methylprednisolone pulse therapy. His ataxia recovered completely by the end of treatment.CSF autoimmune and paraneoplastic encephalitis antibody panel were negative in both the patients.Discussion Few cases of acute cerebellar ataxia with COVID-19 infection have been described in the literature.Most of the studies report neurological involvement in 20-40% of patients hospitalized for COVID-19 infection(4). COVID-19 virus may gain access to the central nervous system either through olfactory bulb or via the hematogenous route. The possible mechanisms responsible for the neurological involvement in COVID-19 infection are direct neuronal injury, immune-mediated injury, and hypoxic injury.(5)Acute cerebellar ataxia is defined as the acute onset of gait ataxia without fever, prominent meningismus, seizures, or a significant alteration of mental state (6). Acute cerebellitis is a neurological condition characterized by fever, headache, and altered mental state, dysarthria, nystagmus, truncal ataxia (7) and is known to occur with multiple infectious causes like typhoid, varicella zoster, mumps, rubella, etc. Acute cerebellitis is likely autoimmune in view of its postinfectious origin and the detection of autoantibodies in some patients targeting Purkinje cells, centrosomes, cardiolipin, and glutamic acid decarboxylase and glutamate receptors, and gangliosides.(8)Our patients had simultaneous onset of COVID-19 infection related symptoms and ataxia without any significant MRI changes and mild systemic signs and symptoms, fitting in the diagnosis of acute infectious cerebellar ataxia and not cerebellitis.MRI brain can help diagnose acute cerebellitis and can be classified in 3 main groups: bihemispheric cerebellitis, hemicerebellitis and cerebellitis with encephalitic findings. In acute cerebellar ataxia, MRI brain can be normal initially and may show some atrophic changes later.(8)CSF examination in acute cerebellar ataxia may show elevated proteins\n"

            speakUp.visibility =View.VISIBLE
            speak.visibility =View.GONE
//            textToSpeech?.speak(description1+" "+description2+" "+allAdapterString, TextToSpeech.QUEUE_FLUSH, null, null)
//            textToSpeech?.speak(data, TextToSpeech.QUEUE_FLUSH, null, null)

            speakOut(description1.toString()+" "+description2.toString()+" "+allAdapterString.toString())
        }

        speakUp.setOnClickListener {
            speakUp.visibility =View.GONE
            speak.visibility =View.VISIBLE
            textToSpeech?.stop()
//            textToSpeech?.shutdown()
//            textToSpeech?.speak(headerText+cleanedText, TextToSpeech.QUEUE_FLUSH, null, null)
        }


     /*   // Set up scroll listener
        sectionRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                isScrollingUp = dy < 0
//                handleScroll()
                Log.e("modiiii",""+dy)
                if (dy==0){
                    titleTextView.visibility =View.VISIBLE
                    descriptionTextView.visibility =View.VISIBLE

                }else{

                    titleTextView.visibility =View.GONE
                    descriptionTextView.visibility =View.GONE

                }
            }
        })*/



        }

private fun handleScroll() {
    if (isScrollingUp) {
        // Show your RecyclerView or specific items
//        sectionRecyclerView.visibility = View.VISIBLE
    } else {
        // Hide your RecyclerView or specific items
//        sectionRecyclerView.visibility = View.GONE
    }
}


    private fun speakOut(text: String) {
        // Text-to-Speech processing
        val maxLength = 4000  // Maximum length of text to process in one call

        if (text.length > maxLength) {
            // Split the text into chunks
            val chunks = text.chunked(maxLength)
            chunks.forEach { chunk ->
                textToSpeech?.speak(chunk, TextToSpeech.QUEUE_ADD, null, null)
            }
        } else {
            // Process the entire text
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }


    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language
            textToSpeech!!.setSpeechRate(0.9f)
            val langResult = textToSpeech?.setLanguage(Locale("hin"))
            if (langResult == TextToSpeech.LANG_MISSING_DATA ||
                langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
//                Toast.makeText(this, "Language is not supported or missing data.", Toast.LENGTH_LONG).show()
            } else {
                // Speak the text
//                textToSpeech?.speak("Hello, welcome to the Text to Speech example.", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        } else {
            Toast.makeText(this, "Initialization failed.", Toast.LENGTH_LONG).show()
        }
    }

    private fun showNoInternetAlert(message:String) {
        val dialog = Dialog(this)
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

    private fun isOnline(connectivityManager: ConnectivityManager): Boolean {
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    // Modify deserializeSections to return List<Section>
// Modify deserializeSections to return List<Section>
    private fun deserializeSections(serializedSections: ArrayList<Bundle>?): List<Section> {
        val sectionsList = mutableListOf<Section>()

        if (serializedSections != null && serializedSections.isNotEmpty()) {
            val stringBuilder = StringBuilder()
            for (serializedSection in serializedSections) {
                // Retrieve properties from the Bundle
                val description: String? = serializedSection.getString("description")
                val descriptionPlain: String? = serializedSection.getString("description_plain")
                val title: String? = serializedSection.getString("title")
                val titlePlain: String? = serializedSection.getString("title_plain")
                val media: ArrayList<Media>? = serializedSection.getParcelableArrayList("media")

//                Log.e("datedetmm",""+titlePlain + " "+descriptionPlain)
                allAdapterString = titlePlain + " "+descriptionPlain

                stringBuilder.append(allAdapterString)

                // Create a Section object and add it to the list
                val section = Section(
                    description = description,
                    description_plain = descriptionPlain,
                    title = title,
                    title_plain = titlePlain,
                            media = media

                )

                sectionsList.add(section)

            }
             allAdapterString = stringBuilder.toString().trim()
            Log.e("datedetmm",""+allAdapterString)
        }

        return sectionsList
    }


//    inner class FetchSectionsTask : AsyncTask<Void, Void, List<Section>>() {
//        override fun doInBackground(vararg params: Void?): List<Section> {
//            return try {
//                // Perform network request to fetch sections
//                val url = "https://www.telemedocket.com/BNA/public/getcases"
//                val jsonString = URL(url).readText()
//
//                // Parse JSON and return a list of Section objects
//                parseJsonSections(jsonString)
//            } catch (e: Exception) {
//                // Handle exceptions
//                emptyList()
//            }
//        }
//
//        override fun onPostExecute(result: List<Section>?) {
//            if (result != null && result.isNotEmpty()) {
//                sectionsList = result as MutableList<Section>
//             //   Log.d(ConstanstsApp.tag, "SectionList contents: $sectionsList")
//
//               // sectionAdapter.updateSections(sectionsList)
//            } else {
//                // Handle case when no sections are available
//            }
//        }
//
//
//        private fun parseJsonSections(jsonString: String): List<Section> {
//            val sections = mutableListOf<Section>()
//
//            try {
//                val json = JSONObject(jsonString)
//                val success = json.getInt("success")
//
//                if (success == 1) {
//                    val dataJsonArray = json.getJSONArray("data")
//
//                    for (i in 0 until dataJsonArray.length()) {
//                        val sectionJson = dataJsonArray.getJSONObject(i)
//
//                        val description = sectionJson.getString("description")
//                        val descriptionPlain = sectionJson.getString("description_plain")
//                        val title = sectionJson.getString("title")
//                        val titlePlain = sectionJson.getString("title_plain")
//                        // Print section data
//                        Log.d("mytag", "Title: $title")
//                        Log.d("mytag", "Description: $description")
//                        Log.d("mytag", "Title Plain: $titlePlain")
//                        Log.d("mytag", "Description Plain: $descriptionPlain")
//
////                        // Parse the 'media' array
////                        val mediaJsonArray = sectionJson.getJSONArray("media")
////                        val mediaList = ArrayList<Media>() // Change to ArrayList<Media>
////
////                        for (j in 0 until mediaJsonArray.length()) {
////                            val mediaJson = mediaJsonArray.getJSONObject(j)
////                            val media = Media(
////                                mediaJson.getString("imagelable"),
////                                mediaJson.getString("imagename"),
////                                mediaJson.getInt("imageno"),
////                                mediaJson.getString("imagetype")
////                            )
////                            mediaList.add(media)
////                        }
//
//                        val section = Section(
//                            description = description,
//                            description_plain = descriptionPlain,
//                            title = title,
//                            title_plain = titlePlain,
//                        )
//
//                        sections.add(section)
//                    }
//                }
//            } catch (e: JSONException) {
//                Log.e(ConstanstsApp.tag, "Error parsing JSON", e)
//            }
//
//            return sections
//        }    }


//    fun makeApiCall() {
//        val call = RetrofitInstance.apiSection.getSections()
//
//        call.enqueue(object : Callback<Section> {
//            override fun onResponse(call: Call<Section>, response: Response<Section>) {
//                if (response.isSuccessful) {
//                    val section = response.body()
//                    // Handle the API response
//
//                } else {
//                    // Handle unsuccessful response
//                }
//            }
//
//            override fun onFailure(call: Call<Section>, t: Throwable) {
//                // Handle failure
//            }
//        })
//
//    }



    class FullScreenBottomSheetFragment : BottomSheetDialogFragment() {

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.comment_bottom_dialog, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // Set full-screen behavior
            val behavior = BottomSheetBehavior.from(view.parent as View)
            behavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
            behavior.isHideable = false // Prevent hiding
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

            // Access the dialog's window and set full-screen attributes
            dialog?.window?.let {
                it.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    }


    @SuppressLint("SuspiciousIndentation")
    fun showBottomSheetDialog(caseId: String, delegateId: String?) {

        Log.e("opendiallof  ","yes")

 /*       val bottomSheet = FullScreenBottomSheetFragment()
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)*/



         bottomSheetDialog = BottomSheetDialog(this)
         bottomSheetView = layoutInflater.inflate(R.layout.comment_bottom_dialog, null)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
//        bottomSheetDialog.behavior.hideFriction

    val    delegateId = SharedPreferencesActivity.getDelegateId(this).toString()
        Log.d(ConstanstsApp.tag, "delegate ID for Chat: $delegateId")
        // Initialize views
         recyclerViewChats = bottomSheetView.findViewById(R.id.recyclerviewChats)
        val sendButton: ImageView = bottomSheetView.findViewById(R.id.sendButton)
        val speechMic: ImageView = bottomSheetView.findViewById(R.id.speechMic)
      //  val dividerView: View = bottomSheetView.findViewById(R.id.dividerView)

// Find views by ID inside the BottomSheetDialog
        val arrowImageView: ImageView = bottomSheetView.findViewById(R.id.arrowImageView)
        val commentsTextView: TextView = bottomSheetView.findViewById(R.id.comments)
        editTextChats = bottomSheetView.findViewById(R.id.editTextChats)




        // Check if the case is archived
        if (isArchived) {
            // If the case is archived, hide the EditText and send button
            editTextChats.visibility = View.GONE
            sendButton.visibility = View.GONE
            speechMic.visibility = View.GONE
        } else {
            // If the case is not archived, make the EditText and send button visible
            editTextChats.visibility = View.VISIBLE
            sendButton.visibility = View.VISIBLE
            speechMic.visibility = View.VISIBLE


        }
            arrowImageView.setOnClickListener{
    val intent = Intent(this, CaseOfMonthActivity::class.java)
    startActivity(intent)
}
        // Initialize chatsAdapter before using it
        chatsAdapter = ChatsAdapter(emptyList(), delegateId,isArchived)
        recyclerViewChats.adapter = chatsAdapter
        recyclerViewChats.layoutManager = LinearLayoutManager(this)
        val itemDecoration = DashLineItemDecoration(this)
        recyclerViewChats.addItemDecoration(itemDecoration)


        // Fetch chats using AsyncTask
        FetchChatsTask().execute(caseId)

        sendButton.setOnClickListener {

            if (editTextChats.text.toString().isNotEmpty()){

                Log.d(ConstanstsApp.tag, "send button click "+editTextChats.text.toString())

                val  delegateId = SharedPreferencesActivity.getDelegateId(this).toString()
                Log.d(ConstanstsApp.tag, "delegate ID for sendButton: $delegateId")
                Log.d(ConstanstsApp.tag, "case ID for sendButton: ${data.case_id}")
                val comment=editTextChats.text.toString()
                sendComment(comment,delegateId,data.case_id.toString())
                FetchChatsTask().execute(caseId)
            }else{
                Toast.makeText(this,"Please Enter text",Toast.LENGTH_SHORT).show()
            }

//            Toast.makeText(this,comment,Toast.LENGTH_SHORT).show()
//            Toast.makeText(this,caseId,Toast.LENGTH_SHORT).show()

        }

        /////////////////////////////



        speechMic.setOnClickListener {
            speak()
//            bottomSheetDialog.dismiss()
        }

        /////////////////////////////////


   /*     bottomSheetDialog.setOnShowListener {
            editTextChats.requestFocus()
            // Get the InputMethodManager
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            // Show the keyboard
            imm.showSoftInput(editTextChats, InputMethodManager.SHOW_IMPLICIT)
        }*/

    /*    // Set full-screen behavior
        val behavior = BottomSheetBehavior.from(this.parent as View)
        behavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
        behavior.isHideable = false // Prevent hiding
        behavior.state = BottomSheetBehavior.STATE_EXPANDED*/



      /*  // Show the dialog
        bottomSheetDialog.show();
        // Access the dialog's window and set full-screen properties
        if (bottomSheetDialog.getWindow() != null) {
            bottomSheetDialog.getWindow()!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            );
            bottomSheetDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            bottomSheetDialog.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation;*/
//        }

        // Show the BottomSheetDialog
        bottomSheetDialog.show()

        val window = bottomSheetDialog.window ?: return
        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val dialogHeight = (screenHeight * 0.9).toInt()


        bottomSheetDialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        bottomSheetDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bottomSheetDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        bottomSheetDialog.window!!.setGravity(Gravity.BOTTOM)




    }


    private fun applyFadeInAnimation(view: TextView, duration: Long) {
        // Set the initial alpha to 0 (fully transparent)
        view.alpha = 0f

        // Create an ObjectAnimator for the alpha property
        val fadeInAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f)

        // Set the duration for the animation
        fadeInAnimator.duration = duration

        // Start the animation
        fadeInAnimator.start()
    }

    private fun sendComment(comment: String, delegateId: String, id: String) {
        // Check if editTextChats is not null before accessing its properties
       /* if (editTextChats != null && editTextChats.length() > 0){*/
           // val commentText = editTextChats.text.toString().trim()

            Log.d(ConstanstsApp.tag, "Comment Text: $comment")
            Log.d(ConstanstsApp.tag, "delegateId: ${this.delegateId}")
            Log.d(ConstanstsApp.tag, "caseId: $caseId")

            if (!delegateId.isNullOrEmpty() && !id.isNullOrEmpty()) {
                val commentsend = Commentsend(
                    case_id = id.toString(),
                    comment = comment,
                    user = "Member",
                    user_id = delegateId!!,
                    is_replied = "0",
                    is_repID = "0"
                )

                SendCommentTask().execute(commentsend)
            } else {
                Log.e(ConstanstsApp.tag, "Invalid delegateId or caseId")
            }
     /*   } else {
            Log.e(ConstanstsApp.tag, "EditText is null")
            // Handle the case where editTextChats is null
        }*/
    }

    inner class SendCommentTask : AsyncTask<Commentsend, Void, SendResponse>() {

        override fun doInBackground(vararg params: Commentsend): SendResponse {
            try {
                val commentData = params[0]

                // Construct the URL for the API endpoint
                val apiUrl = "https://www.telemedocket.com/BNA/public/Addcomments" +
                        "?case_id=${commentData.case_id}" +
                        "&comment=${commentData.comment}" +
                        "&user=${commentData.user}" +
                        "&user_id=${commentData.user_id}" +
                        "&is_replied=${commentData.is_replied}" +
                        "&is_repID=${commentData.is_repID}"

                Log.d(ConstanstsApp.tag, "apiUrl: $apiUrl")

                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                // Get the response code
                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response from the input stream
                    val inputStream = connection.inputStream
                    val jsonString = inputStream.bufferedReader().use { it.readText() }

                    // Parse the JSON response
                    return parseJson(jsonString)
                } else {
                    // Handle the case when the server returns an error
                    Log.e(ConstanstsApp.tag, "API request failed with code $responseCode")
                }

                // Close the connection
                connection.disconnect()
            } catch (e: Exception) {
                // Handle exceptions that may occur during the network request
                Log.e(ConstanstsApp.tag, "API request failed: ${e.message}", e)
            }

            // Return a default SendResponse in case of failure
            return SendResponse(message = "", success = 0)
        }

        private fun parseJson(jsonString: String): SendResponse {
            val json = JSONObject(jsonString)
            val message = json.optString("message", "Default Message")
            val success = json.optInt("success", 0) // Use "success" instead of "status_code"

            return SendResponse(message, success)
        }


        override fun onPostExecute(result: SendResponse?) {

            if (result != null && result.success == 1) {
                Log.d(ConstanstsApp.tag, "Comment sent successfully")
                FetchChatsTask().execute(data.case_id.toString())
              editTextChats.setText(null)
                // You can perform additional actions based on the response
            } else {
                Log.d(ConstanstsApp.tag, "Failed to send comment")
            }
        }
    }


    inner class FetchChatsTask : AsyncTask<String, Void, List<org.bombayneurosciences.bna_2023.Model.chats.Data>>() {
        override fun doInBackground(vararg params: String?): List<org.bombayneurosciences.bna_2023.Model.chats.Data> {
            val caseId = params[0]

            if (isNetworkAvailable()) {
                Log.d(ConstanstsApp.tag, "Fetching chats from the network.")

                val url = "https://www.telemedocket.com/BNA/public/getcomments?case_id=$caseId"
                val jsonString = URL(url).readText()

                return parseJsonChats(jsonString)

                Log.e("datachat ","six")
            } else {
                Log.d(ConstanstsApp.tag, "No active network. Unable to fetch chats.")
                return emptyList()
                Log.e("datachat ","sven")
            }
        }

        override fun onPostExecute(result: List<org.bombayneurosciences.bna_2023.Model.chats.Data>?) {
            if (result != null && result.isNotEmpty()) {
            //    val commentsWithReplyCounts = calculateReplyCounts(result)

              //  chatsAdapter.updateChats(commentsWithReplyCounts)
                chatsAdapter.updateChats(result)
                Log.e("datachat  ","one")


             /*   chatsAdapter.updateChats(result)
                recyclerViewChats.adapter = chatsAdapter
                recyclerViewChats.layoutManager = LinearLayoutManager(this@CaseOfMonthActivity2)
*/
            } else {
                chatsAdapter.updateChats(emptyList())
                Log.e("datachat  ","two")
            }
        }

        private fun parseJsonChats(jsonString: String): List<org.bombayneurosciences.bna_2023.Model.chats.Data> {
            val chats = mutableListOf<org.bombayneurosciences.bna_2023.Model.chats.Data>()

            try {
                val json = JSONObject(jsonString)
                val success = json.getInt("success")
             Log.e("datachat  ","three")

                if (success == 1) {
                    val dataJsonArray = json.getJSONArray("data")
                    Log.e("datachat  ","four")

                    for (i in 0 until dataJsonArray.length()) {
                        val chatJson = dataJsonArray.getJSONObject(i)
                        val caseId = chatJson.getInt("case_id")
                        val commFrom = chatJson.getString("commFrom")
                        val commFromId = chatJson.getInt("commFromid")
                        val comment = chatJson.getString("comment")
                        val commFromname = chatJson.getString("commFromname")
                        val created_at = chatJson.getString("created_at")
                        val id = chatJson.getInt("id")
                        val isReplied = chatJson.getString("is_replied")
                        val reToCommid = chatJson.getInt("reTocommid")
                        val represent = chatJson.getInt("represent")

                        // Check if the key "replyCount" exists in the JSON object
                        val replyCount = if (chatJson.has("replyCount")) {
                            chatJson.getInt("replyCount")
                        } else {
                            0 // Default value if the key is not present
                        }

                        val chat = org.bombayneurosciences.bna_2023.Model.chats.Data(
                            caseId,
                            commFrom,
                            commFromId,
                            comment,
                            commFromname,
                            created_at,
                            id,
                            isReplied,
                            reToCommid,
                            represent,
                            replyCount
                        )
                        chats.add(chat)
                    }
                }
            } catch (e: JSONException) {
                Log.e(ConstanstsApp.tag, "Error parsing JSON", e)
                Log.e("datachat  ","five")
            }

            return chats
        }
    }

//    private fun calculateReplyCounts(comments: List<org.bombayneurosciences.bna_2023.Model.chats.Data>): MutableList<org.bombayneurosciences.bna_2023.Model.chats.Data> {
//        val commentsWithReplyCounts = mutableListOf<org.bombayneurosciences.bna_2023.Model.chats.Data>()
//
//        for (comment in comments) {
//            comment.replyCount = calculateReplyCount(comments, comment.id)
//            commentsWithReplyCounts.add(comment)
//
//            Log.d(ConstanstsApp.tag, "Comment ID: ${comment.id}, Reply Count: ${comment.replyCount}")
//        }
//
//        return commentsWithReplyCounts
//    }
//
//    private fun calculateReplyCount(comments: List<org.bombayneurosciences.bna_2023.Model.chats.Data>, parentId: Int): Int {
//        var replyCount = 0
//
//        for (comment in comments) {
//            if (comment.reTocommid == parentId) {
//                replyCount++
//                // Recursively calculate reply count for this comment's replies
//                replyCount += calculateReplyCount(comments, comment.id)
//            }
//        }
//
//        return replyCount
//    }
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

//    private fun showCustomDialog() {
//        if (data != null) {
//            val dialog = Dialog(this)
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            val bottomSheetView = layoutInflater.inflate(R.layout.case_bottomsheet, null)
//            dialog.setContentView(bottomSheetView)
//            val chatsCard: LinearLayout = findViewById(R.id.chats)
//
//            val radiologyRecyclerView =
//                bottomSheetView.findViewById<RecyclerView>(R.id.radiologyRecyclerView)
//            val pathologyRecyclerView =
//                bottomSheetView.findViewById<RecyclerView>(R.id.pathologyRecyclerView)
//            val videoRecyclerView =
//                bottomSheetView.findViewById<RecyclerView>(R.id.videoRecyclerView)
//
////            val radiologyMediaItems =
////                data!!.rImages.split(",").filter { it.isNotEmpty() }.map { it.trim() }
////            val pathologyMediaItems =
////                data!!.pImages.split(",").filter { it.isNotEmpty() }.map { it.trim() }
////            val videoMediaItems =
////                data!!.videos.split(",").filter { it.isNotEmpty() }.map { it.trim() }
//
//            // Initialize RecyclerViews in the bottom sheet
//            val radiologyLayoutManager =
//                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//            radiologyRecyclerView.layoutManager = radiologyLayoutManager
//            val radiologyAdapter =
//                RadiologyAdapter(this, radiologyMediaItems, this)
//            radiologyRecyclerView.adapter = radiologyAdapter
//
//            val pathologyLayoutManager =
//                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//            pathologyRecyclerView.layoutManager = pathologyLayoutManager
//            val pathologyAdapter = PathologyAdapter(this, pathologyMediaItems, this)
//            pathologyRecyclerView.adapter = pathologyAdapter
//
//            val videoLayoutManager =
//                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//            videoRecyclerView.layoutManager = videoLayoutManager
//            val videoAdapter = VideoAdapter(this, videoMediaItems, this)
//            videoRecyclerView.adapter = videoAdapter
//
//            val downArrowImageView = bottomSheetView.findViewById<ImageView>(R.id.down)
//
//            // Add an OnClickListener to the Downward Arrow ImageView
//            downArrowImageView.setOnClickListener {
//                // Dismiss the dialog when the Downward Arrow is clicked
//                dialog.dismiss()
//            }
//
//            dialog.show()
//            dialog.window!!.setLayout(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
//            dialog.window!!.setGravity(Gravity.BOTTOM)
//        }
//    }

    override fun onImageCaseItemClick(position: Int, imagepath: String, mediaType: String) {
        Log.d(ConstanstsApp.tag, "imagepath=>" + imagepath)
        Log.d(ConstanstsApp.tag, "mediaType=>" + mediaType)

        when (mediaType) {
            "R_IMAGE" -> {
                val intent = Intent(this, ImageViewverCaseOfMonth::class.java)
                intent.putExtra("imagepath", imagepath)
                startActivity(intent)
            }
            "P_IMAGE" -> {
                val intent = Intent(this, ImageViewverCaseOfMonth::class.java)
                intent.putExtra("imagepath", imagepath)
                startActivity(intent)
            }
            "VIDEO" -> {
                val intent = Intent(this, VideoViewerCaseOfMonth::class.java)
                intent.putExtra("imagepath", imagepath)
                startActivity(intent)
            }
        }
    }

    override fun speak() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE)

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"SPEAK NOW")
        startActivityForResult(intent,99)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data1: Intent?) {
        super.onActivityResult(requestCode, resultCode, data1)
        if (requestCode == 99 && resultCode == RESULT_OK){
//            val comment=data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!![0]
//                editTextChats.text.toString()
            editTextChats.setText(data1!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!![0])
            // Update the chat list
//            FetchChatsTask().execute(data.case_id.toString())
//            Log.e("speeckText",""+data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!![0])
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

       // sharedPreferencesManager1.setBackState(true)
        //sharedPreferencesManager1.setBottomMenuBar("CaseOfMonth")

       /* val intent = Intent(this, CaseOfMonthActivity::class.java)
        startActivity(intent)*/
        finish()

    }

    override fun onResume() {
        super.onResume()

        bottomSheetView = layoutInflater.inflate(R.layout.comment_bottom_dialog, null)
        recyclerViewChats = bottomSheetView.findViewById(R.id.recyclerviewChats)

        val    delegateId = SharedPreferencesActivity.getDelegateId(this).toString()
        Log.d(ConstanstsApp.tag, "delegate ID for Chat onResume: $delegateId")        // Initialize chatsAdapter before using it
        chatsAdapter = ChatsAdapter(emptyList(), delegateId!!,isArchived)
// Initialize chatsAdapter before using it
        recyclerViewChats.adapter = chatsAdapter
        recyclerViewChats.layoutManager = LinearLayoutManager(this)
        FetchChatsTask().execute(data.case_id.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
//        sectionAdapter.shutdown()
        textToSpeech?.stop()
        textToSpeech?.shutdown()

    }

    override fun onPause() {
        super.onPause()
//        sectionAdapter.shutdown()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }

    override fun onStop() {
        super.onStop()
//        sectionAdapter.shutdown()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }





}
