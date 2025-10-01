package org.bombayneurosciences.bna_2023.Activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class ImagePagerAdapter(fm: FragmentManager?, private val imageUrls: List<String>) :
    FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
        return imageUrls.size
    }

    override fun getItem(position: Int): Fragment {
        return ImageFragment.newInstance(imageUrls[position])
    }
}
