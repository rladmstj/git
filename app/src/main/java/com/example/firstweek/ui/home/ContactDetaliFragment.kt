package com.example.firstweek.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.firstweek.R

class ContactDetailFragment : Fragment() {

    companion object {
        private const val ARG_NAME = "name"
        private const val ARG_PHONE = "phone"

        fun newInstance(name: String, phone: String): ContactDetailFragment {
            val fragment = ContactDetailFragment()
            val args = Bundle()
            args.putString(ARG_NAME, name)
            args.putString(ARG_PHONE, phone)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_contact_detail, container, false)

        val nameTextView: TextView = root.findViewById(R.id.nameTextView)
        val phoneTextView: TextView = root.findViewById(R.id.phoneTextView)

        arguments?.let {
            nameTextView.text = it.getString(ARG_NAME)
            phoneTextView.text = it.getString(ARG_PHONE)
        }

        return root
    }
}