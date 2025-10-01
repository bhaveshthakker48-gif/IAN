package org.bombayneurosciences.bna_2023

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.media.MediaMetadataRetriever
import android.media.RingtoneManager
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.leolin.shortcutbadger.ShortcutBadger
import org.bombayneurosciences.bna_2023.Activity.MainActivity
import org.bombayneurosciences.bna_2023.Activity.SplashActivity
import org.bombayneurosciences.bna_2023.Data.RetrofitInstance
import org.bombayneurosciences.bna_2023.Model.NotificationRead
import org.bombayneurosciences.bna_2023.Model.Token
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SharedPreferencesManagerToken
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ExecutionException


class MyFirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "FirebaseMessagingService"

    var bitmap:Bitmap? =null

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("mytag","in service token=>"+token)
        Log.d(ConstanstsApp.tag, "token_mode" + token)
        val sharedPreferencesManager = SharedPreferencesManagerToken(applicationContext)

// Retrieve token
        val retrievedToken = sharedPreferencesManager.getToken()

// Ensure the retrieved token is not null before saving it again
        if (retrievedToken != null) {
            sharedPreferencesManager.saveToken(retrievedToken)
        }
// Clear token
      //  sharedPreferencesManager.clearToken()
    }



//    @SuppressLint("LongLogTag")
  public  override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
       /* Log.d(TAG, "Dikirim dari: ${remoteMessage.from}")
        Log.d("notify1","remote message"+remoteMessage.toString())
        Log.d("notify1","remote message"+remoteMessage.notification!!.title)
        Log.d("notify1","remote message"+remoteMessage.notification!!.body)
        Log.d("notify1","remote message"+remoteMessage.notification!!.link)

        if (remoteMessage.notification != null) {
            showNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
        }*/


    val gson = Gson()
    val jsonData = gson.toJson(remoteMessage.data)
    val jsonData1 = gson.toJson(remoteMessage.notification)
    Log.d("FCM", "Message data payload (JSON): $jsonData")
    Log.d("FCM", "Message data payload (JSON): $jsonData1")

//    showNotification1(remoteMessage.notification!!.title.toString(),remoteMessage.notification!!.body.toString(), applicationContext)
        // Handle data payload

  /*  if (remoteMessage.notification!=null){
        remoteMessage!!.notification!!.title?.let { remoteMessage!!.notification!!.body?.let { it1 ->
            showNotification1(it,
                it1, applicationContext)
        } }
    }*/

//    if (remoteMessage.data.isNotEmpty()){


        remoteMessage.data.let { data ->
            val link = data["link"]
            val title = data["title"]
            val body = data["body"]
            val imageUrl = data["attachment"]
            val notification_id = data["notification_id"]
            val device_token = data["device_token"]
            // Process data here
            Log.e("notify1",""+link)
            Log.e("notify1",""+title)
            Log.e("notify1",""+body)
            Log.e("notify1",""+imageUrl)


            // Generate a unique notification ID
            val notificationId = System.currentTimeMillis().toInt()

            showNotification2(title, body, link, imageUrl,notificationId,device_token,notification_id)

            // Update badge count
            updateBadgeCount()
            // Load the image in a background thread
          /*  CoroutineScope(Dispatchers.IO).launch {
//                val bitmap = loadImageFromUrl(imageUrl)
                withContext(Dispatchers.Main) {

                }
            }
*/


        }
