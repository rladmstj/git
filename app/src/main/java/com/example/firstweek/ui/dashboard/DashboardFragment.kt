package com.example.firstweek.ui.dashboard

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.firstweek.R
import com.example.firstweek.databinding.FragmentDashboardBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val REQUEST_IMAGE_CAPTURE = 1
    private val PERMISSION_REQUEST_CODE = 100

    private lateinit var sharedPreferences: SharedPreferences
    private val imageViews = mutableListOf<SelectableImageView>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        binding.imageDiaphragm.setOnClickListener {
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

        binding.imageBin.setOnClickListener {
            clearSelectedImagesFromLayout()
        }

        binding.exit.setOnClickListener {
            saveSelectedImagesToGallery()
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
            val imageId = UUID.randomUUID().toString()
            addImageToLayout(croppedBitmap, imageId)
            saveImageToSharedPreferences(croppedBitmap, imageId)
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
        val frameLayout = FrameLayout(requireContext())
        val imageView = ImageView(requireContext()).apply {
            tag = imageId
            setImageBitmap(bitmap)
            layoutParams = ViewGroup.MarginLayoutParams(
                82.dpToPx(), // 너비를 82dp로 설정
                82.dpToPx()  // 높이를 82dp로 설정
            ).apply {
                setMargins(1, 1, 1, 1)
            }
        }
        val checkImageView = ImageView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                24.dpToPx(),
                24.dpToPx()
            ).apply {
                setMargins(0, 8.dpToPx(), 8.dpToPx(), 0)
                gravity = android.view.Gravity.TOP or android.view.Gravity.END
            }
            setImageResource(R.drawable.ic_check) // 체크 이미지 리소스를 설정하십시오
            visibility = View.INVISIBLE
        }

        val selectableImageView = SelectableImageView(imageView, checkImageView)
        frameLayout.addView(imageView)
        frameLayout.addView(checkImageView)

        imageView.setOnClickListener {
            showImageDialog(bitmap)
        }

        imageView.setOnLongClickListener {
            toggleImageSelection(selectableImageView)
            true
        }

        imageViews.add(selectableImageView)
        binding.layoutImage.addView(frameLayout)
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun toggleImageSelection(selectableImageView: SelectableImageView) {
        val imageView = selectableImageView.imageView
        imageView.isSelected = !imageView.isSelected
        imageView.alpha = if (imageView.isSelected) 0.5f else 1.0f
        selectableImageView.checkImageView.visibility = if (imageView.isSelected) View.VISIBLE else View.INVISIBLE

        if (imageView.isSelected) {
            showToast("사진을 삭제하려면 휴지통을 누르고 갤러리에 저장하려면 화살표를 누르세요")
        }
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        val yOffset = binding.imageBin.height + binding.imageDiaphragm.height + 16.dpToPx()
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, yOffset)
        toast.show()
    }

    private fun clearSelectedImagesFromLayout() {
        val selectedImages = imageViews.filter { it.imageView.isSelected }
        val existingImages = sharedPreferences.getStringSet("saved_images", mutableSetOf())?.toMutableSet()

        selectedImages.forEach { selectedImage ->
            val imageId = selectedImage.imageView.tag as String
            existingImages?.remove(imageId)
            sharedPreferences.edit().remove(imageId).apply()
            binding.layoutImage.removeView(selectedImage.imageView.parent as View)
        }

        sharedPreferences.edit().putStringSet("saved_images", existingImages).apply()
        imageViews.removeAll(selectedImages)
    }

    private fun showImageDialog(bitmap: Bitmap) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_image, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.dialog_image_view)
        imageView.setImageBitmap(bitmap)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("닫기") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    private fun saveSelectedImagesToGallery() {
        val selectedImages = imageViews.filter { it.imageView.isSelected }

        selectedImages.forEach { selectedImage ->
            val bitmap = (selectedImage.imageView.drawable as BitmapDrawable).bitmap
            saveImageToGallery(bitmap)
        }
        showToast("선택된 이미지가 갤러리에 저장되었습니다.")
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        val filename = "${UUID.randomUUID()}.png"
        val fos: OutputStream
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val resolver = requireContext().contentResolver
                val contentValues = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES)
                }
                val imageUri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = resolver.openOutputStream(imageUri!!)!!
            } else {
                val imagesDir = requireContext().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
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

    data class SelectableImageView(val imageView: ImageView, val checkImageView: ImageView)
}
