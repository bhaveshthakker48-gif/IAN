package org.bombayneurosciences.bna_2023.JournalNewFolder

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.AsyncTask
import android.os.Bundle
import android.os.ParcelFileDescriptor
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
import com.bumptech.glide.Glide
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData
import org.bombayneurosciences.bna_2023.CallBack.JournalDataClick
import org.bombayneurosciences.bna_2023.JournalNewFolder.StartActivity.Companion.screenchange
import org.bombayneurosciences.bna_2023.R
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

class MOnthFilterDataAdapter(private val context: Context,
                             private val sectionsList: List<MonthArticles1>)
    : RecyclerView.Adapter<MOnthFilterDataAdapter.SectionViewHolder>(){

    private lateinit var pdfFile: File

    class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val TextView_date: TextView = itemView.findViewById(R.id.TextView_date)
        val TextView_vol: TextView = itemView.findViewById(R.id.TextView_vol)
        val pdfView: ImageView = itemView.findViewById(R.id.pdfView)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.past_data_layout, parent, false)
        return SectionViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val currentSection = sectionsList[position]

       /* Log.e("llllll",""+currentSection.articles)
        Log.e("llllll",""+currentSection.month)*/
        holder.TextView_date.text = currentSection.month+" "+currentSection.year
        holder.TextView_vol.text="Vol."+currentSection.volume+" | Issue "+currentSection.issue
//        holder.pdfView.set(currentSection.cover)

        pdfFile = File(context.cacheDir, currentSection.month)

        pdfFile.delete()

        if (pdfFile.exists()) {
               renderPdf()
            holder.pdfView.setImageBitmap(renderPdf())
            Log.e("uuuuuuu",""+" oooo")

        } else {
            Log.e("uuuuuuu",""+" lllllll")
            // PDF file has not been downloaded, download it first
            DownloadPdfTask(holder).execute(currentSection.cover)
        }

        DownloadPdfTask(holder).execute(currentSection.cover)


        holder.itemView.setOnClickListener{
            val intent = Intent(context, ArchivesActivity::class.java)
            intent.putExtra("month", currentSection.month)
            intent.putExtra("coverPage", currentSection.cover)
            intent.putParcelableArrayListExtra("articles", ArrayList(currentSection.articles)) // Pass articles list


            context.startActivity(intent)
        }
/*
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ArchivesActivity::class.java).apply {
                putParcelableArrayListExtra(
                    "EXTRA_ARTICLES_LIST",
                    ArrayList(currentSection.articles)
                )
            }
            context.startActivity(intent)
        }*/

    }



    override fun getItemCount(): Int {
        return sectionsList.size
    }




    private inner class DownloadPdfTask(private val holder: SectionViewHolder) : AsyncTask<String, Void, Bitmap>() {

        override fun doInBackground(vararg params: String): Bitmap? {
            return downloadPdf(params[0])
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(bitmap: Bitmap?) {
            super.onPostExecute(bitmap)
            if (bitmap != null) {
//                imageView.setImageBitmap(bitmap)
//                pdfView.setImageBitmap(bitmap)
                Log.e("show_data_list",""+bitmap)
                holder.pdfView.setImageBitmap(bitmap)




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



}
