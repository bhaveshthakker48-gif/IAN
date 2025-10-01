package org.bombayneurosciences.bna_2023.utils

import android.content.Context

object SessionManagerSingleton {
    private var sessionManager: SessionManager1? = null

    fun getSessionManager(context: Context): SessionManager1 {
        if (sessionManager == null) {
            sessionManager = SessionManager1(context)
        }
        return sessionManager!!
    }
}
