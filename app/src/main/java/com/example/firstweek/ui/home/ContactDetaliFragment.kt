package com.example.firstweek.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.firstweek.R

class ContactDetailFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels() // ViewModel 초기화

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_contact_detail, container, false)

        val nameTextView: TextView = root.findViewById(R.id.nameTextView)
        val phoneTextView: TextView = root.findViewById(R.id.phoneTextView)
        val callIcon: ImageView = root.findViewById(R.id.callIcon)

        sharedViewModel.selectedContact.observe(viewLifecycleOwner, { contact ->
            nameTextView.text = contact.name
            phoneTextView.text = contact.phone

            callIcon.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${contact.phone}")
                }
                startActivity(intent)
            }
        })

        return root
    }
}

