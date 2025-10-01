package org.bombayneurosciences.bna_2023.adapter

import android.content.Context
import android.speech.tts.TextToSpeech
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.Model.CaseofMonth.Section
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.Locale
import java.util.regex.Pattern

class SectionAdapter(private val context: Context, private val sectionsList: List<Section>,private  val title:String,private  val description:String) : RecyclerView.Adapter<SectionAdapter.SectionViewHolder>(),
    TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null


    init {
        // Initialize TextToSpeech
        textToSpeech = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
//            textToSpeech?.language = Locale.US

//                   int lang = textToSpeech.setLanguage(Locale.ENGLISH);
            textToSpeech!!.setSpeechRate(0.9f)
            textToSpeech!!.setLanguage(Locale("hin"))
        }
    }


    // ViewHolder class
    class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: WebView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: WebView = itemView.findViewById(R.id.descriptionTextView)
        val mediaRecyclerView: RecyclerView = itemView.findViewById(R.id.mediaRecyclerView)
        val header: TextView = itemView.findViewById(R.id.header_section)
        val discrip: TextView = itemView.findViewById(R.id.discrip)
        val header_normal: TextView = itemView.findViewById(R.id.header_normal)
        val discrip_normal: TextView = itemView.findViewById(R.id.discrip_normal)
        val normalheader: LinearLayout = itemView.findViewById(R.id.normalheader)
        val buttonSpeak: ImageView = itemView.findViewById(R.id.speak)
        val speakUp: ImageView = itemView.findViewById(R.id.speakUp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.sections_case_recyclerview, parent, false)
        return SectionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val currentSection = sectionsList[position]
            /* Log.e("sunilmodi",""+currentSection.title)
             Log.e("modisunil",""+currentSection.description)*/
              if (position==0){
                  Log.e("modibro",""+title)
                  Log.e("modibro",""+description)
                  holder.normalheader.visibility = View.VISIBLE
                  holder.header_normal.text= title.toString()
                  holder.discrip_normal.text= description.toString()
              }else{
                  holder.normalheader.visibility = View.GONE
              }

        // Bind data to UI elements
        //holder.titleTextView.text = currentSection.title
       // holder.descriptionTextView.text = currentSection.description

        //val htmlContent: String = currentSection.title!!
        //val htmlContent1: String = currentSection.description!!

        //val htmlContent = "${currentSection.title}<br><br>${currentSection.description}"

        /*val htmlContent = """
            <html>
            <head>
            <style>
            body {
                text-align: justify;
            }
            </style>
            </head>
            <body>
            ${currentSection.title}<br><br>${currentSection.description}
            </body>
            </html>
        """.trimIndent()*/
        // Load HTML content into WebView
       // holder.titleTextView.loadData(htmlContent, "text/html", "UTF-8")


        //header
        val cleanedContent = removeSpanStyles(currentSection.title.toString())
        // Remove the inline style from the <p> tag

// Remove the inline style from the <p> tag
      val  htmlContentHeader = cleanedContent.replace(Regex("""<p[^>]*>"""), "<p>")

//       val htmlContentHeader1 = htmlContentHeader.replace("<strong>", "").replace("</strong>", "")

        //dis
        val cleanedContent1 = removeSpanStyles(currentSection.description.toString())
        val cleanedContent2 = removeSpanStyles1(cleanedContent1.toString())
        val  htmlContentDis = cleanedContent2.replace(Regex("""<p[^>]*>"""), "<p>")


        Log.e("modisunil",""+htmlContentHeader)
        holder.titleTextView.settings.javaScriptEnabled = true
        val htmlContent = """
    <!DOCTYPE html>
    <html>
    <head>
    <style>
    body {
        text-align: justify;
        font-family: Arial, sans-serif; /* Specify desired font */
        line-height: 1.6; /* Adjust line height as needed */
          margin: 0;
         padding: 0;
    }
   h1 {
   
    text-align: justify;
      font-family: Arial, sans-serif; /* Specify desired font */
        line-height: 1.6; /* Adjust line height as needed */
        font-size: 16px; /* Adjust heading size as needed */
            margin-top: 0;
            margin-bottom: 10px; /* Adjust margin as needed */
    }
    p {
     text-align: justify;
       font-family: Arial, sans-serif; /* Specify desired font */
         line-height: 1.6; /* Adjust line height as needed */
        font-size: 16px; /* Ensures that paragraphs use the same font size as the body text */
        margin: 0 0 10px 0; /* Adjust margin as needed */
    }
    </style>
    </head>
    <body>
    <h1>${htmlContentHeader}</h1>
    <p>${htmlContentDis}</p>
    </body>
    </html>
""".trimIndent()


        // Enable JavaScript if needed


