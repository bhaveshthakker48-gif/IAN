package org.bombayneurosciences.bna_2023.utils

import android.content.Context
import android.content.SharedPreferences

class sharepreferenceAppkill {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var mContext: Context
    private val privateMode = 0

    val APP_KILL ="APP_KILL"
    constructor(context: Context):this()
    {
        mContext=context
        sharedPreferences=context.getSharedPreferences(SessionManager1.PREF_NAME,privateMode)
        editor=sharedPreferences.edit()
    }

    constructor()

    fun setAppKill(Login:String)
    {
        editor.putString(SessionManager1.APP_KILL,Login)
        editor.apply()
    }
    fun getAppKill():String?
    {
        return sharedPreferences.getString(SessionManager1.APP_KILL,null)
    }

    fun clearAppKill()
    {
        editor.clear().apply()
    }
}