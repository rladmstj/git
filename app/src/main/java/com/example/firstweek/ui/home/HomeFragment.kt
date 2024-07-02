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
//
//    private lateinit var contactAdapter: ContactAdapter
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
//        val jsonString = loadJSONFromRawResource(R.raw.contacts)
//        parseJson(jsonString)
//
//        // Set up adapter with parsed contacts
//        contactAdapter = ContactAdapter(requireContext(), contactsList)
//        binding.listView.adapter = contactAdapter
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

package com.example.firstweek.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firstweek.R
import com.example.firstweek.databinding.FragmentHomeBinding
 import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.nio.charset.Charset

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var contactAdapter: ContactAdapter
    private var contactsList: MutableList<Contact> = mutableListOf()

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding.root

        loadContactsFromFile()
        sortContacts()

        contactAdapter = ContactAdapter(requireContext(), contactsList) { contact ->
            sharedViewModel.selectContact(contact)
            findNavController().navigate(R.id.contactsDetailFragment)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = contactAdapter

        binding.floatingActionButton5.setOnClickListener {
            findNavController().navigate(R.id.addContactFragment)
        }

        findNavController().getBackStackEntry(R.id.navigation_home).savedStateHandle.getLiveData<Contact>("newContact")
            .observe(viewLifecycleOwner, Observer { newContact ->
                newContact?.let {
                    if (!contactsList.contains(it)) {
                        addContact(it)
                        findNavController().getBackStackEntry(R.id.navigation_home).savedStateHandle.set("newContact", null)
                    }
                }
            })

        sharedViewModel.deletedContact.observe(viewLifecycleOwner, Observer { deletedContact ->
            deletedContact?.let {
                removeContact(it)
                sharedViewModel.clearDeletedContact()
            }
        })

        sharedViewModel.selectedContact.observe(viewLifecycleOwner, Observer { updatedContact ->
            updatedContact?.let {
                updateContact(it)
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadContactsFromFile() {
        try {
            val file = File(requireContext().filesDir, "contacts.json")
            val jsonString: String? = if (file.exists()) {
                Log.d("HomeFragment", "Loading contacts from file")
                file.readText()
            } else {
                Log.d("HomeFragment", "Loading contacts from raw resource")
                loadJSONFromRawResource(R.raw.contacts)
            }
            parseJson(jsonString)
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error loading contacts from file", e)
        }
    }

    private fun loadJSONFromRawResource(resourceId: Int): String? {
        val inputStream: InputStream = resources.openRawResource(resourceId)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, Charset.forName("UTF-8"))
    }

    private fun parseJson(jsonString: String?) {
        jsonString?.let {
            try {
                val jsonArray = JSONArray(it)
                contactsList.clear()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val name = jsonObject.getString("name")
                    val phone = jsonObject.getString("phone")
                    if (!contactsList.any { contact -> contact.getName() == name && contact.getPhone() == phone }) {
                        contactsList.add(Contact(name, phone))
                    }
                }
                sortContacts()
                Log.d("HomeFragment", "Contacts loaded: ${contactsList.size}")
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error parsing JSON", e)
            }
        }
    }

    fun addContact(contact: Contact) {
        contactsList.add(contact)
        sortContacts()
        contactAdapter.notifyDataSetChanged()
        saveContactsToJSON()
    }

    fun removeContact(contact: Contact) {
        Log.d("HomeFragment", "Trying to remove contact: ${contact.getName()} - ${contact.getPhone()}")
        val iterator = contactsList.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.getName() == contact.getName() && item.getPhone() == contact.getPhone()) {
                iterator.remove()
                contactAdapter.notifyDataSetChanged()
                saveContactsToJSON()
                Log.d("HomeFragment", "Contact removed: ${contact.getName()} - ${contact.getPhone()}")
                break
            }
        }
    }

    fun updateContact(updatedContact: Contact) {
        Log.d("HomeFragment", "Trying to update contact: ${updatedContact.getName()} - ${updatedContact.getPhone()}")
        val index = contactsList.indexOfFirst { it.getName() == updatedContact.getName() && it.getPhone() == updatedContact.getPhone() }
        if (index != -1) {
            contactsList[index] = updatedContact
            contactAdapter.notifyItemChanged(index)
            saveContactsToJSON()
            Log.d("HomeFragment", "Contact updated: ${updatedContact.getName()} - ${updatedContact.getPhone()}")
        }
    }

    private fun sortContacts() {
        contactsList.sortBy { it.getName() }
    }

    private fun saveContactsToJSON() {
        try {
            val jsonArray = JSONArray()
            for (contact in contactsList) {
                val jsonObject = JSONObject()
                jsonObject.put("name", contact.getName())
                jsonObject.put("phone", contact.getPhone())
                jsonArray.put(jsonObject)
            }

            val file = File(requireContext().filesDir, "contacts.json")
            file.writeText(jsonArray.toString())
            Log.d("HomeFragment", "Contacts saved to JSON file: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error saving contacts to JSON", e)
        }
    }
}

