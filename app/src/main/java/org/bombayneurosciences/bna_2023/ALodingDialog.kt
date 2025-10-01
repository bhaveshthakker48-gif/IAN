package org.bombayneurosciences.bna_2023

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.NonNull

class ALodingDialog(@NonNull context: Context) : Dialog(context) {

    init {
        val params = window?.attributes
        params?.gravity = Gravity.CENTER
        window?.attributes = params
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setTitle(null)
        setCancelable(false)
        setOnCancelListener(null)
        val view = LayoutInflater.from(context).inflate(R.layout.activity_aloding_dialog, null)
        setContentView(view)
    }
}