// Load HTML content with fixed font size
//        val htmlContent = """
//    <html>
//    <head>
//        <style>
//            body {
//                font-size: 8px; /* Set your desired fixed font size */
//                line-height: 1.5; /* Optional: improve readability */
//            }
//        </style>
//    </head>
//    <body>
//        <h1>Hello, World!</h1>
//        <p>This is a sample WebView content with a fixed font size.</p>
//    </body>
//    </html>
//""".trimIndent()



// Load HTML content into WebView
 /*       holder.titleTextView.settings.apply {
            javaScriptEnabled = true
            builtInZoomControls = true
            displayZoomControls = false
            loadWithOverviewMode = true
            useWideViewPort = true

        }*/



//        holder.textview.text = currentSection.title
//        holder.titleTextView.settings.textZoom = 5



        // Initialize the WebView

     /*   holder.titleTextView.settings.javaScriptEnabled = true // Enable JavaScript if needed
        holder.titleTextView.settings.useWideViewPort = true // Enable wide viewport
        holder.titleTextView.settings.loadWithOverviewMode = true // Load content in overview mode
        holder.titleTextView.settings.setSupportZoom(false) // Disable zoom
        holder.titleTextView.webViewClient = WebViewClient() // To handle page navigation
*/

        holder.titleTextView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)



        //  holder.descriptionTextView.loadData(htmlContent1, "text/html", "UTF-8")

        Log.d(ConstanstsApp.tag,"title in section adapter"+currentSection.title)
        Log.d(ConstanstsApp.tag,"description in section adapter"+currentSection.description)
        Log.e("webdataget",""+htmlContent.toString())
//        Log.e("webdataget",""+currentSection.title_plain)
//        Log.e("webdataget",""+currentSection.description_plain)

        ///////////////////////////////////////////////////////////////////

/*
// Parse the HTML content
        val doc: Document = Jsoup.parse(htmlContent)

        // Extract <h1> tags
        val h1Elements: List<Element> = doc.select("h1")
        println("H1 Tags:")
        for (element in h1Elements) {
            println(element.text())
            Log.e("sunilbrocode1",""+element.text())
        }
*/


        // Parse the HTML content
//        val doc: Document = Jsoup.parse(htmlContent)

     /*   // Extract <p> elements
        val pElements = doc.select("p")

        // Check if <p> elements are not empty
        if (pElements.isEmpty()) {
            println("No <p> tags found.")
            Log.e("sunilbrocode2",""+pElements.text())
        } else {
            println("P Tags:")
            for (pElement in pElements) {
                // Extract content from nested <div> tags inside <p> tags
                val divs = pElement.select("div")
                for (div in divs) {
                    // Print text content of <div> elements
                    val text = div.text().trim()
                    if (text.isNotEmpty()) {
                        println(text)
                        Log.e("sunilbrocode3",""+text)
                    } else {
                        println("Empty or non-visible content.")
                        Log.e("sunilbrocode4",""+"Empty or non-visible content.")
                    }
                }
            }
        }
*/


/*        // Extract <p> tags
        val pElements = doc.select("p")

        // Check if the <p> elements list is empty
        if (pElements.isEmpty()) {
            println("No <p> tags found.")
            Log.e("sunilbrocode2",""+"null")
        } else {
            println("P Tags:")
            for (pElement in pElements) {
                // Extract content from nested <div> tags inside <p> tags
                val divs = pElement.select("div")
                for (div in divs) {
                    println(div.text())

                    Log.e("sunilbrocode3",""+div.text())
                }
            }
        }*/




   /*     // Extract <p> tags
        val pElements: List<Element> = doc.select("p")
        println("\nP Tags:")
        for (element in pElements) {
            println(element.text())
            Log.e("sunilbrocode2",""+ element.text())
        }*/


     /*   // Extract <p> tags
        val pElements: List<Element> = doc.select("p")
        println("\nP Tags:")

        for (element in pElements) {
            Log.e("sunilbrocode2",""+ element.text())
            // Extract content from nested <div> tags inside <p> tags
            val divs = element.select("div")
            for (div in divs) {
                println(div.text())
                Log.e("sunilbrocode3",""+div.text())
            }
        }*/

     /*   // Extract text separated by <br> tags within <p> tags
        println("\nText separated by <br> tags within <p> tags:")
        for (pElement in pElements) {
            val spans = pElement.select("span")
            for (span in spans) {
                println(span.text())
                Log.e("sunilbrocode5",""+span.text())
            }
        }*/
