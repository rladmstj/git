package com.example.firstweek.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.firstweek.databinding.FragmentEditContactBinding


class EditContactFragment : Fragment() {
    private var _binding: FragmentEditContactBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditContactBinding.inflate(inflater, container, false)
        val root = binding.root

        var currentContact: Contact? = null

        sharedViewModel.selectedContact.observe(viewLifecycleOwner) { contact ->
            contact?.let {
                currentContact = it
                binding.nameEditText.setText(it.getName())
                binding.phoneEditText.setText(it.getPhone())
            }
        }

        binding.saveButton.setOnClickListener {
            val newName = binding.nameEditText.text.toString()
            val newPhone = binding.phoneEditText.text.toString()
            if (newName.isBlank() || newPhone.isBlank()) {
                Toast.makeText(requireContext(), "Name and phone cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            currentContact?.let { oldContact ->
                val updatedContact = Contact(newName, newPhone)
                sharedViewModel.updateContact(oldContact, updatedContact)
                findNavController().popBackStack()
                Toast.makeText(requireContext(), "Contact updated", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
