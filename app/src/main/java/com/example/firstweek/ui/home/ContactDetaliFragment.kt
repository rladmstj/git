package com.example.firstweek.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.firstweek.databinding.FragmentContactDetailBinding

class ContactDetailFragment : Fragment() {
    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactDetailBinding.inflate(inflater, container, false)
        val root = binding.root

        sharedViewModel.selectedContact.observe(viewLifecycleOwner) { contact ->
            contact?.let {
                binding.nameTextView.text = it.getName()
                binding.phoneTextView.text = it.getPhone()
            }
        }

        binding.button2.setOnClickListener {
            sharedViewModel.selectedContact.value?.let { contact ->
                sharedViewModel.deleteContact(contact)
                findNavController().popBackStack()
                Toast.makeText(requireContext(), "Contact deleted", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