//    }


    }



    private fun showNotification2(title: String?, body: String?,link: String?,image: String?,notificationId: Int,token: String?,notificationIDapi: String?) {


        val notificationsEnabled = areNotificationsEnabled(applicationContext)
        if (notificationsEnabled) {
            // Notifications are enabled
            notificationRecieve(token!!,notificationIDapi!!,getCurrentDateTime())

            Log.e("permition",""+"enable")
        } else {
            Log.e("permition",""+"not enable")
            // Notifications are disabled
            // You may want to inform the user and direct them to system settings
        }

     /*   val bitmap: Bitmap? = when {
            mediaUrl != null && mediaType == "video" -> generateVideoThumbnail(mediaUrl)
            mediaUrl != null && mediaType == "gif" -> loadGifThumbnail(this, mediaUrl)
            mediaUrl != null && mediaType == "image" -> loadImageFromUrl(mediaUrl)
            else -> null
        }
        */

        val bitmap: Bitmap? = if (image != null && image.endsWith(".pdf", ignoreCase = true)) {
//            null // Handle PDF differently, don't try to load as a bitmap
            generatePdfThumbnail(this, image)
        } else if (image != null && image.endsWith(".mp4", ignoreCase = true)) {
            generateVideoThumbnail( image)

        }else if (image != null && image.endsWith(".gif", ignoreCase = true)) {
            loadGifThumbnail(this, image)

        }else {
            loadImageFromUrl(image)
        }


        val channelId = "your_channel_id"
//        val notificationId = 1

        // Load the image in a background thread


        // Create an Intent for the activity you want to start
        val intent = Intent(this, SplashActivity::class.java).apply {
            putExtra("EXTRA_KEY", link) // String data

            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Create the NotificationChannel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Channel Name", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Channel Description"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Build and display the notification
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Replace with your own icon
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


/*
        // If bitmap is not null, set it as a BigPictureStyle notification
        bitmap?.let {
            builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(it))
            builder.setLargeIcon(it)
        }*/


        bitmap?.let {
            // Scale the bitmap to ensure it fits properly in the notification
            val scaledBitmap = Bitmap.createScaledBitmap(it, 800, 400, false) // Adjust width and height as needed

            builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(scaledBitmap))
            builder.setLargeIcon(scaledBitmap)  // Set the scaled bitmap as the large icon
        }

       /* if (bitmap != null) {
            // If it's an image, show it in the notification
            builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            builder.setLargeIcon(bitmap)

        }*/ /*else if (image != null && image.endsWith(".pdf", ignoreCase = true)) {
            // If it's a PDF, just indicate that it's a PDF in the notification
            builder.setStyle(NotificationCompat.BigTextStyle().bigText("PDF document attached"))
        }

*/

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(notificationId, builder.build())
        }
    }

    fun getCurrentDateTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return current.format(formatter)
    }



    fun areNotificationsEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
    private fun notificationRecieve(device_token: String,notification_id: String,received_at: String) {
        val sharedPreferencesManager = SharedPreferencesManagerToken(applicationContext)

        Log.d(ConstanstsApp.tag, "token_mode" + device_token)
        Log.d(ConstanstsApp.tag, "notification_id_mode" + notification_id)
        Log.d(ConstanstsApp.tag, "received_at_mode" + received_at)

        val apiToken = RetrofitInstance.apiToken
        val call = apiToken.notificationRecieve(device_token, notification_id, received_at,"android")
        // Enqueue the call to make it asynchronous
        call.enqueue(object : Callback<NotificationRead> {
            override fun onResponse(call: Call<NotificationRead>, response: Response<NotificationRead>) {

                Log.e("notificationread"," "+response.body()!!.status)
           /*     when (response.body()!!.success) {
                    0 -> {
                        val token = response.body()!!.message

                        Log.d(ConstanstsApp.tag, "response of token send 0 =>" + token)
                    }
                    1 -> {
                        val token = response.body()!!.message

                        Log.d(ConstanstsApp.tag, "response of token send 1 =>" + token)
                    }
                }*/
            }
            override fun onFailure(call: Call<NotificationRead>, t: Throwable) {
                // Handle failure
                Log.e("notificationread"," not")
            }
        })
    }


    private fun updateBadgeCount() {
        // Your logic to update the badge count
        // Example for Samsung, Sony, HTC, and other devices with custom launchers:
        // ShortcutBadger.applyCount(context, count)

        val badgeCount = getBadgeCount() + 1 // Increment badge count
        setBadgeCount(badgeCount)
    }

    private fun getBadgeCount(): Int {
        // Retrieve the current badge count from shared preferences or a database
        val sharedPreferences = getSharedPreferences("badge_pref", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("badge_count", 0)
    }

    private fun setBadgeCount(badgeCount: Int) {
        // Save the updated badge count to shared preferences
        val sharedPreferences = getSharedPreferences("badge_pref", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("badge_count", badgeCount)
            apply()
        }

        // Apply badge count to launcher (for devices that support it)
        ShortcutBadger.applyCount(this, badgeCount)
    }

    private fun loadImageFromUrl(url: String?): Bitmap? {
        return try {
            if (url.isNullOrEmpty()) return null
            Glide.with(this)
                .asBitmap()
                .load(url)
                .submit()
                .get()
        } catch (e: ExecutionException) {
            Log.e("FCM", "Error loading image", e)
            null
        } catch (e: InterruptedException) {
            Log.e("FCM", "Error loading image", e)
            null
        }
    }
    private fun generatePdfThumbnail(context: Context, pdfUrl: String): Bitmap? {
        return try {
            val url = URL(pdfUrl)
            val connection = url.openConnection()
            val inputStream = connection.getInputStream()
            val file = File(context.cacheDir, "temp_pdf.pdf")
            file.outputStream().use { output -> inputStream.copyTo(output) }

            val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(fileDescriptor)
            val page = pdfRenderer.openPage(0)

            // Create a bitmap with the size of the first page
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)

            // Render the page to the bitmap
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            // Close everything
            page.close()
            pdfRenderer.close()
            fileDescriptor.close()

            bitmap
        } catch (e: IOException) {
            Log.e(TAG, "Error generating PDF thumbnail", e)
            null
        }
    }


    private fun generateVideoThumbnail(videoUrl: String): Bitmap? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoUrl, HashMap())
            val bitmap = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            retriever.release()
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error generating video thumbnail", e)
            null
        }
    }
    private fun loadGifThumbnail(context: Context, gifUrl: String): Bitmap? {
        return try {
            Glide.with(context)
                .asBitmap()
                .load(gifUrl)
                .submit()
                .get()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading GIF thumbnail", e)
            null
        }
    }



        private fun showNotification1(title: String, body: String, context: Context) {
            val channelId = "your_channel_id"
            val notificationId = 1

            // Create the NotificationChannel (for Android 8.0 and above)
            val channel = NotificationChannel(channelId, "Channel Name", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Channel Description"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            // Build and display the notification
            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }
                notify(notificationId, builder.build())
            }
        }
        private fun showNotification(title: String?, body: String?) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(0, notificationBuilder.build())
        }
}