package com.example.firstweek.ui.dashboard

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.firstweek.databinding.FragmentDashboardBinding
import java.io.ByteArrayOutputStream
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val REQUEST_IMAGE_CAPTURE = 1
    private val PERMISSION_REQUEST_CODE = 100

    private lateinit var sharedPreferences: SharedPreferences
    private val imageViews = mutableListOf<ImageView>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // 카메라 버튼 클릭 리스너 설정
        binding.buttonCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                dispatchTakePictureIntent()
            }
        }

        binding.buttonSave.setOnClickListener {
            val bitmap = binding.imagePreview.drawable.toBitmap()
            val croppedBitmap = cropToSquare(bitmap)
            val imageId = UUID.randomUUID().toString() // 고유한 ID 생성
            saveImageToSharedPreferences(croppedBitmap, imageId)
            addImageToLayout(croppedBitmap, imageId)
        }

        binding.buttonDelete.setOnClickListener {
            clearSelectedImagesFromLayout()
        }

        loadImagesFromSharedPreferences()

        return root
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val croppedBitmap = cropToSquare(imageBitmap)
            binding.imagePreview.setImageBitmap(croppedBitmap)
        }
    }

    private fun cropToSquare(bitmap: Bitmap): Bitmap {
        val dimension = Math.min(bitmap.width, bitmap.height)
        val startX = (bitmap.width - dimension) / 2
        val startY = (bitmap.height - dimension) / 2
        return Bitmap.createBitmap(bitmap, startX, startY, dimension, dimension)
    }

    private fun saveImageToSharedPreferences(bitmap: Bitmap, imageId: String) {
        val editor = sharedPreferences.edit()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)
        val existingImages = sharedPreferences.getStringSet("saved_images", mutableSetOf())?.toMutableSet()
        existingImages?.add(imageId)
        editor.putStringSet("saved_images", existingImages)
        editor.putString(imageId, encodedImage)
        editor.apply()
    }

    private fun loadImagesFromSharedPreferences() {
        binding.layoutImage.removeAllViews()
        imageViews.clear()

        val savedImages = sharedPreferences.getStringSet("saved_images", emptySet())
        savedImages?.forEach { imageId ->
            val encodedImage = sharedPreferences.getString(imageId, null)
            if (encodedImage != null) {
                val byteArray = Base64.decode(encodedImage, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                addImageToLayout(bitmap, imageId)
            }
        }
    }

    private fun addImageToLayout(bitmap: Bitmap, imageId: String) {
        val imageView = ImageView(requireContext()).apply {
            tag = imageId
            setImageBitmap(bitmap)
            layoutParams = ViewGroup.MarginLayoutParams(
                180.dpToPx(), // 너비를 180dp로 설정
                180.dpToPx()  // 높이를 180dp로 설정
            ).apply {
                setMargins(1, 1, 1, 1)
            }
            setOnClickListener {
                toggleImageSelection(this)
            }
        }
        imageViews.add(imageView)
        binding.layoutImage.addView(imageView)
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun toggleImageSelection(imageView: ImageView) {
        imageView.isSelected = !imageView.isSelected
        imageView.alpha = if (imageView.isSelected) 0.5f else 1.0f
    }

    private fun clearSelectedImagesFromLayout() {
        val selectedImages = imageViews.filter { it.isSelected }
        val existingImages = sharedPreferences.getStringSet("saved_images", mutableSetOf())?.toMutableSet()

        selectedImages.forEach { selectedImage ->
            val imageId = selectedImage.tag as String
            existingImages?.remove(imageId)
            sharedPreferences.edit().remove(imageId).apply()
            binding.layoutImage.removeView(selectedImage)
        }

        sharedPreferences.edit().putStringSet("saved_images", existingImages).apply()
        imageViews.removeAll(selectedImages)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                dispatchTakePictureIntent()
            } else {
                // 권한이 거부되었습니다. 사용자에게 알리거나 다른 처리를 여기에 추가하십시오.
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


