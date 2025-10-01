package org.bombayneurosciences.bna_2023.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bombayneurosciences.bna_2023.ALodingDialog
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_Repository
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_ViewModel
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RD_ViewModelFactory
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.BNA_RoomDatabase
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DAO.Journal_DAO
import org.bombayneurosciences.bna_2023.Data.RetrofitInstance
import org.bombayneurosciences.bna_2023.Data.SharedPreferencesActivity
import org.bombayneurosciences.bna_2023.Data.SharedPreferencesManager
import org.bombayneurosciences.bna_2023.JournalNewFolder.StartActivity
import org.bombayneurosciences.bna_2023.Model.Notification.Data
import org.bombayneurosciences.bna_2023.Model.Notification.NotificationDataClass
import org.bombayneurosciences.bna_2023.Model.Token
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.Roomdb.AppDatabase
import org.bombayneurosciences.bna_2023.Roomdb.NotificationEntity
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import org.bombayneurosciences.bna_2023.utils.SessionManagerSingleton
import org.bombayneurosciences.bna_2023.utils.SharedPreferencesManagerToken
import org.bombayneurosciences.bna_2023.utils.sharepreferenceAppkill
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var appDatabase: AppDatabase
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var sharedPreferencesManager1: SessionManager1
    private lateinit var SharepreferenceAppkill: sharepreferenceAppkill
    private var latestNotification: Data? = null
    var isLoggedIn: Boolean = false
    private var isLoaderVisible = false
    private lateinit var progressDialog: Dialog
    private lateinit var committeeCard: CardView
    private lateinit var loginBtn: ImageView
    private lateinit var viewAllTextView: TextView
    private lateinit var privacyCard: LinearLayout
    private lateinit var journalCard: CardView
    private lateinit var eventCard: CardView
    private lateinit var caseCard: CardView
    private lateinit var notificationTitleView: TextView
    private lateinit var notificationDescriptionView: TextView
    private lateinit var aLodingDialog: ALodingDialog
    var LatestNotificationData: ArrayList<Data>? = null
    private lateinit var appUpdateManager: AppUpdateManager
    var token: String? = null
    var Acesstoken: String? = null
    private lateinit var progressDialog1: ProgressDialog


    private lateinit var sharedPreferences: SharedPreferences


    var sharedpreferences: SharedPreferences? = null
    val Login_PREFERENCES = "Login_Prefs"
    var Checked: Boolean = true
    var username: Int = 0
    lateinit var sessionManager: SessionManager1
    lateinit var viewModel1: BNA_RD_ViewModel

    private var privacyPolicyDialog: AlertDialog? = null


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false
            val readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false

            if (writePermissionGranted || readPermissionGranted) {
                createFolderAndSaveFile()
            } else {
                Log.e(ConstanstsApp.tag, "Permission denied")
            }
        }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {

        createRoomDatabase()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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


//        WindowCompat.setDecorFitsSystemWindows(window, false)

      /*  WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = true
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
        }*/



        sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            requestStoragePermissions()
        } else {
            createFolderAndSaveFile() // Directly create folder and save file for API 30+
        }

// Check if the success message is passed as an extra
        if (intent.hasExtra("successMessage")) {
            val successMessage = intent.getStringExtra("successMessage")
            if (!successMessage.isNullOrEmpty()) {
                // Display the success message as a toast
               // Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
                            showCustomToast1(successMessage)

            }
        }
//        val successMessage = intent.getStringExtra("successMessage")
//        if (successMessage != null) {
//            // Display the success message on the main page
//            showCustomToast1(successMessage)
//        }
// Check if the user has already accepted the privacy policy
        if (!hasUserAcceptedPrivacyPolicy()) {
             showPrivacyPolicyDialog()
        } else {
            // User has already accepted, proceed with the app's main functionality
            // Add code to start your app's main functionality
        }
        committeeCard = findViewById(R.id.cardcommette)
        viewAllTextView = findViewById(R.id.viewall)
        privacyCard = findViewById(R.id.cardprivacyMainactivity)
        journalCard = findViewById(R.id.cardjournal_mainActivity)
        eventCard = findViewById(R.id.cardevent)
        caseCard = findViewById(R.id.cardcasemonth)
        notificationTitleView = findViewById(R.id.notificationTitle_main)
        notificationDescriptionView = findViewById(R.id.notificationDescription_des)
        loginBtn = findViewById(R.id.healthicons)

        LatestNotificationData = ArrayList()
        sharedPreferencesManager = SharedPreferencesManager(applicationContext)
        sharedPreferencesManager1 = SessionManager1(this)
        sessionManager = SessionManagerSingleton.getSessionManager(this)

        SharepreferenceAppkill = sharepreferenceAppkill(this)
        isLoggedIn = sharedPreferencesManager1.isLoggedIn()

        setImageLogin()
