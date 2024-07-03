package com.example.firstweek.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.firstweek.R
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

        var currentContact: Contact? = null

        sharedViewModel.selectedContact.observe(viewLifecycleOwner) { contact ->
            contact?.let {
                currentContact = it
                binding.nameTextView.text = it.getName()
                binding.phoneTextView.text = it.getPhone()
            }
        }

        binding.button2.setOnClickListener {
            currentContact?.let { contact ->
                sharedViewModel.deleteContact(contact)
                findNavController().popBackStack()
                Toast.makeText(requireContext(), "Contact deleted", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonEdit.setOnClickListener {
            findNavController().navigate(R.id.editContactFragment)
        }

        binding.callIcon.setOnClickListener {
            currentContact?.let { contact ->
                val phoneUri = Uri.parse("tel:${contact.getPhone()}")
                val callIntent = Intent(Intent.ACTION_DIAL, phoneUri)
                startActivity(callIntent)
            }
        }

        binding.messageIcon.setOnClickListener {
            currentContact?.let { contact ->
                val smsUri = Uri.parse("smsto:${contact.getPhone()}")
                val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri)
                startActivity(smsIntent)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


