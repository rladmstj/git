package com.example.firstweek.ui.notifications

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.firstweek.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        loadImageFromSharedPreferences()

        binding.buttonSave.setOnClickListener {
            deleteAllImages()
        }

        return root
    }

    private fun loadImageFromSharedPreferences() {
        val encodedImages = sharedPreferences.getStringSet("saved_images", null)
        val imageViews = listOf(binding.imageView1, binding.imageView2, binding.imageView3, binding.imageView4, binding.imageView5, binding.imageView6)
        if (encodedImages != null) {
            val decodedBitmaps = encodedImages.map {
                val byteArray = Base64.decode(it, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            }
            for ((bitmap, imageView) in decodedBitmaps.zip(imageViews)) {
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    private fun deleteAllImages() {
        sharedPreferences.edit().remove("saved_images").apply()
        val imageViews = listOf(binding.imageView1, binding.imageView2, binding.imageView3, binding.imageView4, binding.imageView5, binding.imageView6)
        imageViews.forEach {
            it.setImageBitmap(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

