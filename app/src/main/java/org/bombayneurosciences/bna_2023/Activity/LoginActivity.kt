package org.bombayneurosciences.bna_2023.Activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

import org.bombayneurosciences.bna_2023.CallBack.RetrofitClient
import org.bombayneurosciences.bna_2023.Data.SharedPreferencesActivity
import org.bombayneurosciences.bna_2023.Data.SharedPreferencesManager
import org.bombayneurosciences.bna_2023.Model.ApiResponse
import org.bombayneurosciences.bna_2023.Model.UserDetails.UserDetails
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.databinding.ActivityLoginBinding
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import org.bombayneurosciences.bna_2023.utils.SessionManagerSingleton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class LoginActivity : AppCompatActivity(), TextWatcher, View.OnClickListener {

    private lateinit var membershipNoEditText: EditText
    private lateinit var mobEmailEditText: EditText
    private lateinit var loginButton: TextView
    private lateinit var keepMeLoggedInCheckBox: CheckBox

    private var isFirstLaunch = true
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var sharedPreferencesManager1: SessionManager1

    var sharedpreferences: SharedPreferences? = null
    val Login_PREFERENCES = "Login_Prefs"

    var loginType=""
    var topicId=""
    var name=""


    lateinit var sessionManager:SessionManager1
    lateinit var binding:ActivityLoginBinding

    companion object {
        const val LOGIN_SUCCESS_RESULT_CODE = 1
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


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


        //setContentView(R.layout.activity_login)

        sessionManager = SessionManagerSingleton.getSessionManager(this)
        sharedPreferencesManager = SharedPreferencesManager(applicationContext)
        sharedpreferences = getSharedPreferences(Login_PREFERENCES, Context.MODE_PRIVATE);
        membershipNoEditText = findViewById(R.id.editTextMemberNo)
        mobEmailEditText = findViewById(R.id.editTextEmailOrPhone)
        loginButton = findViewById(R.id.loginButton)
        keepMeLoggedInCheckBox = findViewById(R.id.keepMeLoggedInCheckBox)

        keepMeLoggedInCheckBox.setOnClickListener(this)


        val backButton = findViewById<ImageView>(R.id.backbutton)

        sharedPreferencesManager1 = SessionManager1(this)

        val inputFilter = InputFilter { source, start, end, dest, dstart, dend ->
            if (source != null && source.length > 0 && source[0] == ' ' && dstart == 0) {
                // Prevent space at the beginning
                ""
            } else {
                null
            }
        }
// After successful login
        val intent = intent
         topicId = intent.getIntExtra("topicId", -1).toString()
         name = intent.getStringExtra("name").toString()

      Log.d("mytag","topicId in login sucess of voting"+topicId)
        Log.d("mytag","name in login sucess of voting"+name)


        // Set the cursor color for Member Number EditText
        val cursorColorMemberNo = ContextCompat.getColor(this, R.color.black)


        setEditTextCursorColor(binding.editTextMemberNo, cursorColorMemberNo)

        // Set the cursor color for Email or Phone EditText
        val cursorColorEmailOrPhone = ContextCompat.getColor(this, R.color.black)
        setEditTextCursorColor( binding.editTextEmailOrPhone, cursorColorEmailOrPhone)


        membershipNoEditText.filters = arrayOf(inputFilter)
        mobEmailEditText.filters = arrayOf(inputFilter)


        loginType = intent.getStringExtra("loginType") ?: ""

        Log.d(ConstanstsApp.tag, "loginType on Create=>$loginType")


        // Set a touch listener for the root layout to dismiss the keyboard when tapped outside EditText fields
        val rootView = findViewById<RelativeLayout>(R.id.rootlayout)
        rootView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
            }
            return@setOnTouchListener false
        }

        // Set a click listener for the back button
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        membershipNoEditText.addTextChangedListener(this)


        loginButton.setOnClickListener {




            // Check for internet connectivity
            if (isOnline()) {
                // Device is online, set the click listener for the login button

                Log.d("myag","loginType in login "+loginType)

                Log.d(ConstanstsApp.tag, "loginType on Button clicked=>$loginType")
                val membershipNo = membershipNoEditText.text.toString().trim()
                val mobEmail = mobEmailEditText.text.toString().trim()

                if (membershipNo.isNotEmpty() && mobEmail.isNotEmpty()) {
                    Log.d(
                        ConstanstsApp.tag,
                        "User Details: Membership No=$membershipNo, Email/Phone=$mobEmail"
                    )

                    // Check if membershipNo contains lowercase letters
                    if (membershipNo.any { it.isLowerCase() }) {
                        // Display a Toast message indicating that membership numbers should be in uppercase
                        showAlert3("Please enter correct Credentials",)

                    } else {
                        // Perform login only if membershipNo is in uppercase
                        performLogin(membershipNo, mobEmail, loginType)
                    }
                }
                else if (membershipNo.isEmpty() && mobEmail.isEmpty()) {
                    // Both membership number and email/phone are empty, display a message
                    showAlert2("Please enter Credentials",)

//                    Toast.makeText(
//                        this,
//                        "Please enter Membership No and Email/Phone",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
                else if (membershipNo.isEmpty())
                {
                    showAlert("Please enter Membership No",)

                } else
                {
                    showAlert1("Please enter Email/Phone",)

//                    // Email/phone is empty, display a message
//                    Toast.makeText(
//                        this,
//                        "Please enter Email/Phone",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }

            }
            else {
                // Device is offline, show the alert directly
                showNoInternetAlert("Please connect to the internet")
            }

        }


        // Set focus change listener for membershipNoEditText
        membershipNoEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Change the background color when membershipNoEditText is clicked
                membershipNoEditText.setBackgroundResource(R.drawable.editable_background)
                // Change text color and style
                membershipNoEditText.setTextColor(
                    ContextCompat.getColor(
                        this@LoginActivity,
                        R.color.black
                    )
                )
                membershipNoEditText.setTypeface(null, Typeface.NORMAL)
            } else {
                // Change the background color back to the original when focus is lost
                if (membershipNoEditText.text.isNotEmpty()) {
                    membershipNoEditText.setBackgroundResource(R.drawable.custom_edittext)
                } else {
                    membershipNoEditText.setBackgroundResource(R.drawable.login_bg)
                }
                // Reset text color and style
                membershipNoEditText.setTextColor(
                    ContextCompat.getColor(
                        this@LoginActivity,
                        R.color.your_red_color
                    )
                )
                membershipNoEditText.setTypeface(null, Typeface.BOLD)
            }
            checkFieldsAndSetButtonStyle()
        }