//        val appUpdateChecker = AppUpdateChecker(this)
//        appUpdateChecker.checkForUpdate()
//
        val userDetails = SharedPreferencesActivity.getUserDetails(this)
        Log.d(ConstanstsApp.tag, "userdetails in MainActivity" + userDetails)
        Log.d(ConstanstsApp.tag, "Main On Create")
        appDatabase = AppDatabase.getDatabase(this)

        val loginType = intent.getStringExtra("loginType")
      val  topicId = intent.getIntExtra("topicId", -1)


        Log.d(ConstanstsApp.tag, "loginType=>" + loginType)
        setOpenActivity(loginType,topicId)
        checkForAppUpdates()

        sharedpreferences = getSharedPreferences(Login_PREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences != null) {
            val username = sharedpreferences!!.getInt("Login", 0)
            val Checked = sharedpreferences!!.getBoolean("Checked", true)

            Log.d(ConstanstsApp.tag, "username=> " + username)
            Log.d(ConstanstsApp.tag, "Checked=> " + Checked)
            // Rest of your code...
        } else {
            // Handle the case where sharedpreferences is null.
        }

        FirebaseApp.initializeApp(this)
        try {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        var token = task.exception?.message
                        Log.d("mytag", task.exception.toString())
                        Log.d("mytag", "token in main =>" + token)
                    } else {
                        token = task.result
                        Log.d("mytag", "token in main =>" + token)
                        sendToken(token.toString())
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val accesToken = org.bombayneurosciences.bna_2023.AccessToken()
            var authToken = accesToken.accessToken
            Acesstoken = authToken
            Log.d("authToken", "token in main =>" + authToken)
        }
        committeeCard.setOnClickListener {
            Log.d(ConstanstsApp.tag, "getUserStatus" + sharedPreferencesManager1.getUserStatus())
            val (isLogin, isKeep) = sharedPreferencesManager1.getUserStatus()
            val intent = Intent(this, CommetteeActivity::class.java)

            intent.putExtra("Login", isLogin)
            intent.putExtra("Keep_logged", isKeep)
            startActivity(intent)
//
        }
        privacyCard.setOnClickListener {
            Log.d(ConstanstsApp.tag, "getUserStatus" + sharedPreferencesManager1.getUserStatus())
            val (isLogin, isKeep) = sharedPreferencesManager1.getUserStatus()
            val intent = Intent(this, PrivacyPolicyActivity::class.java)

            intent.putExtra("Login", isLogin)
            intent.putExtra("Keep_logged", isKeep)

            startActivity(intent)
        }
        journalCard.setOnClickListener {
            if (isOnline() || sharedPreferencesManager1.isLoggedIn()) {
                val (isLoggedIn, shouldRememberMe) = sharedPreferencesManager1.getUserStatus()
                Log.d(ConstanstsApp.tag, "isLoggedIn=>$isLoggedIn")
                Log.d(ConstanstsApp.tag, "shouldRememberMe=>$shouldRememberMe")
                Log.d(ConstanstsApp.tag, "Activity Back =>" + sharedPreferencesManager1.getBackState())
                if (sessionManager.getBackState() == true) {

                 /*   val intent = Intent(this, JournalActivity::class.java)
                    startActivity(intent)*/
                    val intent = Intent(this, StartActivity::class.java)
                    startActivity(intent)

                } else {
                    if (SharepreferenceAppkill == null || SharepreferenceAppkill.getAppKill().equals("AppKill")) {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        if (isLoggedIn == true && shouldRememberMe == true) {
                            /*val intent = Intent(this, JournalActivity::class.java)
                            startActivity(intent)*/
                            val intent = Intent(this, StartActivity::class.java)
                            startActivity(intent)
                        } else if (isLoggedIn == true && shouldRememberMe == false) {
                           /* val intent = Intent(this, JournalActivity::class.java)
                            startActivity(intent)*/
                            val intent = Intent(this, StartActivity::class.java)
                            startActivity(intent)
                        } else if (isLoggedIn == false && shouldRememberMe == false) {
//                            startLoginActivity("journal")
                            startLoginActivity("Journal")
                            sessionManager.setBottomMenuBar("Journal")
                        }
                    }
                }
            } else {
                // Show "No Internet" alert
                showNoInternetAlert("Please connect to the internet")
            }
        }
        eventCard.setOnClickListener {
            val intent = Intent(this, EventActivity::class.java)
            startActivity(intent)

        }
        caseCard.setOnClickListener {
            if (isOnline() || sharedPreferencesManager1.isLoggedIn()) {
                // User is either online or logged in, proceed with the activity
                val (isLoggedIn, shouldRememberMe) = sharedPreferencesManager1.getUserStatus()
                Log.d(ConstanstsApp.tag, "isLoggedIn=>$isLoggedIn")
                Log.d(ConstanstsApp.tag, "shouldRememberMe=>$shouldRememberMe")
                if (sessionManager.getBackState() == true) {
                    val intent = Intent(this, CaseOfMonthActivity::class.java)
                    startActivity(intent)
//                    Toast.makeText(this, "cash5", Toast.LENGTH_SHORT).show()
                } else {

                    if (SharepreferenceAppkill == null || SharepreferenceAppkill.getAppKill().equals("AppKill")) {


                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
//                        Toast.makeText(this, "cash4", Toast.LENGTH_SHORT).show()
                    } else {
                        if (isLoggedIn == true && shouldRememberMe == true) {
                            val intent = Intent(this, CaseOfMonthActivity::class.java)
                            startActivity(intent)
//                            Toast.makeText(this, "cash3", Toast.LENGTH_SHORT).show()
                        } else if (isLoggedIn == true && shouldRememberMe == false) {
                            val intent = Intent(this, CaseOfMonthActivity::class.java)
                            startActivity(intent)
//                            Toast.makeText(this, "cash2", Toast.LENGTH_SHORT).show()
                        } else if (isLoggedIn == false && shouldRememberMe == false) {
                            startLoginActivity("CaseOfMonth")
                            sessionManager.setBottomMenuBar("CaseOfMonth")
//                            Toast.makeText(this, "cash1", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                // User is offline and not logged in, show "No Internet" alert
                showNoInternetAlert("Please connect to the internet")
            }
        }

        loginBtn.setOnClickListener {

            val (islogin, iskeep) = sessionManager.getUserStatus()
            if (isOnline()) {
                // Check if the user is logged in
                if (islogin == true) {
                    // If logged in, show custom dialog
                    showCustomDialog()
                } else {
                    // If not logged in, start the login activity
                    startLoginActivity("LOGIN_ACTIVITY")
                }
            } else {
                showNoInternetAlert("Please connect to the internet")
            }
        }

        viewAllTextView.setOnClickListener {
            val isOnline = isOnline()
            Log.d("Click", "View All Clicked")
            if (isOnline) {
                loadNotifications()
            } else {
                displayOfflineNotifications()
            }
        }
        val isOnline = isOnline()
        Log.d("Click", "View All Clicked")
        if (isOnline) {
        } else {
            updateLatestNotificationViews()
        }
        loadNotifications1()



        val notificationData = intent.getStringExtra("notification")

        if (notificationData==null){
//            Log.e("notificationData",""+notificationData)

        }else{
            if (notificationData.equals("Journal")){

                journalCard.callOnClick()
                Log.e("notificationData",""+notificationData)
            }else if (notificationData.equals("CaseMonth")){

                caseCard.callOnClick()
            }else if (notificationData.equals("Scientifc")){

                eventCard.callOnClick()
            }


        }

    }

    private fun createRoomDatabase() {
        val database = BNA_RoomDatabase.getDatabase(this)
        val Journal_DAO: Journal_DAO =database.Journal_DAO()
        val repository = BNA_RD_Repository(Journal_DAO,database)
        viewModel1 = ViewModelProvider(this, BNA_RD_ViewModelFactory(repository)).get(
            BNA_RD_ViewModel::class.java)
    }

    private fun showCustomToast1(message: String) {
        // Inflate the custom toast layout
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.login_sucess_toast, null)

        // Set the message in the custom toast layout
        val customToastMessage = layout.findViewById<TextView>(R.id.CustomToastnotificationTitle)
        val imgCancel: ImageView = layout.findViewById(R.id.imgCancel)

        customToastMessage.text = message

        // Create and show the custom toast with bottom gravity
        val toast = Toast(applicationContext)
        toast.setGravity(Gravity.BOTTOM, 0, 32)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout

        // Set click listener on imgCancel to dismiss the toast
        imgCancel.setOnClickListener {
            toast.cancel()
        }

        toast.show()
        // Delay dismissing the toast after a certain duration
        Handler().postDelayed({
            toast.cancel()
        }, 1000)
    }

    private fun hasUserAcceptedPrivacyPolicy(): Boolean {
        // Retrieve the user's acceptance status from SharedPreferences
        return sharedPreferences.getBoolean(KEY_ACCEPTED_PRIVACY_POLICY, false)
    }

    private fun setUserAcceptedPrivacyPolicy() {
        // Save the user's acceptance status in SharedPreferences
        sharedPreferences.edit().putBoolean(KEY_ACCEPTED_PRIVACY_POLICY, true).apply()
    }

    companion object {
        private const val KEY_ACCEPTED_PRIVACY_POLICY = "accepted_privacy_policy"
    }

    @SuppressLint("MissingInflatedId")
    private var isPrivacyPolicyAccepted = false

    private fun showPrivacyPolicyDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_privacy_policy, null)

        // Initialize your dialog views
        val textPrivacyPolicy: TextView = view.findViewById(R.id.textprivacypolicy)
        val btnAcceptContinue: TextView = view.findViewById(R.id.acceptButton)

        // Set privacy policy text

        // Set click listener for the Accept & Continue button
        btnAcceptContinue.setOnClickListener {
            // Set the flag to true to indicate that the user has accepted
            setUserAcceptedPrivacyPolicy()
            Log.d("mytag","isPrivacyPolicyAccepted=>true"+isPrivacyPolicyAccepted)
            // Close the dialog
            privacyPolicyDialog?.dismiss()
            // Add code to start your app's main functionality
        }

        builder.setView(view)
        privacyPolicyDialog = builder.create()

        // Disable outside touch dismissal
        privacyPolicyDialog?.setCancelable(false)


        // Set top and bottom margins for the dialog window
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(privacyPolicyDialog?.window?.attributes)

        // Set top margin in pixels
        val topMarginInPixels = resources.getDimensionPixelSize(R.dimen.margin_80dp)
        layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        layoutParams.y = topMarginInPixels

        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        privacyPolicyDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        privacyPolicyDialog?.window?.attributes = layoutParams

        // Set a listener for the back button to handle the back press
        privacyPolicyDialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == android.view.KeyEvent.KEYCODE_BACK && event.action == android.view.KeyEvent.ACTION_UP) {
                // Handle back button press here
                // For example, show a toast indicating that the user needs to accept the policy
                return@setOnKeyListener true
            } else {
                false
            }
        }


        privacyPolicyDialog?.show()
    }




    //    private  val installStateUpdatedListener = InstallStateUpdatedListener {
