package com.example.tasktracker.utils

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tasktracker.databinding.TaskItemBinding

class TaskAdapter(private val list: MutableList<TaskData>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val TAG = "TaskAdapter"
    private var listener: TaskAdapterInterface? = null
    fun setListener(listener: TaskAdapterInterface) {
        this.listener = listener
    }

    inner class TaskViewHolder(val binding: TaskItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding =
            TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.todoTask.text = this.task
                binding.completeCheckBox.isChecked = this.complete

                // Remove the previous OnCheckedChangeListener to avoid duplicates
                binding.completeCheckBox.setOnCheckedChangeListener(null)
                 if (this.complete) {
                     binding.todoTask.paintFlags = binding.todoTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                 } else {
                     binding.todoTask.paintFlags = binding.todoTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                 }
                binding.completeCheckBox.setOnClickListener {
                    val isChecked = binding.completeCheckBox.isChecked
                    list[position].complete = isChecked

                    notifyItemChanged(position)
                    listener?.onTaskCompleteStatusChanged(list[position], position, isChecked)
//                    Log.d(TAG, "Inside listener for checkbox: $isChecked")
                }

                Log.d(TAG, "onBindViewHolder: $this")

                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteItemClicked(this, position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface TaskAdapterInterface {
        fun onDeleteItemClicked(toDoData: TaskData, position: Int)
        fun onTaskCompleteStatusChanged(toDoData: TaskData, position: Int, complete: Boolean)
    }

}