package com.example.firstweek.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firstweek.R
import com.example.firstweek.databinding.FragmentAddContactBinding

class AddContactFragment : Fragment() {
    private var _binding: FragmentAddContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddContactBinding.inflate(inflater, container, false)
        val root = binding.root

        binding.saveButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val phone = binding.phoneEditText.text.toString()
            if (name.isBlank() || phone.isBlank()) {
                Toast.makeText(requireContext(), "Name and phone cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val formattedPhone = formatPhoneNumber(phone)
            val newContact = Contact(name, formattedPhone)

            try {
                // HomeFragment를 찾기 위한 안전한 방법
                val navController = findNavController()
                val backStackEntry = navController.getBackStackEntry(R.id.navigation_home)
                backStackEntry.savedStateHandle.set("newContact", newContact)
                navController.popBackStack()
            } catch (e: Exception) {
                Log.e("AddContactFragment", "Error adding contact", e)
                Toast.makeText(requireContext(), "Error adding contact", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun formatPhoneNumber(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        return when (digits.length) {
            10 -> digits.replaceFirst(Regex("(\\d{3})(\\d{3})(\\d{4})"), "$1-$2-$3")
            11 -> digits.replaceFirst(Regex("(\\d{3})(\\d{4})(\\d{4})"), "$1-$2-$3")
            else -> phone
        }
    }
}

