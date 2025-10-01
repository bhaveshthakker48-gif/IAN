package org.bombayneurosciences.bna_2023.Data
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

import org.bombayneurosciences.bna_2023.Model.UserDetails.UserDetails


class SharedPreferencesActivity(context: Context) {


    companion object {
        private const val PREF_NAME = "login_prefs"
        private const val KEEP_ME_LOGGED_IN_KEY = "keep_me_logged_in"
        private const val MEMBER_NO_KEY = "member_no"
        private const val EMAIL_PHONE_KEY = "email_phone"
        private const val EMAIL_PHONE = "email"
        private const val USER_LOGGED_IN_KEY = "user_logged_in"
        private const val KEY_FIRST_LAUNCH = "isFirstLaunch"
        private const val DELEGATE_ID_KEY = "delegate_id"

        // Additional details keys
        private const val TITLE_KEY = "title"
        private const val FNAME_KEY = "fname"
        private const val MNAME_KEY = "mname"
        private const val LNAME_KEY = "lname"


        private const val PREF_SUCCESS_VALUE = "pref_login_success_value"

        fun saveSuccessValue(context: Context, successValue: Int) {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt(PREF_SUCCESS_VALUE, successValue)
            editor.apply()
        }

        fun getSuccessValue(context: Context): Int {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getInt(PREF_SUCCESS_VALUE, 0)
        }
        // Save and retrieve additional details
        fun saveUserDetails(context: Context, userDetails: UserDetails) {
            val editor = getSharedPreferences(context).edit()
            editor.putString(TITLE_KEY, userDetails.title)
            editor.putString(FNAME_KEY, userDetails.fname)
            editor.putString(MNAME_KEY, userDetails.mname)
            editor.putString(LNAME_KEY, userDetails.lname)
            editor.putString(EMAIL_PHONE, userDetails.email)
            editor.putString(EMAIL_PHONE_KEY,userDetails.emailPhone)
            editor.putString(MEMBER_NO_KEY, userDetails.memberNo)

            editor.apply()
        }

        fun getUserDetails(context: Context): UserDetails {
            val preferences = getSharedPreferences(context)
            val title = preferences.getString(TITLE_KEY, "") ?: ""
            val fname = preferences.getString(FNAME_KEY, "") ?: ""
            val mname = preferences.getString(MNAME_KEY, "") ?: ""
            val lname = preferences.getString(LNAME_KEY, "") ?: ""
            val email = preferences.getString(EMAIL_PHONE, "") ?: ""
            val emailPhone = preferences.getString(EMAIL_PHONE_KEY, "") ?: ""
            val memberNo = preferences.getString(MEMBER_NO_KEY, "") ?: ""

            return UserDetails(title, fname, mname, lname, emailPhone, memberNo,email)
        }

        fun setKeepMeLoggedIn(context: Context, value: Boolean) {
            val editor = getSharedPreferences(context).edit()
            editor.putBoolean(KEEP_ME_LOGGED_IN_KEY, value)
            editor.apply()
            Log.d("KeepMeLoggedIn", "Value set to: $value")
        }

        fun getKeepMeLoggedIn(context: Context): Boolean {
            val keepLoggedIn = getSharedPreferences(context).getBoolean(KEEP_ME_LOGGED_IN_KEY, false)
            Log.d("KeepMeLoggedIn", "Value retrieved: $keepLoggedIn")
            return keepLoggedIn
        }

        fun saveDelegateId(context: Context, delegateId: Int) {
            val editor = getSharedPreferences(context).edit()
            editor.putInt(DELEGATE_ID_KEY, delegateId)
            editor.apply()
        }

        fun getDelegateId(context: Context): Int {
            return getSharedPreferences(context).getInt(DELEGATE_ID_KEY, -1)
        }
        fun saveUserInputs(context: Context, memberNo: String, emailPhone: String) {
            val editor = getSharedPreferences(context).edit()
            editor.putString(MEMBER_NO_KEY, memberNo)
            editor.putString(EMAIL_PHONE_KEY, emailPhone)
            editor.apply()
        }
        fun getSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }

        fun clearPreferences(context: Context) {
            val editor = getSharedPreferences(context).edit()
            editor.clear()
            editor.apply()
        }


    }
}
