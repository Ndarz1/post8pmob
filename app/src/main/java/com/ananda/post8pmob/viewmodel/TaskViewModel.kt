package com.ananda.post8pmob.viewmodel

import androidx.lifecycle.ViewModel
import com.ananda.post8pmob.model.Task

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TaskViewModel : ViewModel() {
	private val db = FirebaseDatabase.getInstance()
	private val tasksRef = db.getReference("tasks")
	
	private val _tasks = MutableStateFlow<List<Task>>(emptyList())
	val tasks: StateFlow<List<Task>> = _tasks
	
	init {
		fetchTasks()
	}
	
	private fun fetchTasks() {
		tasksRef.addValueEventListener(object : ValueEventListener {
			override fun onDataChange(snapshot: DataSnapshot) {
				val taskList = mutableListOf<Task>()
				for (childSnapshot in snapshot.children) {
					val task = childSnapshot.getValue(Task::class.java)
					if (task != null) {
						taskList.add(task)
					}
				}
				_tasks.value = taskList
			}
			
			override fun onCancelled(error: DatabaseError) {
			}
		})
	}
	
	fun saveTask(id: String, title: String, desc: String, date: String, isCompleted: Boolean) {
		if (id.isEmpty()) {
			val newId = tasksRef.push().key ?: return
			val task = Task(newId, title, desc, date, isCompleted)
			tasksRef.child(newId).setValue(task)
		} else {
			val task = Task(id, title, desc, date, isCompleted)
			tasksRef.child(id).setValue(task)
		}
	}
	
	fun deleteTask(taskId: String) {
		tasksRef.child(taskId).removeValue()
	}
	
	fun toggleComplete(task: Task) {
		tasksRef.child(task.id).child("isCompleted").setValue(!task.isCompleted)
	}
}