//package com.example.firstweek.ui.home
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.ListView
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
//import com.example.firstweek.R
//import com.example.firstweek.databinding.FragmentHomeBinding
//
//class HomeFragment : Fragment() {
//
//    private var _binding: FragmentHomeBinding? = null
//
//    // This property is only valid between onCreateView and
//    // onDestroyView.
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
//
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//       // val textView: TextView = binding.textHome
//        //homeViewModel.text.observe(viewLifecycleOwner) {
//        //    textView.text = it
//       // }이건 진짜 필요 없는 것
//
//        val item =arrayOf("tkrhk","pear","goe","ejiefe")
//        val listView: ListView =root.findViewById(R.id.listView)
//        listView.adapter=ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,item)
//
//        return root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
//package com.example.firstweek.ui.home
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ListView
//import androidx.fragment.app.Fragment
//import com.example.firstweek.R
//import java.util.*
//
//class HomeFragment : Fragment() {
//
//    private lateinit var listView: ListView
//    private lateinit var contactAdapter: ContactAdapter
//    private var contactsList: MutableList<Contact> = mutableListOf()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val root = inflater.inflate(R.layout.fragment_home, container, false)
//
//        // Sample data for demonstration
//        contactsList.add(Contact("John Doe"))
//        contactsList.add(Contact("Jane Smith"))
//        contactsList.add(Contact("Michael Johnson"))
//
//        // Initialize ListView and ContactAdapter
//        listView = root.findViewById(R.id.listView)
//        contactAdapter = ContactAdapter(requireContext(), contactsList)
//        listView.adapter = contactAdapter
//
//        return root
//    }
//}

package com.example.firstweek.ui.home

import androidx.fragment.app.FragmentTransaction
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.firstweek.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.nio.charset.Charset

class HomeFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var contactAdapter: ContactAdapter
   // private lateinit var selectedContactTextView: TextView
    private var contactsList: MutableList<Contact> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize ListView and ContactAdapter
        listView = root.findViewById(R.id.listView)
    //    selectedContactTextView=root.findViewById(R.id.selectedContactTextView)

        // Load JSON from raw resources
        val jsonString = loadJSONFromRawResource(R.raw.contacts)
        parseJson(jsonString)

        // Set up adapter with parsed contacts
        contactAdapter = ContactAdapter(requireContext(), contactsList)
        listView.adapter = contactAdapter

       // listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
         //   val contact = contactsList[position]
           // selectedContactTextView.text = "Name: ${contact.name}\nPhone: ${contact.phone}"
        //}
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val contact = contactsList[position]
            // Navigate to ContactDetailFragment with the selected contact details
            val fragment = ContactDetailFragment.newInstance(contact.name, contact.phone)
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return root
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
                val jsonArray = JSONArray(jsonString)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                    val name = jsonObject.getString("name")
                    val phone=jsonObject.getString("phone")

                    contactsList.add(Contact(name,phone))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

