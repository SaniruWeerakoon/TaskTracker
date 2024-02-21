package com.example.tasktracker.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasktracker.R
import com.example.tasktracker.databinding.FragmentHomeBinding
import com.example.tasktracker.utils.TaskAdapter
import com.example.tasktracker.utils.TaskData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class HomeFragment : Fragment(), TaskAdapter.TaskAdapterInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: TaskAdapter
    private lateinit var authId: String
    private lateinit var taskList: MutableList<TaskData>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        getDataFromFirebase()
        registerEvents()
    }


    private fun registerEvents() {
        binding.addTaskBtn.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_addTaskFragment)
        }
        binding.btnSettings.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_settingsFragment)
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()

//        val database = FirebaseDatabase.getInstance()
//        database.setPersistenceEnabled(true)
        databaseRef =
            Firebase.database.reference.child("Tasks").child(auth.currentUser?.uid.toString())

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        taskList = mutableListOf()
        adapter = TaskAdapter(taskList)
        adapter.setListener(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getDataFromFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()
                for (taskSnapshot in snapshot.children) {
                    // Extracting the task string from the snapshot
                    val taskString = taskSnapshot.child("task").value?.toString()

                    // Creating a new TaskData object with only the task string
                    if (!taskString.isNullOrBlank()) {
                        val task = TaskData(taskSnapshot.key ?: "", taskString)
                        taskList.add(task)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                databaseRef.keepSynced(true)
                databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        taskList.clear()
                        for (taskSnapshot in snapshot.children) {
                            val taskString = taskSnapshot.child("task").value?.toString()
                            if (!taskString.isNullOrBlank()) {
                                val task = TaskData(taskSnapshot.key ?: "", taskString)
                                taskList.add(task)
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()

                    }
                })
            }
        })
    }

    override fun onDeleteItemClicked(toDoData: TaskData, position: Int) {
        databaseRef.child(toDoData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onTaskCompleteStatusChanged(toDoData: TaskData, position: Int, complete: Boolean) {
        Log.d(tag, "onTaskCompleteStatusChanged - TaskData: $toDoData, Complete: $complete")
        // Update the task complete status in the database
        databaseRef.child(toDoData.taskId).child("complete").setValue(complete)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (complete) {
                        Toast.makeText(context, "Task completed", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Task marked incomplete", Toast.LENGTH_SHORT).show()
                    }
                    taskList[position].complete = complete
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

}