// Set focus change listener for mobEmailEditText
        mobEmailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Change the background color when mobEmailEditText is clicked
                mobEmailEditText.setBackgroundResource(R.drawable.editable_background)
                // Change text color and style
                mobEmailEditText.setTextColor(
                    ContextCompat.getColor(
                        this@LoginActivity,
                        R.color.black
                    )
                )
                mobEmailEditText.setTypeface(null, Typeface.NORMAL)
            } else {
                // Change the background color back to the original when focus is lost
                if (mobEmailEditText.text.isNotEmpty()) {
                    mobEmailEditText.setBackgroundResource(R.drawable.custom_edittext)
                } else {
                    mobEmailEditText.setBackgroundResource(R.drawable.login_bg)
                }
                // Reset text color and style
                mobEmailEditText.setTextColor(
                    ContextCompat.getColor(
                        this@LoginActivity,
                        R.color.your_red_color
                    )
                )
                mobEmailEditText.setTypeface(null, Typeface.BOLD)
            }
            checkFieldsAndSetButtonStyle()
        }
    }

    private fun showAlert3(message: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.login_alert)

        // Find views in the custom dialog layout
        val notificationTitle: TextView = dialog.findViewById(R.id.notificationTitle)
        val textViewLogoutConfirmation: TextView = dialog.findViewById(R.id.textViewLogoutConfirmation)
        val buttonYes: Button = dialog.findViewById(R.id.buttonYes)
        val buttonNo: Button = dialog.findViewById(R.id.buttonNo)

        // Set notification title and confirmation text
        notificationTitle.text = "Alert"
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

    private fun showAlert2(message: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.login_alert)

        // Find views in the custom dialog layout
        val notificationTitle: TextView = dialog.findViewById(R.id.notificationTitle)
        val textViewLogoutConfirmation: TextView = dialog.findViewById(R.id.textViewLogoutConfirmation)
        val buttonYes: Button = dialog.findViewById(R.id.buttonYes)
        val buttonNo: Button = dialog.findViewById(R.id.buttonNo)

        // Set notification title and confirmation text
        notificationTitle.text = "Alert"
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

    @SuppressLint("SuspiciousIndentation")
    private fun showAlert1(message: String) {
        val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.login_alert)

            // Find views in the custom dialog layout
            val notificationTitle: TextView = dialog.findViewById(R.id.notificationTitle)
            val textViewLogoutConfirmation: TextView = dialog.findViewById(R.id.textViewLogoutConfirmation)
            val buttonYes: Button = dialog.findViewById(R.id.buttonYes)
            val buttonNo: Button = dialog.findViewById(R.id.buttonNo)

            // Set notification title and confirmation text
            notificationTitle.text = "Alert"
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



    private fun showAlert(message: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.login_alert)

        // Find views in the custom dialog layout
        val notificationTitle: TextView = dialog.findViewById(R.id.notificationTitle)
        val textViewLogoutConfirmation: TextView = dialog.findViewById(R.id.textViewLogoutConfirmation)
        val buttonYes: Button = dialog.findViewById(R.id.buttonYes)
        val buttonNo: Button = dialog.findViewById(R.id.buttonNo)

        // Set notification title and confirmation text
        notificationTitle.text = "Alert"
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

    private fun checkFieldsAndSetButtonStyle() {
        val membershipNoNotEmpty = membershipNoEditText.text.isNotEmpty()
        val mobEmailNotEmpty = mobEmailEditText.text.isNotEmpty()

        if (membershipNoNotEmpty && mobEmailNotEmpty) {
            // Both fields are non-empty, set the background and text color of the login button
            loginButton.setBackgroundResource(R.drawable.button_background1)
            loginButton.setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.logintext))
        } else {
            // Either one or both fields are empty, set the original style of the login button
            loginButton.setBackgroundResource(R.drawable.button_background)
            loginButton.setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.white))
        }

        // Reset text style for membershipNoEditText
        if (!membershipNoNotEmpty) {
            membershipNoEditText.setTypeface(null, Typeface.NORMAL)

        }

        // Reset text style for mobEmailEditText
        if (!mobEmailNotEmpty) {
            mobEmailEditText.setTypeface(null, Typeface.NORMAL)
        }
    }

    @SuppressLint("SoonBlockedPrivateApi")
    private fun setEditTextCursorColor(editText: EditText, color: Int) {
        try {
            val field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            field.isAccessible = true
            val drawableResId = field.getInt(editText)

            val drawable = ContextCompat.getDrawable(this, drawableResId)
            drawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)

            val fieldEditor = TextView::class.java.getDeclaredField("mEditor")
            fieldEditor.isAccessible = true
            val editor = fieldEditor.get(editText)

            val fieldCursorDrawable = editor.javaClass.getDeclaredField("mCursorDrawable")
            fieldCursorDrawable.isAccessible = true
            val drawables = arrayOf(drawable, drawable)

            fieldCursorDrawable.set(editor, drawables)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
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



    private fun isOnline(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun performLogin(membershipNo: String, mobEmail: String, loginType: String) {
        val call = RetrofitClient.apiService.login(membershipNo, mobEmail)

        call.enqueue(object : Callback<ApiResponse> {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse?.success == 1) {
                        Log.d(ConstanstsApp.tag, "Login successful" + apiResponse.success)
                        sharedPreferencesManager1.setLogin(true,keepMeLoggedInCheckBox.isChecked)
                        sessionManager.setLogin(true,keepMeLoggedInCheckBox.isChecked)

//                        // Set the image based on the login status
//                        val loginSuccess = sharedPreferencesManager1.getUserStatus().first
//                        updateLoginBtnImage(loginSuccess)


                        val editor = sharedpreferences!!.edit()

                        editor.putInt("Login", apiResponse.success)
                        editor.putBoolean("Checked",keepMeLoggedInCheckBox.isChecked)

                        editor.commit()

                        // Set the result indicating a successful login
                        setResult(LOGIN_SUCCESS_RESULT_CODE)
                        val userData = apiResponse.data?.get(0)
                        userData?.delegate_id?.let {
                            SharedPreferencesActivity.saveDelegateId(this@LoginActivity, it)
                        }
                        userData?.let {
                            val userDetails = UserDetails(
                                it.title ?: "",
                                it.fname ?: "",
                                it.mname ?: "",
                                it.lname ?: "",
                                it.mobileno ?: "",
                                it.membershipno ?: "",
                                it.email ?: ""
                            )
                            // Save user details to SharedPreferences
                            SharedPreferencesActivity.saveUserDetails(
                                this@LoginActivity,
                                userDetails
                            )
                        }
//                        

// Show custom toast for successful login

                        // Navigate to MainActivity and pass success message as an extra
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("successMessage", "Login Successful")
                        intent.putExtra("loginType",loginType)
                        startActivity(intent)
                        finish()
//                        intent.putExtra("successMessage", "Login Successful")

                        sharedPreferencesManager1.setIntentFromLoginActivity("LOGIN_ACTIVITY")
                        SharedPreferencesActivity.saveUserInputs(
                            this@LoginActivity,
                            membershipNo,
                            mobEmail
                        )

                        Log.d(ConstanstsApp.tag,"getBottomMenuBar=>"+sharedPreferencesManager1.getBottomMenuBar())


                     /*   val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("loginType",  sharedPreferencesManager1.getBottomMenuBar())
                        startActivity(intent)*/
//                        intent.putExtra("loginSuccess", true)
//                        Log.d(ConstanstsApp.tag, "Login Success: true")
//
//                        intent.putExtra("keepMeLoggedIn", keepMeLoggedInCheckBox.isChecked)
//                        Log.e(ConstanstsApp.tag, "keepMeLoggedInCheckBox"+keepMeLoggedInCheckBox.isChecked )



                    } else {
                        showAlert3("Please enter correct Credentials",)
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                if (t is IOException) {
//                    Toast.makeText(
//                        this@LoginActivity,
//                        "Network Error",
//                        Toast.LENGTH_SHORT
//                    ).show()
                    showAlert4("Please connect to the internet",)

                } else {
//                    Toast.makeText(
//                        this@LoginActivity,
//                        "Login Failed. Please try again.",
//                        Toast.LENGTH_SHORT
//                    ).show()
                    Log.e("LoginActivity", "Unexpected error: ${t.localizedMessage}")
                }
            }
        })
    }

    // Uncomment the code for showing the custom toast
// Uncomment the code for showing the custom toast
    @SuppressLint("SuspiciousIndentation")
    private fun showCustomToast(message: String) {
        // Inflate the custom layout for the Toast
        val inflater = layoutInflater
        val layout: View = inflater.inflate(R.layout.login_sucess_toast, findViewById(R.id.custom_toast_layout))

        // Set the message text
        val toastMessage: TextView = layout.findViewById(R.id.CustomToastnotificationTitle)
        toastMessage.text = message

        // Create the Toast with the custom layout
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout

        // Check if the current activity is MainActivity
//        if (this@LoginActivity::class.java.simpleName == "MainActivity") {
            // Show the Toast
            toast.show()
//        } else {
//            // Navigate to the MainActivity and show the Toast after delay
//            Handler(Looper.getMainLooper()).postDelayed({
//                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//                toast.show()
//                finish()
//            }, 5000)
//        }
    }



    private fun showAlert4(message: String) {
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



    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // ... (unchanged)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        Log.d(ConstanstsApp.tag, "" + s)

        if (membershipNoEditText.text.isNotEmpty()) {
            membershipNoEditText.setBackgroundResource(R.drawable.editable_background)
        } else {
            membershipNoEditText.setBackgroundResource(R.drawable.login_bg)
        }

        if (mobEmailEditText.text.isNotEmpty()) {
            mobEmailEditText.setBackgroundResource(R.drawable.editable_background)
        } else {
            mobEmailEditText.setBackgroundResource(R.drawable.login_bg)
        }
    }


    override fun afterTextChanged(s: Editable?) {
        Log.d(ConstanstsApp.tag, "" + s)
    }

    override fun onClick(v: View?) {
        when (v) {
            keepMeLoggedInCheckBox -> {
                if (keepMeLoggedInCheckBox.isChecked) {
                    sharedPreferencesManager1.setKeepMeLoggedInState(true)
                    Log.d(ConstanstsApp.tag, "checked => true")
                } else {
                    sharedPreferencesManager1.setKeepMeLoggedInState(false)
                    Log.d(ConstanstsApp.tag, "checked => false")
                }
            }
        }
    }
}
