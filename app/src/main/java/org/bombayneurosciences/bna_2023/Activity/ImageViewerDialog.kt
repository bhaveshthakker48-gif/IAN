package org.bombayneurosciences.bna_2023.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import org.bombayneurosciences.bna_2023.R

class ImageViewerDialog(private val imageUrls: List<String>) : DialogFragment() {
    private var viewPager: ViewPager? = null
    private var btnClose: Button? = null
    private lateinit var pageIndicator: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.dialog_image_viewer, container, false)
        viewPager = view.findViewById(R.id.viewPager)
        btnClose = view.findViewById(R.id.btnClose)
        pageIndicator =view. findViewById(R.id.pageIndicator)

        val adapter = ImagePagerAdapter(childFragmentManager, imageUrls)
        
        
        viewPager?.adapter = adapter // Use 'adapter' property directly

        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // Do nothing
            }

            override fun onPageSelected(position: Int) {
                updatePageIndicator(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                // Do nothing
            }
        })

        btnClose?.setOnClickListener { dismiss() }
        return view
    }

    private fun updatePageIndicator(position: Int) {
        val totalItems = imageUrls.size
        val currentPage = position + 1
        pageIndicator.text = "$currentPage/$totalItems"
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
            dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        }
    }
}
