package org.bombayneurosciences.bna_2023.Fragment

import android.app.Dialog
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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bombayneurosciences.bna_2023.Activity.JournalActivity
import org.bombayneurosciences.bna_2023.Activity.MainActivity
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData
import org.bombayneurosciences.bna_2023.CallBack.JournalDataClick
import org.bombayneurosciences.bna_2023.CustomPageUtils.CustomBookFlipAnimation1
import org.bombayneurosciences.bna_2023.JournalNewFolder.WelcomeElement
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.adapter.JournalBottomAdapter
import org.bombayneurosciences.bna_2023.databinding.ActivityJournal2Binding
import org.bombayneurosciences.bna_2023.databinding.ActivityJournalView3Binding
import org.bombayneurosciences.bna_2023.databinding.ActivityJournalViewpagerBinding
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp.Companion.getFileNameFromUrl
import java.io.File
import java.io.IOException

class JournalViewActivity3 : AppCompatActivity(), View.OnClickListener, JournalDataClick {

    private lateinit var binding: ActivityJournalView3Binding
    private lateinit var pdfFiles: ArrayList<File>
    var pdfFileNames1: List<String> = emptyList()
    private var adapter: PdfCompositeAdapter? = null

    lateinit var dialog:Dialog
//    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJournalView3Binding.inflate(layoutInflater)
        setContentView(binding.root)


        pdfFileNames1= mutableListOf(
          /*  "issue_20240629082804.pdf",
            "article_20240629145535.pdf",
            "article_20240629145425.pdf",
            "article_20240629145216.pdf",
            "article_20240629144243.pdf",
            "article_20240629143854.pdf",
            "article_20240629143644.pdf",
            "article_20240629142940.pdf",
            "article_20240629140857.pdf",
            "article_20240629140813.pdf",
            "article_20240629130016.pdf",*/

         /*   "BNA_Journal_Cover_Pic.pdf",
            "BNA_Journal_Cover_Pic.pdf",
            "BNA_Journal_Cover_Pic.pdf",
            "BNA_Journal_Cover_Pic.pdf",
            "BNA_Journal_Cover_Pic.pdf",
            "BNA_Journal_Cover_Pic.pdf",
            "BNA_Journal_Cover_Pic.pdf",
            "BNA_Journal_Cover_Pic.pdf",
            "BNA_Journal_Cover_Pic.pdf",
            "BNA_Journal_Cover_Pic.pdf",
            "BNA_Journal_Cover_Pic.pdf",*/

            "BNA_Journal_Cover_Pic.pdf",

            "article_20240418062902.pdf",
            "article_20240418071151.pdf",
            "article_20240418074109.pdf",
            "article_20240420082757.PDF",
            "article_20240418080549.pdf",
            "article_20240418081148.pdf",
            "article_20240418085519.pdf",
            "article_20240419112704.pdf",
            "article_20240419112916.pdf",
            "article_20240419113109.pdf",
            "article_20240419113155.pdf",
            "article_20240419113228.pdf",
            "article_20240419113501.pdf",
            "article_20240419113604.pdf",
            "article_20240419074901.pdf",
            "article_20240419112326.pdf",
            "article_20240419114033.pdf",
            "article_20240419173515.pdf",
            "article_20240419173647.pdf",
            "article_20240419173813.pdf",
            "article_20240419173925.pdf",
            "article_20240629130016.pdf",
            "article_20240629140813.pdf",
            "article_20240629140857.pdf",
            "article_20240629142940.pdf",
            "article_20240629143644.pdf",
            "article_20240629143854.pdf",
            "article_20240629144243.pdf",
            "article_20240629145216.pdf",
            "article_20240629145425.pdf",
            "article_20240629145535.pdf",
            "issue_20240629082804.pdf",
























        )


      /*   pdfFileNames1= mutableListOf(
            "issue_20240629082804.pdf",
            "article_20240629145535.pdf",
            "article_20240629145425.pdf",
            "article_20240629145216.pdf",
            "article_20240629144243.pdf",
            "article_20240629143854.pdf",
            "article_20240629143644.pdf",
            "article_20240629142940.pdf",
            "article_20240629140857.pdf",
            "article_20240629140813.pdf",
            "article_20240629130016.pdf",
            "BNA_Journal_Cover_Pic.pdf",
             "article_20240418062902.pdf",
             "article_20240418071151.pdf",
             "article_20240418074109.pdf",

            "article_20240419173925.pdf",
            "article_20240419173813.pdf",
            "article_20240419173647.pdf",
            "article_20240419173515.pdf",
            "article_20240419114033.pdf",
            "article_20240419112326.pdf",
            "article_20240419074901.pdf",
            "article_20240419113604.pdf",
            "article_20240419113501.pdf",
            "article_20240419113228.pdf",
            "article_20240419113155.pdf",
            "article_20240419113109.pdf",
            "article_20240419112916.pdf",
            "article_20240419112704.pdf",
            "article_20240418085519.pdf",
            "article_20240418081148.pdf",
            "article_20240418080549.pdf",
            "article_20240420082757.PDF"
        )*/



