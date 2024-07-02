package com.example.firstweek.ui.notifications

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.firstweek.R
import com.example.firstweek.databinding.FragmentNotificationsBinding
import org.json.JSONArray
import org.json.JSONObject
import android.widget.TextView
import android.widget.Button

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private val tasks = mutableListOf<Task>()

    data class Task(val text: String, var isChecked: Boolean)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        loadTasks()

        binding.addTaskButton.setOnClickListener {
            addTask("")
        }

        return root
    }

    private fun addTask(text: String) {
        val taskLayout = LinearLayout(requireContext())
        taskLayout.orientation = LinearLayout.HORIZONTAL

        val checkBox = CheckBox(requireContext())
        val editText = EditText(requireContext())
        editText.hint = "Enter task"
        editText.setText(text)
        editText.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )

        val task = Task(text, false)
        tasks.add(task)
        saveTasks()

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            task.isChecked = isChecked
            if (isChecked) {
                editText.paintFlags = editText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                editText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                Toast.makeText(requireContext(), "참 잘했어요!", Toast.LENGTH_SHORT).show()
            } else {
                editText.paintFlags = editText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                editText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                Toast.makeText(requireContext(), "깨비", Toast.LENGTH_SHORT).show()
            }
            saveTasks()
        }

        editText.setOnLongClickListener {
            showDeleteConfirmationDialog(task, taskLayout)
            true
        }

        taskLayout.addView(checkBox)
        taskLayout.addView(editText)
        binding.tasksContainer.addView(taskLayout)
    }

    private fun showDeleteConfirmationDialog(task: Task, taskLayout: LinearLayout) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog, null)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
        val buttonYes = dialogView.findViewById<Button>(R.id.dialog_button_yes)
        val buttonNo = dialogView.findViewById<Button>(R.id.dialog_button_no)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        buttonYes.setOnClickListener {
            tasks.remove(task)
            binding.tasksContainer.removeView(taskLayout)
            saveTasks()
            Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }

        buttonNo.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun saveTasks() {
        val editor = sharedPreferences.edit()
        val jsonArray = JSONArray()
        tasks.forEach { task ->
            val jsonObject = JSONObject()
            jsonObject.put("text", task.text)
            jsonObject.put("isChecked", task.isChecked)
            jsonArray.put(jsonObject)
        }
        editor.putString("tasks", jsonArray.toString())
        editor.apply()
    }

    private fun loadTasks() {
        val tasksString = sharedPreferences.getString("tasks", "[]")
        val jsonArray = JSONArray(tasksString)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val text = jsonObject.getString("text")
            val isChecked = jsonObject.getBoolean("isChecked")
            val task = Task(text, isChecked)
            tasks.add(task)
            addTaskToView(task)
        }
    }

    private fun addTaskToView(task: Task) {
        val taskLayout = LinearLayout(requireContext())
        taskLayout.orientation = LinearLayout.HORIZONTAL

        val checkBox = CheckBox(requireContext())
        val editText = EditText(requireContext())
        editText.hint = "Enter task"
        editText.setText(task.text)
        editText.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )

        checkBox.isChecked = task.isChecked
        if (task.isChecked) {
            editText.paintFlags = editText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            editText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        } else {
            editText.paintFlags = editText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            editText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            task.isChecked = isChecked
            if (isChecked) {
                editText.paintFlags = editText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                editText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                Toast.makeText(requireContext(), "참 잘했어요!", Toast.LENGTH_SHORT).show()
            } else {
                editText.paintFlags = editText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                editText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                Toast.makeText(requireContext(), "깨비", Toast.LENGTH_SHORT).show()
            }
            saveTasks()
        }

        editText.setOnLongClickListener {
            showDeleteConfirmationDialog(task, taskLayout)
            true
        }

        taskLayout.addView(checkBox)
        taskLayout.addView(editText)
        binding.tasksContainer.addView(taskLayout)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


