package org.bombayneurosciences.bna_2023.JournalNewFolder

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bombayneurosciences.bna_2023.CallBack.JournalDataClick
import org.bombayneurosciences.bna_2023.JournalNewFolder.StartActivity.Companion.screenchange

import org.bombayneurosciences.bna_2023.R


class ViewPagerAdapter(
    private val context: Context,
    private var dataList: List<WelcomeElement>,
    private var link: Bitmap?,
    val DataClick: JournalDataClick,
    private val innerClickListener: SubSectionAdapter.OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_STATIC = 0
        const val VIEW_TYPE_DYNAMIC = 1
    }



    // Define ViewHolder for the static page
    class StaticViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val staticText: TextView = itemView.findViewById(R.id.staticTextView)
        val webView: ImageView = itemView.findViewById(R.id.webView)


       /* fun bindStaticContent( dataList:WelcomeElement) {
            staticText.text = dataList.cover
        }*/
    }

    // Reuse the CaseViewHolder from LetestAdapter for dynamic pages
    class DynamicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titel: TextView = itemView.findViewById(R.id.titel)
        val articel: TextView = itemView.findViewById(R.id.articel)
        val auther: TextView = itemView.findViewById(R.id.auther)
        val desRecyclerView: RecyclerView = itemView.findViewById(R.id.desRecyclerView)
        val pdffile: ImageView = itemView.findViewById(R.id.pdffile)
        val fl_zoom: LinearLayout = itemView.findViewById(R.id.fl_zoom)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return if (viewType == VIEW_TYPE_STATIC) {
            val view = inflater.inflate(R.layout.static_page_layout, parent, false)
            StaticViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.letest_jurnul, parent, false)
            DynamicViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
//            val data = dataList[position]
//            (holder as StaticViewHolder).bindStaticContent(data)
            val static = holder as StaticViewHolder
//            static.staticText.text = link
            static.webView.setImageBitmap(link)


            static.itemView.setOnLongClickListener {
                Log.e("longpress", "Long press detected")

                // Handle the long press action here
                screenchange = "long_press_mode"
                Log.e("screenchn1", screenchange)

                // Perform your long press action here, for example, call a different function
                DataClick.ItemClicked(link!!, position)

                // Return true to indicate the long press has been handled
                true
            }

          /*  static.itemView.setOnClickListener(View.OnClickListener {
                Log.e("touchone"," "+"one")
                screenchange="2"
                Log.e("screenchn1",""+ screenchange)

                DataClick.ItemClicked(link!!,position)
            })*/


         /*   // Configure WebView settings
            val webSettings: WebSettings = static.webView.settings
            webSettings.javaScriptEnabled = true // Enable JavaScript if necessary

            // Load PDF URL using Google Docs Viewer
            val pdfUrl =link
            val googleDocsViewerUrl = "https://docs.google.com/gview?embedded=true&url=$pdfUrl"

            static.webView.webViewClient = WebViewClient() // Ensure links open in the WebView
            static.webView.loadUrl(googleDocsViewerUrl)
            // Hide scrollbars
            static.webView.isVerticalScrollBarEnabled = false
            static.webView.isHorizontalScrollBarEnabled = false*/


        } else {
            val data = dataList[position - 1] // Adjust position to account for static page
            val dynamicHolder = holder as DynamicViewHolder

           /* dynamicHolder.itemView.setOnClickListener(View.OnClickListener {
                Log.e("touchone"," "+"two")
            })*/

            dynamicHolder.pdffile.visibility = View.GONE
            dynamicHolder.fl_zoom.visibility = View.VISIBLE

        if (isTablet(context)){
                holder.articel.textSize=25f
                holder.auther.textSize=25f

            } else {
            holder.articel.textSize=12f
            holder.auther.textSize=12f
            }  // Set font size based on device type


            holder.articel.text = data.title
            holder.auther.text = data.author


            Log.e("touchone"," "+ data.subsections)
            // Set up media adapter
            val mediaAdapter = SubSectionAdapter(context, data.subsections, data.title, data.author!!,innerClickListener)
            dynamicHolder.desRecyclerView.layoutManager = LinearLayoutManager(context)
            dynamicHolder.desRecyclerView.adapter = mediaAdapter






        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_STATIC else VIEW_TYPE_DYNAMIC
    }

    override fun getItemCount(): Int {
        return dataList.size + 1 // Add 1 for the static page
    }

   /* fun refreshItem(position: Int) {
        Log.e("ViewPagerNotify", "Refreshing content for page: $position")
        // Logic to refresh the item at the given position
        // This might involve fetching new data or simply notifying the adapter
        notifyItemChanged(position)
    }*/
    fun refreshItem(position: Int) {
        Log.e("ViewPagerNotify", "Scheduling refresh for page: $position")
        // Launch a coroutine for delayed execution
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000) // Delay for 2 seconds
            Log.e("ViewPagerNotify", "Refreshing content for page: $position")
            notifyItemChanged(position) // Notify adapter to refresh the item
        }
    }

    fun updateData(filteredData: List<WelcomeElement>,link1:Bitmap) {
        dataList = filteredData
        link = link1
        notifyDataSetChanged()
    }

 /*   override fun onInnerItemClick(item: Subsection, position: Int) {
        Log.e("ssssss ",""+position)
    }*/

    fun isTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }
}
