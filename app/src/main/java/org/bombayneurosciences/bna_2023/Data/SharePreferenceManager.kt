package org.bombayneurosciences.bna_2023.Data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bombayneurosciences.bna.Model.Journal.JournalEntry
import org.bombayneurosciences.bna_2023.Model.CaseofMonth.DataX
import org.bombayneurosciences.bna_2023.Model.Notification.Data
import org.bombayneurosciences.bna_2023.Model.Notification.NotificationDataClass
import org.bombayneurosciences.bna_2023.Model.UserDetails.UserDetails
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import java.lang.reflect.Type

class SharedPreferencesManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val notificationDataKey = "notificationData"
    private val selectedSectionKey = "selectedSection" // Add this key
    private val eventListKey = "eventList"
    private val PREF_NAME = "EventPreferences"
    private val KEY_EVENTS = "events"
    private  val KEY_SELECTED_EVENT_NAME = "SelectedEventName"



    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

//    fun saveEvents(events: List<org.bombayneurosciences.bna_2023.Model.Events.Data>) {
//        val gson = Gson()
//        val json = gson.toJson(events)
//        Log.d(ConstanstsApp.tag, "Saved events JSON: $json")
//        editor.putString(KEY_EVENTS, json)
//        editor.apply()
//    }
//
//    fun getStoredEvents(): List<org.bombayneurosciences.bna_2023.Model.Events.Data> {
//        val eventsJson = sharedPreferences.getString(KEY_EVENTS, "")
//        if (eventsJson.isNullOrEmpty()) {
//            return emptyList()
//        }
//        val type = object : TypeToken<List<org.bombayneurosciences.bna_2023.Model.Events.Data>>() {}.type
//        return gson.fromJson(eventsJson, type)
//    }

    // Save selected event name to SharedPreferences
    fun saveSelectedEventName(eventName: String) {
        editor.putString(KEY_SELECTED_EVENT_NAME, eventName)
        editor.apply()
    }

    // Retrieve selected event name from SharedPreferences
    fun getStoredSelectedEventName(): String {
        return sharedPreferences.getString(KEY_SELECTED_EVENT_NAME, "") ?: ""
    }
        fun saveEvents(events: List<org.bombayneurosciences.bna_2023.Model.Events.Data>) {
        val gson = Gson()
        val json = gson.toJson(events)
        Log.d(ConstanstsApp.tag, "Saved events JSON: $json")
        editor.putString(KEY_EVENTS, json)
        editor.apply()
    }

    fun getStoredEvents(): List<org.bombayneurosciences.bna_2023.Model.Events.Data> {
        val eventsJson = sharedPreferences.getString(KEY_EVENTS, "")
        if (eventsJson.isNullOrEmpty()) {
            return emptyList()
        }
        val type = object : TypeToken<List<org.bombayneurosciences.bna_2023.Model.Events.Data>>() {}.type
        return gson.fromJson(eventsJson, type)
    }
    // Save token in SharedPreferences
    fun saveUserToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_token", token)
        editor.apply()
    }

    // Retrieve token from SharedPreferences
    fun getUserToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_token", null)
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
    fun saveNotificationData(notificationData: NotificationDataClass?) {
        val notificationDataJson = gson.toJson(notificationData)
        editor.putString(notificationDataKey, notificationDataJson)
        editor.apply()
    }

    fun getNotificationData(): NotificationDataClass? {
        val notificationDataJson = sharedPreferences.getString(notificationDataKey, null)
        return if (notificationDataJson != null) {
            gson.fromJson(notificationDataJson, NotificationDataClass::class.java)
        } else {
            null
        }
    }

    fun saveJournalData(journalData: List<JournalEntry>, isLatestSection: Boolean) {
        val key = if (isLatestSection) "latest_journalData" else "archive_journalData"
        val journalDataJson = gson.toJson(journalData)
        editor.putString(key, journalDataJson)
        editor.apply()
    }

    // Functions to track the selected section (latest or archive)
    fun isLatestSectionSelected(): Boolean {
        return sharedPreferences.getBoolean(selectedSectionKey, true)
    }

    fun setLatestSectionSelected(isLatest: Boolean) {
        editor.putBoolean(selectedSectionKey, isLatest)
        editor.apply()
    }

    fun getJournalData(isLatestSection: Boolean): List<JournalEntry> {
        val key = if (isLatestSection) "latest_journalData" else "archive_journalData"
        val journalDataJson = sharedPreferences.getString(key, null)
        return if (journalDataJson != null) {
            gson.fromJson(journalDataJson, object : TypeToken<List<JournalEntry>>() {}.type)
        } else {
            emptyList()
        }
    }

//    fun saveEvents(events: List<org.bombayneurosciences.bna_2023.Model.Events.Data>) {
//        val eventsJson = gson.toJson(events)
//        editor.putString(eventListKey, eventsJson)
//        editor.apply()
//    }

//    fun getStoredEvents(): List<org.bombayneurosciences.bna_2023.Model.Events.Data> {
//        val eventsJson = sharedPreferences.getString(eventListKey, null)
//        return if (eventsJson != null) {
//            gson.fromJson(eventsJson, object : TypeToken<List<Data>>() {}.type)
//        } else {
//            emptyList()
//        }
//    }

    fun saveLatestData(data: List<JournalEntry>) {
        val json = gson.toJson(data)
        sharedPreferences.edit().putString("latest_data", json).apply()
    }

    fun saveArchivesData(data: List<JournalEntry>) {
        val json = gson.toJson(data)
        sharedPreferences.edit().putString("archives_data", json).apply()
    }

    fun getOfflineNotifications(): List<Data> {
        val notificationData = getNotificationData()
        return notificationData?.data ?: emptyList()
    }

    fun saveLatestNotification(notification: Data) {
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = gson.toJson(notification)
        prefs.edit().putString("latestNotification", json).apply()
    }

    fun getLatestNotification(): Data? {
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val json = prefs.getString("latestNotification", null)
        return if (json != null) {
            gson.fromJson(json, Data::class.java)
        } else {
            null
        }
    }

    // Notification ko "read" state me mark karen
    fun markNotificationAsRead(notificationId: Int) {
        editor.putBoolean("Notification_$notificationId", true)
        editor.apply()
    }

    // Notification ka "read" state check karen
    fun isNotificationRead(notificationId: Int): Boolean {
        return sharedPreferences.getBoolean("Notification_$notificationId", false)
    }

    fun clearUserData() {
//        editor.remove("userToken")
        editor.clear()// Clear the user token or any other user-related data
        // Add more lines to remove other user-related data if needed
        editor.apply()
    }

    fun isKeepMeLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("keepMeLoggedIn", false)
    }

    companion object {
        private const val KEY_USER_LOGGED_IN = "user_logged_in"
    }

    fun saveEventToVoting(eventId:String,start_date:String,end_date:String)
    {

    }

    fun saveUserDetails(userDetails: UserDetails) {
        editor.putString("user_email", userDetails.email)
        editor.putString("user_name", userDetails.memberNo)
        // Add other user details as needed
        editor.apply()
    }

  /*  fun getUserDetails(): UserDetails {
        val email = sharedPreferences.getString("user_email", "")
        val member = sharedPreferences.getString("user_name", "")
        // Retrieve other user details as needed

        return member?.let { UserDetails(email, it) }
    }*/
}
