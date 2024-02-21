package com.example.tasktracker.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.tasktracker.R
import com.example.tasktracker.databinding.FragmentAddTaskBinding
import com.example.tasktracker.utils.TaskData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class AddTaskFragment : Fragment() {
    private lateinit var binding: FragmentAddTaskBinding
    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
    }

    private fun registerEvents() {
        binding.btnNavBack.setOnClickListener {
            navController.navigate(R.id.action_addTaskFragment_to_homeFragment)
        }


        binding.btnPickContact.setOnClickListener {
            checkAndRequestContactsPermission()
        }

        binding.btnSaveTask.setOnClickListener {
            binding.pbAddTask.visibility = View.VISIBLE
            binding.btnSaveTask.isEnabled = false
            val task = binding.etTask.text.toString()
            val description = binding.etDescription.text.toString()
            if (task.isNotEmpty()) {
                saveTask(task, description)
            } else {
                Toast.makeText(context, "Please type some task", Toast.LENGTH_SHORT).show()
                binding.btnSaveTask.isEnabled = true
                binding.pbAddTask.visibility = View.GONE
            }
        }
    }


    private fun saveTask(task: String, description: String) {
        val taskId = databaseRef.push().key
        val newTask = TaskData(taskId!!, task, false, description)
        databaseRef.child(taskId).setValue(newTask).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Task added successfully", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.action_addTaskFragment_to_homeFragment)
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            binding.btnSaveTask.isEnabled = true
            binding.pbAddTask.visibility = View.GONE
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef =
            Firebase.database.reference.child("Tasks").child(auth.currentUser?.uid.toString())
    }

    private fun checkAndRequestContactsPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) -> {
                pickContact()
            }

            else -> {
                requestContactsPermission()
            }
        }
    }

    private val contactPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickContact()) { contactUri ->
            contactUri?.let {
                retrieveContactDetails(it)
            }
        }

    private val contactsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pickContact()
            } else {
                showToast("Permission denied. Cannot access contacts")
            }
        }

    private fun pickContact() {
        contactPickerLauncher.launch(null)
    }

    private fun requestContactsPermission() {
        contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }

    private fun retrieveContactDetails(contactUri: android.net.Uri) {
        val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

        requireActivity().contentResolver.query(contactUri, projection, null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val displayNameIndex =
                        cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        val displayName = cursor.getString(displayNameIndex)
                        binding.etTask.setText(getString(R.string.call_task, displayName))
                        showToast("Selected contact: $displayName")
                    } else {
                        showToast("Display name column not found")
                    }
                } else {
                    showToast("Cursor is empty")
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}