/////////////////////////////////////////////////////////////////////////////////
        // Parse HTML and extract text
        val document: Document = Jsoup.parse(htmlContent)

        Log.e("documentget",""+document)

        // Extract header (h1) text
        val headerText: String = document.select("h1").text()
        holder.header.text = headerText

        // Extract body (p) texts


        ////////////////////
        // Select relevant elements
     /*   val elements: Elements = document.select("p, div")

        // Extract text and handle <br> tags
        val bodyText: String = elements.joinToString("\n") { element ->
            val textWithLineBreaks = element.html().replace("<br>", "\n")
            Jsoup.parse(textWithLineBreaks).text() // Parse again to remove remaining HTML entities or tags
        }

        // Print the result
        println(bodyText)*/

        ///////////////////////

     /*   val bodyText: String = document.select("p, div,<br>").joinToString("\n") { element ->
            element.html().replace(Regex("<br>"), "\n")
        }*/

        val bodyText: String = document.select("p, div").joinToString("\n") { it.text() }


          // Extract <p> tags
      val pElements: List<Element> = document.select("p, div,  li")
//        Log.e("sunilbrocode4",""+pElements)
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
        holder.discrip.text = cleanedText.toString()

/////////////////////////////////////////////////////////////////////////////////////////////////


      /*  // Regex patterns
        val headerPattern = Pattern.compile("<h1>.*?</h1>", Pattern.DOTALL)
        val tagPattern = Pattern.compile("</?html.*?>|</?head.*?>|</?style.*?>|</?body.*?>|</?p.*?>|</?strong.*?>")
        val brPattern = Pattern.compile("<br>")

        // Extract header
        val headerMatcher = headerPattern.matcher(htmlContent)
        val header = if (headerMatcher.find()) headerMatcher.group(0) else ""

        // Remove header and other tags
        var bodyContent = htmlContent.replaceFirst(headerPattern.toRegex(), "")
        bodyContent = tagPattern.matcher(bodyContent).replaceAll("")

        // Replace <br> with newlines
        bodyContent = brPattern.matcher(bodyContent).replaceAll("\n")

        // Remove remaining HTML tags
        bodyContent = bodyContent.replace(Regex("<[^>]+>"), "").trim()

        // Split by newlines and clean up any empty lines
        val lines = bodyContent.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

        // Print results
        println("Header:")
        println(header)

        println("\nContent:")
        lines.forEach { println(it) }

        Log.e("fffffff",""+header)
        Log.e("fffffff",""+ lines.forEach { println(it) })
*/

        ////////////////////////////////////////////////////////////////////////////////////////////

        /*holder.header.text = headerText
        holder.discrip.text = cleanedText.toString()*/


      /*  holder.header.text =currentSection.title_plain
        holder.discrip.text = currentSection.description_plain*/

/*
        Log.e("fffffff",""+currentSection.title_plain)
        Log.e("fffffff",""+currentSection.description_plain)*/

        Log.e("fffffff",""+headerText)
        Log.e("fffffff",""+cleanedText)

        holder.buttonSpeak.setOnClickListener {
            holder.speakUp.visibility =View.VISIBLE
            holder.buttonSpeak.visibility =View.GONE
            textToSpeech?.speak(headerText+cleanedText, TextToSpeech.QUEUE_FLUSH, null, null)
        }

        holder.speakUp.setOnClickListener {
            holder.speakUp.visibility =View.GONE
            holder.buttonSpeak.visibility =View.VISIBLE
            textToSpeech?.stop()
//            textToSpeech?.shutdown()
//            textToSpeech?.speak(headerText+cleanedText, TextToSpeech.QUEUE_FLUSH, null, null)
        }


       /* // Parse the HTML content using Jsoup
        val document: Document = Jsoup.parse(currentSection.title.toString())

        // Select all <p> elements
        val paragraphs: Elements = document.select("p")

        // Iterate through paragraphs and print the text
        for (paragraph in paragraphs) {
            println(paragraph.text())
            Log.e("ddddddd",""+paragraph.text().toString())
        }*/




     /*   val text = currentSection.title?.let { extractTextFromHtml(it) }
        println(text)
        Log.e("webdataget",""+text)
        holder.textview.text = text.toString()*/


        // Set up the RecyclerView for media
        val mediaAdapter = MediaAdapter1(context, currentSection.media ?: emptyList())
        holder.mediaRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.mediaRecyclerView.adapter = mediaAdapter
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
   /* fun extractTextFromHtml(html: String): String {
        // Parse the HTML content using Jsoup
        val document = Jsoup.parse(html)
        // Extract and return the text content
        return document.text()
    }*/

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
}
