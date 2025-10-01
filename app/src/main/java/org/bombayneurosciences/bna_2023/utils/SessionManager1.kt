package org.bombayneurosciences.bna_2023.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import org.bombayneurosciences.bna_2023.Model.CaseofMonth.DataX
import org.bombayneurosciences.bna_2023.Model.PdfFileInfo
import java.lang.reflect.Type

class SessionManager1() {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var mContext: Context
    private val privateMode = 0

    private val gson = Gson()

    companion object{
        val PREF_NAME="BNA"
        val KeepMeLoggedIn = "KeepMeLoggedInState"
        private const val USER_LOGGED_OUT = "UserLoggedOut"

        val MY_TOKEN="My_Token"
        val USER_TYPE="User_Type"
        val MY_LOGIN="MY_LOGIN"
        val MY_LOGIN_DATA="My_Login_Data"
        val MY_LOGIN_STUDENT_DATA="My_Login_Student_Data"
        val NOTIFICATION="My_Notification"
        val QUICK_LINKS="QUICK_LINKS"
        val EVENT_DATA="EVENT_DATA"
        val LOGIN_ACTIVITY="LOGIN_ACTIVITY"
        val NOTIFICATION_BACK="NOTIFICATION_BACK"
        val MEMBER_DIRECTORY_DATA="MEMBER_DIRECTORY_DATA"
        val BOTTOM_MENU_BAR="BOTTOM_MENU_BAR"
        val APP_KILL ="APP_KILL"
        private const val KEY_IS_LOGGED_IN = "IsLoggedIn"
        private const val KEY_REMEMBER_ME = "remember_me"
        val IS_BACK="IS_BACK"
         val KEY_PDF_FILE_NAMES = "pdf_file_names"
        val KEY_PDF_FILE_NAMES1 = "pdf_file_names"
        val isDownloadInitiated="isDownloadInitiated"
        val IS_DOWNLOAD="IS_DOWNLOAD"

    }

    constructor(context: Context):this()
    {
        mContext=context
        sharedPreferences=context.getSharedPreferences(PREF_NAME,privateMode)
        editor=sharedPreferences.edit()
    }
    fun setUserLoggedOut() {
        editor.putBoolean(USER_LOGGED_OUT, true)
        editor.apply()
    }

    fun isUserLoggedOut(): Boolean {
        return sharedPreferences.getBoolean(USER_LOGGED_OUT, false)
    }

    fun setTokenNumber(token:String)
    {
        editor.putString(MY_TOKEN,token)
        editor.apply()
    }
    fun saveCases(cases: List<DataX>) {
        val json = gson.toJson(cases)
        sharedPreferences.edit().putString("cases", json).apply()
    }

    fun getCases(): List<DataX> {
        val json = sharedPreferences.getString("cases", "")
        val type: Type = object : TypeToken<List<DataX>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }


    fun setNotification(notification:String)
    {
        editor.putString(NOTIFICATION,notification)
        editor.apply()
    }
    // Method to set the login status
    fun setLogin(isLoggedIn: Boolean, rememberMe: Boolean) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.putBoolean(KEY_REMEMBER_ME, rememberMe)
        editor.apply()
    }

    fun getUserStatus(): Pair<Boolean, Boolean> {
        val isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        val shouldRememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)

        return Pair(isLoggedIn, shouldRememberMe)
    }


    // Method to get the login status
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setBottomMenuBar(notification:String)
    {
        editor.putString(BOTTOM_MENU_BAR,notification)
        editor.apply()
    }
    fun setIntentFromLoginActivity(Login:String)
    {
        editor.putString(LOGIN_ACTIVITY,Login)
        editor.apply()
    }
