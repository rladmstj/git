package com.example.firstweek.ui.notifications

import android.content.Context
import android.content.SharedPreferences
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
        loadImagesFromSharedPreferences()

        binding.buttonSave.setOnClickListener {
            clearImagesFromSharedPreferences()
        }

        return root
    }

    private fun loadImagesFromSharedPreferences() {
        val savedImages = sharedPreferences.getStringSet("saved_images", emptySet())
        savedImages?.forEachIndexed { index, encodedImage ->
            val byteArray = Base64.decode(encodedImage, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            when (index) {
                0 -> binding.imageView1.setImageBitmap(bitmap)
                1 -> binding.imageView2.setImageBitmap(bitmap)
                2 -> binding.imageView3.setImageBitmap(bitmap)
                3 -> binding.imageView4.setImageBitmap(bitmap)
                4 -> binding.imageView5.setImageBitmap(bitmap)
                5 -> binding.imageView6.setImageBitmap(bitmap)
                6 -> binding.imageView7.setImageBitmap(bitmap)
                7 -> binding.imageView8.setImageBitmap(bitmap)
                8 -> binding.imageView9.setImageBitmap(bitmap)
                9 -> binding.imageView10.setImageBitmap(bitmap)
                10 -> binding.imageView11.setImageBitmap(bitmap)
                11 -> binding.imageView12.setImageBitmap(bitmap)
                12 -> binding.imageView13.setImageBitmap(bitmap)
                13 -> binding.imageView14.setImageBitmap(bitmap)
                14 -> binding.imageView15.setImageBitmap(bitmap)
                15 -> binding.imageView16.setImageBitmap(bitmap)
                16 -> binding.imageView17.setImageBitmap(bitmap)
                17 -> binding.imageView18.setImageBitmap(bitmap)
                18 -> binding.imageView17.setImageBitmap(bitmap)
                19 -> binding.imageView18.setImageBitmap(bitmap)
                20 -> binding.imageView17.setImageBitmap(bitmap)
            }
        }
    }

    private fun clearImagesFromSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.remove("saved_images")
        editor.apply()
        clearImageViews()
    }

    private fun clearImageViews() {
        binding.imageView1.setImageBitmap(null)
        binding.imageView2.setImageBitmap(null)
        binding.imageView3.setImageBitmap(null)
        binding.imageView4.setImageBitmap(null)
        binding.imageView5.setImageBitmap(null)
        binding.imageView6.setImageBitmap(null)
        binding.imageView7.setImageBitmap(null)
        binding.imageView8.setImageBitmap(null)
        binding.imageView9.setImageBitmap(null)
        binding.imageView10.setImageBitmap(null)
        binding.imageView11.setImageBitmap(null)
        binding.imageView12.setImageBitmap(null)
        binding.imageView13.setImageBitmap(null)
        binding.imageView14.setImageBitmap(null)
        binding.imageView15.setImageBitmap(null)
        binding.imageView16.setImageBitmap(null)
        binding.imageView17.setImageBitmap(null)
        binding.imageView18.setImageBitmap(null)
        binding.imageView19.setImageBitmap(null)
        binding.imageView20.setImageBitmap(null)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
