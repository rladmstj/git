//package com.example.firstweek.ui.home
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.AdapterView
//import android.widget.ListView
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentTransaction
//import androidx.lifecycle.ViewModelProvider
//import androidx.navigation.fragment.findNavController
//import com.example.firstweek.R
//import com.example.firstweek.databinding.FragmentHomeBinding
//import org.json.JSONArray
//import org.json.JSONObject
//import java.io.InputStream
//import java.nio.charset.Charset
//
//class HomeFragment : Fragment() {
//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!
//     private lateinit var contactAdapter: ContactAdapter
//     private var contactsList: MutableList<Contact> = mutableListOf()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding=FragmentHomeBinding.inflate(inflater, container, false)
//
//         val root=binding.root
//
//
//        val jsonString = loadJSONFromRawResource(R.raw.contacts)
//        parseJson(jsonString)
//
//        contactAdapter = ContactAdapter(requireContext(), contactsList)
//        binding.listView.adapter = contactAdapter
//
//
//        binding.listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//            val contact = contactsList[position]
//            val bundle = Bundle().apply {
//                putString("contactName", contact.name)
//                putString("contactPhone", contact.phone)
//            }
//            findNavController().navigate(R.id.contactsDetailFragment, bundle)
//
//
//    }
//
//        return root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//    private fun loadJSONFromRawResource(resourceId: Int): String? {
//        val inputStream: InputStream = resources.openRawResource(resourceId)
//        val size = inputStream.available()
//        val buffer = ByteArray(size)
//        inputStream.read(buffer)
//        inputStream.close()
//        return String(buffer, Charset.forName("UTF-8"))
//    }
//
//    private fun parseJson(jsonString: String?) {
//        jsonString?.let {
//            try {
//                val jsonArray = JSONArray(jsonString)
//                for (i in 0 until jsonArray.length()) {
//                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
//                    val name = jsonObject.getString("name")
//                    val phone=jsonObject.getString("phone")
//
//                    contactsList.add(Contact(name,phone))
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//}
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import androidx.appcompat.widget.SearchView
//import androidx.fragment.app.Fragment
//import com.example.firstweek.databinding.FragmentHomeBinding
//import org.json.JSONArray
//import org.json.JSONObject
//import java.io.InputStream
//import java.nio.charset.Charset
//import java.util.Locale
//
//class HomeFragment : Fragment() {
//
//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var contactAdapter: ArrayAdapter<String>
//    private lateinit var contactList: MutableList<String>
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        contactList = getContactsFromJson()
//        contactAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, contactList)
//        binding.listView.adapter = contactAdapter
//
//        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                if (newText != null) {
//                    val filteredList = contactList.filter {
//                        it.toLowerCase(Locale.getDefault()).contains(newText.toLowerCase(Locale.getDefault()))
//                    }
//                    contactAdapter.clear()
//                    contactAdapter.addAll(filteredList)
//                    contactAdapter.notifyDataSetChanged()
//                }
//                return true
//            }
//        })
//    }
//
//    private fun getContactsFromJson(): MutableList<String> {
//        val contactList = mutableListOf<String>()
//        val jsonString = loadJsonFromAsset("contacts.json")
//        if (jsonString != null) {
//            val jsonArray = JSONArray(jsonString)
//            for (i in 0 until jsonArray.length()) {
//                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
//                val name = jsonObject.getString("name")
//                val phone = jsonObject.getString("phone")
//                contactList.add("$name - $phone")
//            }
//        }
//        return contactList
//    }
//
//    private fun loadJsonFromAsset(filename: String): String? {
//        return try {
//            val inputStream: InputStream = requireContext().assets.open(filename)
//            val size = inputStream.available()
//            val buffer = ByteArray(size)
//            inputStream.read(buffer)
//            inputStream.close()
//            String(buffer, Charset.forName("UTF-8"))
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//            null
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.example.firstweek.databinding.FragmentHomeBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.nio.charset.Charset
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var contactAdapter: ArrayAdapter<String>
    private lateinit var fullContactList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fullContactList = getContactsFromJson()
        contactAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, fullContactList)
        binding.listView.adapter = contactAdapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = if (newText.isNullOrEmpty()) {
                    fullContactList
                } else {
                    fullContactList.filter {
                        it.toLowerCase(Locale.getDefault()).contains(newText.toLowerCase(Locale.getDefault()))
                    }
                }
                updateListView(filteredList)
                return true
            }
        })
    }

    private fun updateListView(newList: List<String>) {
        contactAdapter.clear()
        contactAdapter.addAll(newList)
        contactAdapter.notifyDataSetChanged()
    }

    private fun getContactsFromJson(): List<String> {
        val contactList = mutableListOf<String>()
        val jsonString = loadJsonFromAsset("contacts.json")
        if (jsonString != null) {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val phone = jsonObject.getString("phone")
                val contact = "$name - $phone"
                if (!contactList.contains(contact)) {
                    contactList.add(contact)
                }
            }
        }
        return contactList
    }

    private fun loadJsonFromAsset(filename: String): String? {
        return try {
            val inputStream: InputStream = requireContext().assets.open(filename)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


