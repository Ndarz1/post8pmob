package com.ananda.post8pmob.ui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ananda.post8pmob.model.Task
import com.ananda.post8pmob.viewmodel.TaskViewModel
import java.util.Calendar

@Composable
fun TaskScreen() {
	val viewModel: TaskViewModel = viewModel()
	val tasks by viewModel.tasks.collectAsState()
	var showDialog by remember { mutableStateOf(false) }
	var currentTask by remember { mutableStateOf<Task?>(null) }
	
	Scaffold(
		floatingActionButton = {
			FloatingActionButton(
				onClick = {
					currentTask = null
					showDialog = true
				},
				containerColor = Color(0xFF5B4DFF),
				contentColor = Color.White
			) {
				Icon(Icons.Default.Add, contentDescription = "Tambah")
			}
		}
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.padding(20.dp)
		) {
			Text(
				text = "My Tasks",
				fontSize = 28.sp,
				fontWeight = FontWeight.Bold,
				color = Color(0xFF1F2937)
			)
			Text(
				text = "HARI INI",
				fontSize = 12.sp,
				color = Color.Gray,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
			)
			
			LazyColumn(
				verticalArrangement = Arrangement.spacedBy(12.dp)
			) {
				items(tasks) { task ->
					TaskItem(
						task = task,
						onToggle = { viewModel.toggleComplete(task) },
						onDelete = { viewModel.deleteTask(task.id) },
						onEdit = {
							currentTask = task
							showDialog = true
						}
					)
				}
			}
		}
		
		if (showDialog) {
			TaskInputData(
				task = currentTask,
				onDismiss = { showDialog = false },
				onSave = { id, title, desc, date ->
					viewModel.saveTask(id, title, desc, date, currentTask?.isCompleted ?: false)
					showDialog = false
				}
			)
		}
	}
}

@Composable
fun TaskItem(
	task: Task,
	onToggle: () -> Unit,
	onDelete: () -> Unit,
	onEdit: () -> Unit
) {
	val cardColor = if (task.isCompleted) Color(0xFFF3F4F6) else Color.White
	val textColor = if (task.isCompleted) Color.Gray else Color.Black
	val iconColor = if (task.isCompleted) Color.Gray else Color(0xFF5B4DFF)
	val dateBgColor = if (task.isCompleted) Color(0xFFE5E7EB) else Color(0xFFEEF2FF)
	val dateTextColor = if (task.isCompleted) Color.Gray else Color(0xFF5B4DFF)
	
	Card(
		colors = CardDefaults.cardColors(containerColor = cardColor),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
		shape = RoundedCornerShape(12.dp),
		modifier = Modifier
			.fillMaxWidth()
	) {
		Row(
			modifier = Modifier
				.padding(16.dp)
				.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically
		) {
			IconButton(onClick = { onToggle() }) {
				Icon(
					imageVector = if (task.isCompleted) Icons.Outlined.CheckBox else Icons.Outlined.CheckBoxOutlineBlank,
					contentDescription = "Status",
					tint = iconColor
				)
			}
			Column(
				modifier = Modifier
					.weight(1f)
					.padding(start = 8.dp)
					.clickable { onEdit() }
			) {
				Text(
					text = task.title,
					fontWeight = FontWeight.Bold,
					fontSize = 16.sp,
					color = textColor
				)
				if (task.description.isNotEmpty()) {
					Text(
						text = task.description,
						fontSize = 12.sp,
						color = Color.Gray
					)
				}
				Spacer(modifier = Modifier.height(8.dp))
				Surface(
					color = dateBgColor,
					shape = RoundedCornerShape(6.dp)
				) {
					Text(
						text = task.deadline,
						modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
						color = dateTextColor,
						fontSize = 12.sp,
						fontWeight = FontWeight.Bold
					)
				}
			}
			IconButton(onClick = onDelete) {
				Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFEF4444))
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskInputData(
	task: Task?,
	onDismiss: () -> Unit,
	onSave: (String, String, String, String) -> Unit
) {
	var title by remember { mutableStateOf(task?.title ?: "") }
	var desc by remember { mutableStateOf(task?.description ?: "") }
	var deadline by remember { mutableStateOf(task?.deadline ?: "") }
	
	val context = LocalContext.current
	val calendar = Calendar.getInstance()
	
	val datePickerDialog = android.app.DatePickerDialog(
		context,
		{ _, year, month, dayOfMonth ->
			deadline = "$dayOfMonth/${month + 1}/$year"
		},
		calendar.get(Calendar.YEAR),
		calendar.get(Calendar.MONTH),
		calendar.get(Calendar.DAY_OF_MONTH)
	)
	
	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text(text = if (task == null) "Tambah Tugas Baru" else "Edit Tugas") },
		text = {
			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				OutlinedTextField(
					value = title,
					onValueChange = { title = it },
					label = { Text("Judul Tugas") },
					singleLine = true
				)
				OutlinedTextField(
					value = desc,
					onValueChange = { desc = it },
					label = { Text("Deskripsi (Opsional)") }
				)
				OutlinedTextField(
					value = deadline,
					onValueChange = { },
					label = { Text("Deadline") },
					readOnly = true,
					trailingIcon = {
						IconButton(onClick = { datePickerDialog.show() }) {
							Icon(Icons.Default.CalendarToday, contentDescription = "Pilih Tanggal")
						}
					},
					singleLine = true,
					modifier = Modifier.clickable { datePickerDialog.show() }
				)
			}
		},
		confirmButton = {
			TextButton(onClick = { onSave(task?.id ?: "", title, desc, deadline) }) {
				Text("Simpan")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text("Batal")
			}
		}
	)
}