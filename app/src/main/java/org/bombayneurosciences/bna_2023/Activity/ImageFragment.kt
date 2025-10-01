package org.bombayneurosciences.bna_2023.Activity


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import org.bombayneurosciences.bna_2023.R


class ImageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }
     private var btnClose: Button? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView = view.findViewById<ImageView>(R.id.imageView)

        // Load the image using Picasso with error handling and placeholder
        val imageUrl = requireArguments().getString(ARG_IMAGE_URL)
        val baseUrl = "https://www.telemedocket.com/BNA/public/uploads/voting/img/$imageUrl"
        Log.d("mytag","baseUrl=>"+baseUrl)

        Picasso.get()
            .load(baseUrl)
//            .placeholder(R.drawable.image_voting) // Replace with your placeholder image resource
//            .error(R.drawable.image_voting) // Replace with your error image resource
            .into(imageView)
    }

    private fun dismiss() {
    }

    companion object {
        private const val ARG_IMAGE_URL = "image_url"

        fun newInstance(imageUrl: String?): ImageFragment {
            val fragment = ImageFragment()
            val args = Bundle()
            args.putString(ARG_IMAGE_URL, imageUrl)
            fragment.arguments = args
            return fragment
        }
    }
}
