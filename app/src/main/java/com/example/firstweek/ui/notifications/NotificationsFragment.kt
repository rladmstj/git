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






    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
