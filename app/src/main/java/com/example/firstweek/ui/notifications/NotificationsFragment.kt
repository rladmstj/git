package com.example.firstweek.ui.notifications

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.firstweek.databinding.FragmentNotificationsBinding
import java.io.ByteArrayOutputStream

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private val imageViews = mutableListOf<ImageView>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        loadImagesFromSharedPreferences()

        binding.buttonSave.setOnClickListener {
            clearSelectedImagesFromSharedPreferences()
        }

        return root
    }

    private fun loadImagesFromSharedPreferences() {
        binding.gridLayout.removeAllViews()
        imageViews.clear()

        val savedImages = sharedPreferences.getStringSet("saved_images", emptySet())
        savedImages?.forEachIndexed { index, encodedImage ->
            val byteArray = Base64.decode(encodedImage, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            val imageView = ImageView(requireContext()).apply {
                setImageBitmap(bitmap)
                layoutParams = ViewGroup.LayoutParams(150, 150)
                setOnClickListener {
                    toggleImageSelection(this)
                }
            }
            imageViews.add(imageView)
            binding.gridLayout.addView(imageView)
        }
    }

    private fun toggleImageSelection(imageView: ImageView) {
        imageView.isSelected = !imageView.isSelected
        imageView.alpha = if (imageView.isSelected) 0.5f else 1.0f
    }

    private fun clearSelectedImagesFromSharedPreferences() {
        val selectedImages = imageViews.filter { it.isSelected }
        val existingImages = sharedPreferences.getStringSet("saved_images", mutableSetOf())?.toMutableSet()

        selectedImages.forEach { selectedImage ->
            val bitmap = (selectedImage.drawable as BitmapDrawable).bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)
            existingImages?.remove(encodedImage)
        }

        sharedPreferences.edit().putStringSet("saved_images", existingImages).apply()
        loadImagesFromSharedPreferences()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


