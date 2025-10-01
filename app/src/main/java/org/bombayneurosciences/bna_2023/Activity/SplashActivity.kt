package org.bombayneurosciences.bna_2023.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import org.bombayneurosciences.bna_2023.R

class SplashActivity : AppCompatActivity() {

    private var isLoaderVisible = false
    private lateinit var progressDialog: Dialog

    lateinit var textView: TextView
    private val REQUEST_CODE_PERMISSIONS = 101



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ensures your layout fits inside system bars
//        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContentView(R.layout.activity_splash)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }


        textView=findViewById(R.id.textView_Version)



        textView.text="ver."+getAppVersion()

        // Show loader when splash screen starts
     //   showCustomProgressDialog()

      /*  Handler(Looper.getMainLooper()).postDelayed({
            // Hide loader after 3000 milliseconds (3 seconds) and start MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)*/



        val notificationData = intent.getStringExtra("EXTRA_KEY")
        Log.e("sunilmdoi",""+notificationData)

        if (notificationData==null){
            // Check permissions on app launch
            if (!checkPermissions()) {
                requestPermissions();
            } else {
                // Permissions already granted, proceed with your logic
                // Example: startCameraPreview();
                navigateToMain()
            }


//            Log.e("notificationData",""+notificationData)
        }else{
            if (notificationData.equals("Journal")){
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("notification", "Journal")
                startActivity(intent)
                finish()
                Log.e("notificationData",""+notificationData)
            }else if (notificationData.equals("Case of the Month")){
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("notification", "CaseMonth")
                startActivity(intent)
                finish()
            }else if (notificationData.equals("Scientifc Program")){
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("notification", "Scientifc")
                startActivity(intent)
                finish()
            }else if (notificationData.equals("Select Notification about")){
                navigateToMain()
            }


        }
    }

    private fun getAppVersion(): String? {
        try {
            val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            return pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "Unknown"
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val notificationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY)
        val notification = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)

        return cameraPermission == PackageManager.PERMISSION_GRANTED &&
                storagePermission == PackageManager.PERMISSION_GRANTED &&
                locationPermission == PackageManager.PERMISSION_GRANTED &&
                notificationPermission == PackageManager.PERMISSION_GRANTED &&
                notification == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                Manifest.permission.POST_NOTIFICATIONS
            ),
            REQUEST_CODE_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            var allPermissionsGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }

            if (allPermissionsGranted) {
                // All permissions granted, proceed with your logic
                // Example: startCameraPreview()
                navigateToMain()
            } else {
                // Permissions not granted, handle accordingly
                // Example: disableCameraFunctionality()
                navigateToMain()
            }
        }
    }

    private fun navigateToMain()
    {
        Handler(Looper.getMainLooper()).postDelayed({
            // Hide loader after 3000 milliseconds (3 seconds) and start MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }





}