//        state ->
//        if (state.installStatus() == InstallStatus.DOWNLOADED){
//            Toast.makeText(applicationContext,"download sucessful .Restarting app ",Toast.LENGTH_SHORT).show()
//            lifecycleScope.launch {
//                delay(5.seconds)
//                appUpdateManager.completeUpdate()
//            }
//        }
//    }

    private fun getAppVersion(): Int? {
        try {
            val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            return pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }


    private fun showUpdateDialog(
        currentVersion: String,
        latestVersion: String,
        appUpdateManager: AppUpdateManager,
        appUpdateInfo: AppUpdateInfo
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update Available")
        builder.setMessage(
            "Your current version: $currentVersion\n" +
                    "Latest version: $latestVersion\n\n" +
                    "What's new:\n" +
                    "- Bug fixes and performance improvements\n" +
//                    "- New features added\n\n" +
                    "Please update to continue."
        )
        builder.setCancelable(false)

        builder.setPositiveButton("Update Now") { _, _ ->
            try {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        builder.setNegativeButton("Later") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }


    private fun checkForAppUpdates(){
        val appUpdateManager = AppUpdateManagerFactory.create(this)

// Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {


                /*// Request the update.
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // an activity result launcher registered via registerForActivityResult
                    activityResultLauncher,
                    // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                    // flexible updates.
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())*/


                val currentVersion = getAppVersion()
                val latestVersionCode = appUpdateInfo.availableVersionCode().toInt() // Play Store versionCode

                if (currentVersion != null) {
                    if (currentVersion < latestVersionCode) {
                        // Show custom dialog only if current version is older
                        showUpdateDialog(currentVersion.toString(), latestVersionCode.toString(), appUpdateManager, appUpdateInfo)
                    }
                }

            }
        }

        appUpdateInfoTask.addOnFailureListener {exception->

            Log.d(ConstanstsApp.tag," appUpdateInfoTask.addOnFailureListener =>"+exception.message)
        }

    }






    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
        // handle callback
        if (result.resultCode != RESULT_OK) {
            Log.d("mytag","Update flow failed! Result code: " + result.resultCode);
            // If the update is canceled or fails,
            // you can request to start the update again.
        }
    }



    private fun setImageLogin() {
        runOnUiThread {
            val (isLogin, isKeep) = sharedPreferencesManager1.getUserStatus()

            if (isLogin) {
                // If logged in, check online status
                if (isOnline()) {
                    loginBtn.setImageResource(R.drawable.healthiconsuserprofile)
                } else {
                    // If offline and logged in, show healthicon
                    loginBtn.setImageResource(R.drawable.healthiconsuserprofile)
                }
            } else {
                // If not logged in, show userprofile icon
                loginBtn.setImageResource(R.drawable.userprofile)
            }
        }
    }
    override fun onStart() {
        super.onStart()

        // Check for updates when your activity starts
        //  checkForUpdates()
    }

