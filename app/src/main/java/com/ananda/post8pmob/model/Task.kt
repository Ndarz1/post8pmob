package com.ananda.post8pmob.model

import com.google.firebase.database.PropertyName

data class Task(
	val id: String = "",
	val title: String = "",
	val description: String = "",
	val deadline: String = "",
	
	
	@get:PropertyName("isCompleted")
	val isCompleted: Boolean = false
)