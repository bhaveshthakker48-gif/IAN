package org.bombayneurosciences.bna_2023.JournalNewFolder

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData
import org.bombayneurosciences.bna_2023.CallBack.JournalDataClick
import org.bombayneurosciences.bna_2023.JournalNewFolder.StartActivity.Companion.screenchange
import org.bombayneurosciences.bna_2023.R
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.Locale

class SubSectionAdapter(private val context: Context,
                        private val sectionsList: List<Subsection>,
                        private  val title:String,
                        private  val description:String,
                        private val itemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<SubSectionAdapter.SectionViewHolder>(){

    // ViewHolder class

    interface OnItemClickListener {
        fun onInnerItemClick(item: Subsection, position: Int)
    }


    class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val webView: WebView = itemView.findViewById(R.id.titleTextView)
        val header: TextView = itemView.findViewById(R.id.header_sectionn)
        val discrip: TextView = itemView.findViewById(R.id.discripp)
        val mediaRecyclerView: RecyclerView = itemView.findViewById(R.id.mediaRecyclerView)
        val fl_zoom: LinearLayout = itemView.findViewById(R.id.fl_zoom)
        val transparentOverlay: View = itemView.findViewById(R.id.transparentOverlay)
//        val zoom_layout: ZoomLayout = itemView.findViewById(R.id.zoom_layout)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.sub_section_layout, parent, false)
        return SectionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val currentSection = sectionsList[position]






        if (position==0){
            Log.e("modiji",""+title)
            Log.e("modiji",""+description)
            holder.header.visibility = View.GONE
            holder.discrip.visibility = View.GONE
            holder.header.text = title.toString()
            holder.discrip.text = description.toString()
        }else{
            Log.e("modiji",""+" null")
            holder.header.visibility = View.GONE
            holder.discrip.visibility = View.GONE
        }



        Log.e("modibro",""+title)
        Log.e("modibro",""+description)


        //header
        val cleanedContent = removeSpanStyles(currentSection.absubdescription.toString())
        // Remove the inline style from the <p> tag

// Remove the inline style from the <p> tag
        val  htmlContentHeader = cleanedContent.replace(Regex("""<p[^>]*>"""), "<p>")




//       val htmlContentHeader1 = htmlContentHeader.replace("<strong>", "").replace("</strong>", "")

        //dis
        val cleanedContent1 = removeSpanStyles(currentSection.subdescription.toString())
        val cleanedContent2 = removeSpanStyles1(cleanedContent1.toString())
        val  htmlContentDis = cleanedContent2.replace(Regex("""<p[^>]*>"""), "<p>")

    /*   val  htmlContentDis = cleanedContent2.replace(Regex("""<style[^>]*>.*?</style>""", RegexOption.DOT_MATCHES_ALL), "")

    // Step 2: Remove inline style attributes from any HTML tag
    .replace(Regex("""\s*style=["'][^"']*["']"""), "")

    // Step 3: Optional - Remove other unwanted attributes like "class", "id", "onclick"
    .replace(Regex("""\s*class=["'][^"']*["']"""), "")
    .replace(Regex("""\s*id=["'][^"']*["']"""), "")
    .replace(Regex("""\s*onclick=["'][^"']*["']"""), "")*/



        val fontSize = if (isTablet(context)){
            26f

        } else {
            14f
        }  // Set font size based on device type

//        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        val htmlContent = """
    <!DOCTYPE html>
    <html>
    <head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=3.0, user-scalable=yes">


      
    <style>
  body {
        text-align: justify;
        font-family: Arial, sans-serif; /* Specify desired font */
        line-height: 1.6; /* Adjust line height as needed */
          margin: 0;
         padding: 0;
          font-style: normal;
          
    }
   h1 {
   
    text-align: justify;
      font-family: Arial, sans-serif; /* Specify desired font */
        line-height: 1.6; /* Adjust line height as needed */
         font-size: ${fontSize}px; /* Adjust paragraph font size */
            margin-top: 0;
            margin-bottom: 10px; /* Adjust margin as needed */
              font-weight: normal; 
    }
    p {
     text-align: justify;
       font-family: Arial, sans-serif; /* Specify desired font */
         line-height: 1.6; /* Adjust line height as needed */
        font-size: ${fontSize}px; /* Adjust paragraph font size */
        margin: 10px 0 10px 0; /* Adjust margin as needed */
    }
    table {
                width: 100%;
                border-collapse: collapse;
            }
            th, td {
                border: 1px solid #ddd;
                padding: 8px;
                text-align: left;
                word-wrap: break-word;
            }
            .table-container {
                overflow-x: auto;
                -webkit-overflow-scrolling: touch;
            }
            img {
                max-width: 100%;
                height: auto;
            }
    </style>
    </head>
    <body>
   <p>${htmlContentDis}</p>
    </body>
    </html>
""".trimIndent()


        /*<div class="table-container">
        ${htmlContentDis} <!-- Insert your CKEditor HTML content here -->
        </div>*/
//        <p>${htmlContentDis}</p>
        /*<h1>${htmlContentHeader}</h1>*/

        // Create a WebView instance

//        holder.webView.settings.javaScriptEnabled = true
       holder.webView.settings.setSupportZoom(true)
        holder.webView.settings.builtInZoomControls = true
        holder.webView.settings.displayZoomControls = false
       holder.webView.settings.loadWithOverviewMode=true
       holder.webView.settings.useWideViewPort=true
       holder.webView.settings.domStorageEnabled=true
        holder.webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        holder.webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

        // Set up the WebView settings to ensure proper scaling and content fitting
     /*   holder.webView.settings.apply {
            javaScriptEnabled = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN // Makes the content fit the screen width
            loadWithOverviewMode = true // Ensures the content scales properly
            useWideViewPort = true // Allows scaling to fit the screen
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }*/


        // Setting up WebView
       /* holder.webView.settings.apply {
            javaScriptEnabled = true
            setSupportZoom(false) // Disable zoom in WebView
            builtInZoomControls = false // Disable built-in zoom controls
            displayZoomControls = false
        }*/

        holder.webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        Log.e("htmlContentHeader"," "+htmlContent)


        holder.webView.webViewClient = object : WebViewClient() {
            private var lastScale = 1.0f // Initialize to the original scale

            override fun onScaleChanged(view: WebView, oldScale: Float, newScale: Float) {
                super.onScaleChanged(view, oldScale, newScale)
//                itemClickListener.onInnerItemClick(currentSection, 100)
                // Check if zoom is at original size (0% zoom)
                if (newScale.toString() =="1.0") {
                    Log.e("ZoomEvent", "Original size (0% zoom) detected")
                    holder.header.visibility= View.GONE
                    holder.discrip.visibility= View.GONE
                } else if (newScale > lastScale) {

                    // Zooming in
                    Log.e("ZoomEvent123", newScale.toString())
                    holder.header.visibility= View.GONE
                    holder.discrip.visibility= View.GONE
                } else if (newScale < lastScale) {
                    Log.e("ZoomEvent1 ", newScale.toString())
                    Log.e("ZoomEvent2 ", lastScale.toString())
//                    holder.header.visibility= View.VISIBLE
//                    holder.discrip.visibility= View.VISIBLE
                    // Zooming out
                    if (newScale.toString().equals("3.0")){
                        holder.header.visibility= View.GONE
                        holder.discrip.visibility= View.GONE
                        Log.e("ZoomEvent", "Zoom Out detected")
                    }

                }
                lastScale = newScale
           /*     // Update the last scale for the next comparison
                lastScale = newScale

                // Optionally apply the zoom scale to both header and description
                // Applying the zoom scale to both header and description based on WebView's scale
                val scaleFactor = newScale / oldScale
                holder.header.textSize = holder.header.textSize * scaleFactor
                holder.discrip.textSize = holder.discrip.textSize * scaleFactor*/
            }
        }

        holder.webView.setOnTouchListener { v, event ->
            if (event.pointerCount > 1) {
                holder.webView.parent.requestDisallowInterceptTouchEvent(true)
                Log.e("modiiii"," "+"one")

//                holder.header.textSize = holder.header.textSize * 0.1f
//                holder.discrip.textSize = holder.discrip.textSize * 0.1f

            } else {
                holder.webView.parent.requestDisallowInterceptTouchEvent(false)
//                itemClickListener.onInnerItemClick(currentSection, position)
                Log.e("modiiii"," "+"two")
                screenchange ="3"
                Log.e("screenchn1",""+ screenchange)

                // Apply the zoom scale to both header and description
//                holder.header.textSize = holder.header.textSize * 0.2f
//                holder.discrip.textSize = holder.discrip.textSize * 0.2f

            }
            false
        }


   /*     // TextView Pinch-to-Zoom for header and description
        val scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            private var scaleFactor = 1.0f

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor

                // Limit the zoom scale (for readability)
                scaleFactor = 0.5f.coerceAtLeast(scaleFactor.coerceAtMost(2.0f))

                // Apply the zoom scale to both header and description
                holder.header.textSize = holder.header.textSize * scaleFactor
                holder.discrip.textSize = holder.discrip.textSize * scaleFactor

                return true
            }
        })*/

/*

        // Apply touch listener to detect pinch gesture for TextViews
        holder.header.setOnTouchListener { v, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }
        holder.discrip.setOnTouchListener { v, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }
*/



        holder.webView.setOnLongClickListener {
            // Handle long press action here
            Log.e("LongTouch", "Long press detected")

            // If you want to invoke the same item click action:
            itemClickListener.onInnerItemClick(currentSection, position)

            // Return true to indicate that the long click is handled
            true
        }




        holder.transparentOverlay.setOnClickListener {
            itemClickListener.onInnerItemClick(currentSection, position)


            Log.e("mmmmmmmmm ",""+"hhhh")
        }


//        holder.webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)

        ////////////////////////////////////////////////////////////

        // Parse HTML and extract text
        val document: Document = Jsoup.parse(htmlContent)

        Log.e("documentget",""+document)

        // Extract header (h1) text
        val headerText: String = document.select("h1").text()
//        holder.header.text = headerText



        val bodyText: String = document.select("p, div").joinToString("\n") { it.text() }


        // Extract <p> tags
        val pElements: List<Element> = document.select("p, div")
        println("\nP Tags:")
        val concatenatedText = StringBuilder()
        // Initialize a Set to hold unique texts
        val uniqueTexts = mutableSetOf<String>()

        // Extract text separated by <br> tags within <p> tags
        println("\nText separated by <br> tags within <p> tags:")

        if (pElements.isEmpty()) {
            println("No <p> tags found.")
            Log.e("sunilbrocode2",""+"null")
        } else {
            for (pElement in pElements) {
                val spans = pElement.select("span")
                for (span in spans) {
                    println(span.text())
                    Log.e("sunilbrocode5",""+span.text())
                    uniqueTexts.add(span.text())
//                 concatenatedText.append(span.text()).append("\n")
                }
            }
        }

        // Convert the set to a single string
        val finalText = uniqueTexts.joinToString(separator = "\n")

        // Set text to TextViews



        Log.e("ddddddd1",""+headerText)

        Log.e("ddddddd2",""+bodyText)


//        val parts = bodyText.split("$headerText")
        val parts = finalText.split("$headerText")
//        val cleanedText = parts.toString().replace("[", "").replace(",","").replace("]", "")
        val cleanedText = parts.toString().replace("[", "").replace(",","").replace("]", "")


        Log.e("ddddddd3",""+parts)
//        holder.discrip.text = cleanedText.toString()



        // Set up the RecyclerView for media
        val mediaAdapter = ImagesAdapter(context, currentSection.images ?: emptyList())
        holder.mediaRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        holder.mediaRecyclerView.adapter = mediaAdapter

        ///////////////////////////////////////////////////////////

    }



    override fun getItemCount(): Int {
        return sectionsList.size
    }


    fun removeSpanStyles(content: String): String {
        // Regular expression to match the span tags with specific style attributes
        val regex = """<span style="[^"]*">""".toRegex()

//        htmlContent = htmlContent.replace(Regex("""<p[^>]*>"""), "<p>")

        // Replace the matched span tags with just the span tag
        val withoutStyles = regex.replace(content, "<span>")

        // Remove any closing span tags that might be empty
        return withoutStyles.replace("</span>", "")
    }
    fun removeSpanStyles1(content: String): String {
        // Regular expression to match the span tags with specific style attributes
        val regex = """<div style="[^"]*">""".toRegex()

        // Replace the matched span tags with just the span tag
        val withoutStyles = regex.replace(content, "<span>")

        // Remove any closing span tags that might be empty
        return withoutStyles.replace("</div>", "")
    }
    fun isTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

}