        // Retrieve PDF file names from intent extras
        // val pdfFileNames = intent.getStringArrayListExtra("pdf_file_names")!!.reversed()

        val pdfFileNames = intent.getStringArrayListExtra("pdf_file_names")!!



        val journalData = intent.getSerializableExtra("journal_data") as? JournalLoacalData

//        binding.eventheader.text=journalData!!.title
        binding.eventheader.text=journalData!!.month+" "+journalData.year+" : "+"Vol. "+ journalData.volume +" | "+"Issue "+journalData.issue_no

        val pdfFileName = ConstanstsApp.getFileNameFromUrl(journalData!!.articleFile)

        val gson = Gson()
        val json = gson.toJson(pdfFileNames)
        Log.e("modimodi4",""+json)
        Log.e("modimodi6",""+pdfFileName)



        binding.backbutton.setOnClickListener(this)
        binding.journalMenu.setOnClickListener(this)

//        setCustomPageViewer(pdfFileNames,"article_20240418062902.pdf")
//        setCustomPageViewer(pdfFileNames,"BNA_Journal_Cover_Pic.pdf")
        setCustomPageViewer(pdfFileNames,pdfFileName)
//        setCustomPageViewer(pdfFileNames1,pdfFileName)
    }




    override fun onBackPressed() {
        super.onBackPressed()

       /* val intent=Intent(this,JournalActivity::class.java)
        startActivity(intent)*/
        finish()
    }

    override fun onClick(v: View?) {
        when(v)

        {
            binding.backbutton->
            {
                this.onBackPressed()
//              finish()
            }
            binding.journalMenu -> {

                binding.journalMenu.isEnabled = false

                // Retrieve the JSON string from the Bundle
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
                }

            }
        }
    }



    private fun setCustomPageViewer(filteredArticlesList: List<String>, initialStartFileName: String?) {
        binding.viewPager.visibility = View.INVISIBLE
        binding.progressBar.visibility = ProgressBar.VISIBLE

//////////////////////////////
        /*  // Filter out the item "BNA_Journal_Cover_Pic.pdf" and reverse the list
          val filteredList = filteredArticlesList.filterNot { it.equals("BNA_Journal_Cover_Pic.pdf", ignoreCase = true) }
          val reversedList = filteredList.reversed()*/



     /*   // The specific item you want to ensure is at index 0
        val specificItem = "BNA_Journal_Cover_Pic.pdf"

        // Remove the specific item if it exists
        val updatedList = filteredArticlesList.filterNot { it.equals(specificItem, ignoreCase = true) }

        val reversedList = updatedList.reversed()


        val finalList = listOf(specificItem) + reversedList

        val gson1 = Gson()
        val json1 = gson1.toJson(finalList)
        Log.e("modimodi123",""+json1)*/


        ///////////////////////
     /*  //new code split
        var finalList: List<String> = emptyList()
        // Define the split point
        val splitPoint = "article_20240419173925.pdf"

        // Find the index of the split point
        val splitIndex = filteredArticlesList.indexOf(splitPoint)

        // Check if the split point exists in the list
        if (splitIndex != -1) {
            // Split the list
            val part1 = filteredArticlesList.subList(0, splitIndex)
            val part2 = filteredArticlesList.subList(splitIndex, filteredArticlesList.size)

            // Print the results
            println("Part 1:")
            println(part1)
            Log.e("modimodi111",""+part1)
            println("\nPart 2:")
            println(part2)
            Log.e("modimodi111",""+part2)

            val reversedList = part2.reversed()

             finalList = part1 + reversedList
            Log.e("modimodi111",""+finalList.toString())


        } else {
            println("The split point '$splitPoint' was not found in the list.")
        }
*/






        ///////////////////////////////////////

        var startFileName = initialStartFileName

        pdfFiles = ArrayList()
        pdfFiles.clear()

        for (fileName in pdfFileNames1) {
            Log.e("fileNamea",""+fileName)
            (pdfFiles as ArrayList<File>).add(File("/storage/emulated/0/Android/data/org.bombayneurosciences.bna_2023/files/BNA_App_PDF/$fileName"))
        }

        val gson = Gson()
        val json = gson.toJson(filteredArticlesList)
        Log.e("modimodi",""+json)
        Log.e("modimodi1",""+initialStartFileName)

        if (startFileName.isNullOrEmpty()) {
            startFileName = filteredArticlesList.first()
            Log.e("modimodi2",""+startFileName)
        }



        //////////////////////

        // Ensure the list has at least one element to set at index 0


        //////////////////////////

        try {
            val startPosition = calculateStartPosition(filteredArticlesList, startFileName!!)
            adapter = this?.let { PdfCompositeAdapter(it, pdfFiles as ArrayList<File>, startPosition) }
            binding.viewPager.adapter = adapter





            binding.viewPager.setCurrentItem(startPosition-58, true)

            Handler(Looper.getMainLooper()).postDelayed({
                // Hide loader after 3000 milliseconds (3 seconds) and start MainActivity
                binding.progressBar.visibility = ProgressBar.GONE
                binding.viewPager.visibility = View.VISIBLE
            }, 3000)




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
        binding.viewPager.visibility = View.INVISIBLE
        binding.progressBar.visibility = ProgressBar.VISIBLE

//////////////////////////////
        /*  // Filter out the item "BNA_Journal_Cover_Pic.pdf" and reverse the list
          val filteredList = filteredArticlesList.filterNot { it.equals("BNA_Journal_Cover_Pic.pdf", ignoreCase = true) }
          val reversedList = filteredList.reversed()*/


        // The specific item you want to ensure is at index 0
        val specificItem = "BNA_Journal_Cover_Pic.pdf"
//        val specificItem1 = "https://telemedocket.com/BNA/public/uploads/Issues_file/BNA_Journal_Cover_Pic.pdf"

//        https://telemedocket.com/BNA/public/uploads/Issues_file/BNA_Journal_Cover_Pic.pdf
//        https://telemedocket.com/BNA/public/uploads/Issues_file/BNA_Journal_Cover_Pic.pdf


        // Remove the specific item if it exists
        val updatedList = filteredArticlesList.filterNot { it.equals(specificItem, ignoreCase = true) }

        val reversedList = updatedList.reversed()


        val finalList = listOf(specificItem) + reversedList

        val gson1 = Gson()
        val json1 = gson1.toJson(finalList)
        Log.e("modimodi123",""+json1)


        ///////////////////////

        var startFileName = initialStartFileName

       pdfFiles.clear()
        pdfFiles = ArrayList()
        for (fileName in filteredArticlesList) {
            (pdfFiles as ArrayList<File>).add(File("/storage/emulated/0/Android/data/org.bombayneurosciences.bna_2023/files/BNA_App_PDF/$fileName"))
        }
        // Reverse the pdfFiles list
        pdfFiles.reverse()

        val gson = Gson()
        val json = gson.toJson(filteredArticlesList.reversed())
        Log.e("modimodi",""+json)
        Log.e("modimodi1",""+initialStartFileName)


        if (startFileName.isNullOrEmpty()) {
            startFileName = filteredArticlesList.first()
            Log.e("modimodi2",""+startFileName)
        }


        //////////////////////

        // Ensure the list has at least one element to set at index 0


        //////////////////////////

        try {
            val startPosition = calculateStartPosition(finalList, startFileName!!)
            adapter = this?.let { PdfCompositeAdapter(it, pdfFiles as ArrayList<File>, startPosition) }
            binding.viewPager.adapter = adapter



                   Log.e("postionNumber",""+startPosition)
            if (startPosition>1){
                binding.viewPager.setCurrentItem(startPosition-1, true)
            }else{
                binding.viewPager.setCurrentItem(startPosition, true)
            }



            Handler(Looper.getMainLooper()).postDelayed({
                // Hide loader after 3000 milliseconds (3 seconds) and start MainActivity
                binding.progressBar.visibility = ProgressBar.GONE
                binding.viewPager.visibility = View.VISIBLE
            }, 3000)




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
            Log.e("pdfnameshow",""+pdfFile.name)
            adapter?.notifyItemChanged(position)
        }
    }

    private fun setJournalBottomDialog(
        response: MutableList<JournalLoacalData>,
        journalData: JournalLoacalData?
    ) {
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

            // Append " " + abbreviatedMonth + " " + lastTwoDigits in red color
//            builder.appendColoredText(" $abbreviatedMonth $year", ContextCompat.getColor(this,R.color.dark_red))
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
    }

    override fun JournalItemClicked(data: JournalLoacalData, position: Int, view: View) {

        dialog.dismiss()

        val pdfFileNames = intent.getStringArrayListExtra("pdf_file_names")!!
        setCustomPageViewer1(pdfFileNames,getFileNameFromUrl(data.articleFile))
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



}