//    fun setAppKill(Login:String)
//    {
//        editor.putString(APP_KILL,Login)
//        editor.apply()
//    }
//    fun getAppKill():String?
//    {
//        return sharedPreferences.getString(APP_KILL,null)
//    }

    fun getIntentFromLoginActivity():String?
    {
        return sharedPreferences.getString(LOGIN_ACTIVITY,null)
    }

    fun getBottomMenuBar():String?
    {
        return sharedPreferences.getString(BOTTOM_MENU_BAR,null)
    }

    fun getNotification():String?
    {
        return sharedPreferences.getString(NOTIFICATION,null)
    }
    fun getTokenNumber():String?
    {
        return sharedPreferences.getString(MY_TOKEN,null)
    }

    fun setLogin(login:Int)
    {
        editor.putInt(MY_LOGIN,login)
        editor.apply()
    }

    fun setNotificationBack(login:String)
    {
        editor.putString(NOTIFICATION_BACK,login)
        editor.apply()
    }

    fun getNotificationBack():String?
    {
        return sharedPreferences.getString(NOTIFICATION_BACK,null)
    }
    fun getLogin():Int?
    {
        return sharedPreferences.getInt(MY_LOGIN,0)
    }



    fun setKeepMeLoggedInState(keepMeLoggedIn: Boolean) {
        editor.putBoolean(KeepMeLoggedIn, keepMeLoggedIn)
        editor.apply()
    }

    fun getKeepMeLoggedInState(): Boolean {
        return sharedPreferences.getBoolean(KeepMeLoggedIn, false)
    }

    fun setBackState(keepMeLoggedIn: Boolean) {
        editor.putBoolean(IS_BACK, keepMeLoggedIn)
        editor.apply()
    }
    fun getBackState():Boolean {
        return sharedPreferences.getBoolean(IS_BACK, false)
    }


    fun clearSession() {
        editor.clear()
        editor.apply()
    }

    fun saveState(state: String) {
        val editor = sharedPreferences.edit()
        editor.putString("state_key", state)
        editor.apply()
    }

    fun setTopicId(topicId1: Int) {
        editor.putInt("topicId1", topicId1)
        //editor.putString("topicName", topicName)
        editor.apply()
    }

    fun getTopicId(): Int {
        return sharedPreferences.getInt("topicId1", 0) // Default value 0, aap apne use case ke hisaab se change kar sakte hain
    }

    fun set_isDownloadInitiated(name: Boolean) {
        editor.putBoolean(isDownloadInitiated, name)
        editor.apply()
    }
    fun get_isDownloadInitiated(): Boolean? {
        return sharedPreferences.getBoolean(isDownloadInitiated, false)
    }

    fun setName(name: String) {
        editor.putString("name", name)
        editor.apply()
    }

    fun getName(): String? {
        return sharedPreferences.getString("name", null)
    }

    fun setPdfFileNames(pdfFileNames: List<String>) {
        val json = gson.toJson(pdfFileNames)
        editor.putString(KEY_PDF_FILE_NAMES1, json)
        editor.apply()
    }

    fun setPdfFileNames1(pdfFileInfos:List<PdfFileInfo>) {
        val gson = Gson()
        val json = gson.toJson(pdfFileInfos)
        editor.putString(KEY_PDF_FILE_NAMES, json)
        editor.apply()
    }

    /*fun getPdfFileInfos(): List<PdfFileInfo> {
        val gson = Gson()
        val json = sharedPreferences.getString(KEY_PDF_FILE_NAMES, null)
        val type: Type = object : TypeToken<List<PdfFileInfo>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }*/
    fun getPdfFileInfos(): List<PdfFileInfo> {
        val gson = Gson()
        val json = sharedPreferences.getString(KEY_PDF_FILE_NAMES, null)

        // Print out json for debugging
        Log.d("PdfFileInfo", "JSON from sharedPreferences: $json")

        if (json.isNullOrEmpty()) {
            Log.w("PdfFileInfo", "JSON string is null or empty")
            return emptyList()
        }

        try {
            val type: Type = object : TypeToken<List<PdfFileInfo>>() {}.type
            return gson.fromJson(json, type) ?: emptyList()
        } catch (e: JsonSyntaxException) {
            Log.e("PdfFileInfo", "Error parsing JSON", e)
            return emptyList()
        }
    }


  /*  fun getPdfFileNames(): List<String> {
        val json = sharedPreferences.getString(KEY_PDF_FILE_NAMES, null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }*/

    fun getPdfFileNames(): List<String> {
        val json = sharedPreferences.getString(KEY_PDF_FILE_NAMES1, null)
        return if (!json.isNullOrEmpty()) {
            try {
                val type = object : TypeToken<List<String>>() {}.type
                gson.fromJson(json, type) ?: mutableListOf()
            } catch (e: JsonSyntaxException) {
                Log.e("PdfFileInfo", "Error parsing JSON: ${e.message}")
                mutableListOf() // Return an empty list or handle the error accordingly
            }
        } else {
            mutableListOf()
        }
    }



    fun getIsDownload(): String? {

        return sharedPreferences.getString(IS_DOWNLOAD, null)

    }

    fun setIsDownload(isDownload:String) {
        editor.putString(IS_DOWNLOAD, isDownload)
        editor.apply()
    }


}