//    private fun checkForUpdates() {
//        val packageInfo = try {
//            packageManager.getPackageInfo(packageName, 0)
//        } catch (e: PackageManager.NameNotFoundException) {
//            null
//        }
//
//        val installedVersionCode = packageInfo?.versionCode ?: 0
//
//        val playStoreVersionCode = 45
//        Log.d("mytag", "Installed Version Code: $installedVersionCode")
//        Log.d("mytag", "Play Store Version Code: $playStoreVersionCode")
//
//        if (installedVersionCode < playStoreVersionCode) {
//            // An update is available
//            showUpdateDialog()
//        }
//    }
//
//    private fun showUpdateDialog() {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Update Available")
//            .setMessage("A new version of the app is available. Please update to the latest version.")
//            .setPositiveButton("Update") { _, _ ->
//                // Open the Play Store for the app update
//                openPlayStoreForUpdate()
//            }
//            .setNegativeButton("Cancel") { _, _ ->
//                // Handle cancel button click
//            }
//
//        val alertDialog = builder.create()
//        alertDialog.show()
//
//        // Customize button text color
//        val positiveButton: Button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
//        val negativeButton: Button = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
//
//        // Set the desired color for the positive button (Update)
//        val updateText = "Update"
//        val spannableUpdate = SpannableString(updateText)
//        spannableUpdate.setSpan(ForegroundColorSpan(resources.getColor(R.color.white)), 0, updateText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        positiveButton.text = spannableUpdate
//
//        // Set the desired color for the negative button (Cancel)
//        val cancelText = "Cancel"
//        val spannableCancel = SpannableString(cancelText)
//        spannableCancel.setSpan(ForegroundColorSpan(resources.getColor(R.color.white)), 0, cancelText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        negativeButton.text = spannableCancel
//    }
//
//
//
//    private fun openPlayStoreForUpdate() {
//        try {
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        } catch (e: Exception) {
//            // Handle exception (e.g., Play Store not installed)
//        }
//    }
//
//
//


    private fun showCustomDialog() {
        val userDetails = SharedPreferencesActivity.getUserDetails(this)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_xml)
        // Find views in the custom dialog layout
        val userDetailsTextView: TextView = dialog.findViewById(R.id.textView_person_name)
        val userDetailsEmail: TextView = dialog.findViewById(R.id.textViewEmail)
        val logout: TextView = dialog.findViewById(R.id.textViewlogout)
        // Set user details in a single line
        userDetailsTextView.text =
            "${userDetails.title} ${userDetails.fname} ${userDetails.mname} ${userDetails.lname}"
        userDetailsEmail.text = userDetails.email
        // Set click listener for the Logout button
        logout.setOnClickListener {
            // Handle logout actions here
            showLogoutConfirmationDialog()
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }
    private fun startLoginActivity(loginType: String) {
        if (isOnline()) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("loginType", loginType)
            //startActivityForResult(intent, LOGIN_REQUEST_CODE)
            startActivity(intent)
//            Toast.makeText(this, "login type $loginType", Toast.LENGTH_SHORT).show()
        } else {
            //showNoInternetAlert()
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
    private fun showLogoutConfirmationDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.logout_confirmation_dialog)
        // Find views in the custom dialog layout
        val confirmationText: TextView = dialog.findViewById(R.id.textViewLogoutConfirmation)
        val buttonYes: Button = dialog.findViewById(R.id.buttonYes)
        val buttonNo: Button = dialog.findViewById(R.id.buttonNo)
        // Set confirmation text
        confirmationText.text = "Are you sure want to logout?"
        // Set click listener for the Yes button
        buttonYes.setOnClickListener {
            isLoggedIn = false
            loginBtn.setImageResource(R.drawable.userprofile)
            sharedPreferencesManager1.clearSession()
            //    sharedPreferencesManager.clearPreferences(this)
//            Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show()
//            dialog.dismiss()
//            
            showCustomToast("Logout Successful")
            dialog.dismiss()
            // Call the logout function or navigate to the logout screen
        }
        // Set click listener for the No button
        buttonNo.setOnClickListener {
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

    private fun showCustomToast(message: String) {
        // Inflate the custom toast layout
        val inflater = layoutInflater
        val layout: View = inflater.inflate(R.layout.custom_toast_layout, null)

        // Find the TextView and Cancel ImageView in your custom layout
        val notificationTitle: TextView = layout.findViewById(R.id.CustomToastnotificationTitle)
        val imgCancel: ImageView = layout.findViewById(R.id.imgCancel)

        // Set the message text
        notificationTitle.text = message

        // Create a PopupWindow
        val popupWindow = PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        // Set an OnClickListener for the cancel button
        imgCancel.setOnClickListener {
            // Dismiss the PopupWindow
            popupWindow.dismiss()
        }

        // Show the PopupWindow
        popupWindow.showAtLocation(layout, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 32)
        // Dismiss the popup after 2 milliseconds
        Handler().postDelayed({
            popupWindow.dismiss()
        }, 1000)
    }


    private fun sendToken(token: String) {
        val sharedPreferencesManager = SharedPreferencesManagerToken(applicationContext)
        val deviceToken = token
        Log.d(ConstanstsApp.tag, "deviceToken=>" + deviceToken)
        var udidNo =
            Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        val osName = "Android"
        val location = "Mumbai"
        val platform = "Android"
        Log.d(ConstanstsApp.tag, "udidNo" + udidNo)
        val apiToken = RetrofitInstance.apiToken
        val call = apiToken.addDeviceToken(token!!, udidNo, osName, location, platform)
        // Enqueue the call to make it asynchronous
        call.enqueue(object : Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                when (response.body()!!.success) {
                    0 -> {
                        val token = response.body()!!.message

                        Log.d(ConstanstsApp.tag, "response of token send 0 =>" + token)
                    }
                    1 -> {
                        val token = response.body()!!.message

                        Log.d(ConstanstsApp.tag, "response of token send 1 =>" + token)
                    }
                }
            }
            override fun onFailure(call: Call<Token>, t: Throwable) {
                // Handle failure
            }
        })
    }




    @SuppressLint("ServiceCast")
    private fun isOnline(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }

    private fun loadNotifications() {
        val apiServiceNotification = RetrofitInstance.apiServiceNotification
        val call = apiServiceNotification.getNotifications()
        call.enqueue(object : Callback<NotificationDataClass> {
            override fun onResponse(
                call: Call<NotificationDataClass>,
                response: Response<NotificationDataClass>
            ) {
                if (response.isSuccessful) {
                    val notificationData = response.body()
                    if (notificationData != null) {
                        val notificationsList = notificationData.data
                        if (notificationsList.isNotEmpty()) {
                            latestNotification = notificationsList[notificationsList.size - 1]
                        }
                        updateLatestNotificationViews()
                        for (notificationData in notificationsList) {
                            val notificationEntity = NotificationEntity(
                                title = notificationData.title,
                                content = notificationData.content,
                                attachment = notificationData.attachment,
                                fileName = "sample.pdf"
                            )
                            storeNotificationInDatabase(notificationEntity)
                        }
                        val sharedPreferencesManager = SharedPreferencesManager(applicationContext)
                        sharedPreferencesManager.saveNotificationData(notificationData)

                        if (notificationsList.isNotEmpty()) {
                            val latestNotificationTitle = notificationsList.last().title
                            val latestNotificationContent = notificationsList.last().content
                            notificationTitleView.text = latestNotificationTitle
                            notificationDescriptionView.text = latestNotificationContent
                            updateLatestNotificationViews()
                            val intent =
                                Intent(applicationContext, NotificationActivity::class.java)
                            intent.putParcelableArrayListExtra(
                                "notifications",
                                ArrayList(notificationsList)
                            )
                            startActivity(intent)
                        }
                    } else {
                        Log.e(
                            ConstanstsApp.tag,
                            "API request failed with code: ${response.code()}"
                        )
                    }
                }
            }
            override fun onFailure(call: Call<NotificationDataClass>, t: Throwable) {
                Log.e(ConstanstsApp.tag, "API request failed: ${t.message}", t)
            }
        })
    }
    private fun loadNotifications1() {
        val apiServiceNotification = RetrofitInstance.apiServiceNotification
        val call = apiServiceNotification.getNotifications()
        call.enqueue(object : Callback<NotificationDataClass> {
            override fun onResponse(
                call: Call<NotificationDataClass>,
                response: Response<NotificationDataClass>
            ) {
                if (response.isSuccessful) {
                    val notificationData = response.body()
                    if (notificationData != null) {
                        val notificationsList = notificationData.data
                        if (notificationsList.isNotEmpty()) {
                            latestNotification = notificationsList[notificationsList.size - 1]
                        }
                        for (notificationData in notificationsList) {
                            val notificationEntity = NotificationEntity(
                                title = notificationData.title,
                                content = notificationData.content,
                                attachment = notificationData.attachment,
                                fileName = "sample.pdf"
                            )
                            storeNotificationInDatabase(notificationEntity)
                        }
                        val sharedPreferencesManager = SharedPreferencesManager(applicationContext)
                        sharedPreferencesManager.saveNotificationData(notificationData)
                        if (notificationsList.isNotEmpty()) {
                            val latestNotificationTitle = notificationsList.last().title
                            val latestNotificationContent = notificationsList.last().content
                            notificationTitleView.text = latestNotificationTitle
                            notificationDescriptionView.text = latestNotificationContent
                            updateLatestNotificationViews()
                        }
                    } else {
                        Log.e(
                            ConstanstsApp.tag,
                            "API request failed with code: ${response.code()}"
                        )
                    }
                }
            }
            override fun onFailure(call: Call<NotificationDataClass>, t: Throwable) {
                Log.e(ConstanstsApp.tag, "API request failed: ${t.message}", t)
            }
        })
    }
    private fun updateLatestNotificationViews() {
        val sharedPreferencesManager = SharedPreferencesManager(applicationContext)
        val notification = sharedPreferencesManager.getNotificationData()
        for (i in 0 until notification!!.data.size) {
            val data = notification.data[i]

            val attachment = data.attachment
            val content = data.content
            val created_at = data.created_at
            val id = data.id
            val isactive = data.isactive
            val link_to = data.link_to
            val modified_at = data.modified_at
            val s_date = data.s_date
            val s_time = data.s_time
            val sent_to_android = data.sent_to_android
            val sent_to_ios = data.sent_to_ios
            val title = data.title
            val total = data.total
            var isRead = data.isRead
            val timestamp = data.timestamp

            val notificationData = Data(
                attachment,
                content,
                created_at,
                id,
                isactive,
                link_to,
                modified_at,
                s_date,
                s_time,
                sent_to_android,
                sent_to_ios,
                title,
                total,
                isRead,
                timestamp
            )
            LatestNotificationData!!.add(notificationData)
        }
        notificationTitleView.text = LatestNotificationData!!.last().title
        notificationDescriptionView.text = LatestNotificationData!!.last().content
    }
    private fun storeNotificationInDatabase(notificationEntity: NotificationEntity) {
        val database = AppDatabase.getDatabase(this)
        GlobalScope.launch(Dispatchers.IO) {
            database.notificationDao().insertFile(notificationEntity)
            getDataFromRoomDatabase()
        }
    }
    private fun getDataFromRoomDatabase() {
        GlobalScope.launch(Dispatchers.IO) {
            val notificationEntities = appDatabase.notificationDao().getAllDownloadedFiles()
            if (notificationEntities.isNotEmpty()) {
                for (notificationEntity in notificationEntities) {

                    val title = notificationEntity.title
                    val content = notificationEntity.content
                    val attachment = notificationEntity.attachment
                    val fileName = notificationEntity.fileName
                }
            } else {
                Log.d(ConstanstsApp.tag, "No data found in Room database.")
            }
        }
    }
    private fun displayOfflineNotifications() {
        val sharedPreferencesManager = SharedPreferencesManager(applicationContext)
        val offlineNotifications = sharedPreferencesManager.getNotificationData()
        if (offlineNotifications != null) {
            val notificationsList = offlineNotifications.data
            val unreadNotifications = notificationsList.filter { !it.isRead }
            val intent = Intent(this, NotificationActivity::class.java)
            intent.putParcelableArrayListExtra("notifications", ArrayList(unreadNotifications))
            startActivity(intent)
        } else {
            // You can show a message to the user indicating there are no offline notifications.
        }
    }
    override fun onBackPressed() {
        // Check if the privacy policy dialog is showing
        if (privacyPolicyDialog?.isShowing == true) {
            // Optionally, show a toast to inform the user to accept the policy
            // Toast.makeText(this, "Please accept the privacy policy", Toast.LENGTH_SHORT).show()
        } else if (supportFragmentManager.backStackEntryCount == 0) {
            // If the back stack is empty, show exit confirmation dialog
            showExitConfirmationDialog()
        } else {
            // Otherwise, proceed with the default back press behavior
            super.onBackPressed()
        }
    }

    private fun showExitConfirmationDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.exit_app)
        // Find views in the custom dialog layout
        val confirmationText: TextView = dialog.findViewById(R.id.textViewLogoutConfirmation)
        val buttonYes: Button = dialog.findViewById(R.id.buttonYes)
        val buttonNo: Button = dialog.findViewById(R.id.buttonNo)
        // Set confirmation text
        confirmationText.text = "Do you want to exit the app?"
        buttonYes.setOnClickListener {
            finishAffinity()
            exitProcess(0)
        }
        buttonNo.setOnClickListener {
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
    override fun onStop() {
        super.onStop()
        val (isLogin, isKeep) = sessionManager.getUserStatus()
        if (isKeep == false) {
            SharepreferenceAppkill.clearAppKill()
        } else {
        }
        Log.d(ConstanstsApp.tag, "getAppKill=>" + SharepreferenceAppkill.getAppKill())
    }
    override fun onDestroy() {
        super.onDestroy()
//        if (updateType == AppUpdateType.FLEXIBLE){
//            appUpdateManager.unregisterListener(installStateUpdatedListener)
//        }
//        Toast.makeText(this, "cash100", Toast.LENGTH_SHORT).show()

        //TODO MODI
       /* sharedPreferencesManager1.clearSession()
        when (sharedPreferencesManager1.getKeepMeLoggedInState()) {
            true -> {
            }
            false -> {
                val editor = sharedpreferences!!.edit()
                editor.remove("Login")
                editor.remove("Checked")
                editor.apply()
            }
        }*/
    }
    private fun setOpenActivity(loginType: String?, topicId: Int) {
        Log.d(ConstanstsApp.tag, "loginType in Main Activity" + loginType)
        Log.e(ConstanstsApp.tag, "brocodee" + loginType)
        Log.e(ConstanstsApp.tag, "brocodee" + topicId)
        when (loginType) {
            "CaseOfMonth" -> {
                startActivity(Intent(this, CaseOfMonthActivity::class.java))
            }
            "Journal" -> {
//                startActivity(Intent(this, JournalActivity::class.java))
                startActivity(Intent(this, StartActivity::class.java))
            }
            "Voting" ->{
                val intent=Intent(this, VotingActivity::class.java)
                intent.putExtra("topicId",topicId)
                startActivity(intent)


            }
        }
    }
    override fun onRestart() {
        super.onRestart()
        Log.d(ConstanstsApp.tag,"on Restart")
        setImageLogin()
    }
    override fun onResume() {
        super.onResume()
        Log.d(ConstanstsApp.tag,"on Resume")
        setImageLogin()

    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Handle landscape orientation
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Handle portrait orientation
        }
    }

    private fun requestStoragePermissions() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                createFolderAndSaveFile()
            }
            else -> {
                requestPermissionLauncher.launch(arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ))
            }
        }
    }

    private fun createFolderAndSaveFile() {
        val folderName = "BNA_App_PDF"
        val appFolder: File = File(getExternalFilesDir(null), folderName)

        if (!appFolder.exists()) {
            val folderCreated = appFolder.mkdirs()
            if (!folderCreated) {
                Log.e(ConstanstsApp.tag, "Failed to create folder: $appFolder")
                return
            } else {
                Log.d(ConstanstsApp.tag, "Folder created: $appFolder")
            }
        } else {
            Log.d(ConstanstsApp.tag, "Folder already exists: $appFolder")
        }

        // Save the file
        val fileName = "example.txt"
        val fileContent = "Hello, this is a sample file content."

        val file = File(appFolder, fileName)
        try {
            FileOutputStream(file).use { fos ->
                fos.write(fileContent.toByteArray())
                fos.flush()
                Log.d(ConstanstsApp.tag, "File saved successfully: $file")
            }
        } catch (e: IOException) {
            Log.e(ConstanstsApp.tag, "Error saving file: ${e.message}", e)
        }
    }

}