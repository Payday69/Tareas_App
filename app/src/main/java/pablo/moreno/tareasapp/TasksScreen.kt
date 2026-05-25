package pablo.moreno.tareasapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TasksScreen(
    viewModel: TaskViewModel = viewModel(
        factory = TaskViewModel.Factory
    )
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val searchInput by viewModel.searchInput.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    var nuevaTareaTexto by remember { mutableStateOf("") }
    var tareaAEliminar by remember { mutableStateOf<TaskEntity?>(null) }

    tareaAEliminar?.let { tarea ->
        AlertDialog(
            onDismissRequest = { tareaAEliminar = null },
            title = { Text(stringResource(R.string.delete_confirm_title)) },
            text = { Text(stringResource(R.string.delete_confirm_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTask(tarea)
                    tareaAEliminar = null
                }) {
                    Text(stringResource(R.string.delete_confirm_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { tareaAEliminar = null }) {
                    Text(stringResource(R.string.cancel_button))
                }
            }
        )
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // ----- Titulo -----
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // ----- Barra de búsqueda -----
            SearchBar(
                searchInput = searchInput,
                onSearchInputChanged = { texto ->
                    viewModel.onSearchInputChanged(texto)
                },
                onSearchClicked = {
                    viewModel.executeSearch()
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // ----- Selector de ordenamiento -----
            SortOrderSelector(
                sortOrder = sortOrder,
                onSortOrderChanged = { viewModel.onSortOrderChanged(it) },
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // ----- Lista de tareas -----
            Box(modifier = Modifier.weight(1f)) {
                if (tasks.isEmpty()) {
                    Text(
                        text = stringResource(R.string.empty_list_message),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(
                            items = tasks,
                            key = { task -> task.id }
                        ) { task ->
                            TaskItem(
                                task = task,
                                onToggleCompleted = {
                                    viewModel.toggleCompleted(task)
                                },
                                onDelete = {
                                    tareaAEliminar = task
                                }
                            )
                        }
                    }
                }
            }

            // ----- Campo para agregar nueva tarea -----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nuevaTareaTexto,
                    onValueChange = { nuevaTareaTexto = it },
                    placeholder = {
                        Text(text = stringResource(R.string.new_task_placeholder))
                    },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        viewModel.addTask(nuevaTareaTexto)
                        nuevaTareaTexto = ""
                    }
                ) {
                    Text(text = stringResource(R.string.add_button))
                }
            }
        }
    }
}