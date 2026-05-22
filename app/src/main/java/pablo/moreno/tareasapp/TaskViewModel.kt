package pablo.moreno.tareasapp

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    private val dao: TaskDao
) : ViewModel() {

    // Exponemos la lista de tareas como StateFlow.
    // stateIn convierte el Flow del DAO en un StateFlow
    // que Compose puede observar fácilmente.
    val tasks: StateFlow<List<TaskEntity>> = dao
        .getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            dao.insert(TaskEntity(title = title.trim()))
        }
    }

    fun toggleCompleted(task: TaskEntity) {
        viewModelScope.launch {
            dao.update(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            dao.delete(task)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as Application
                val dao = AppDataBase
                    .getInstance(application)
                    .taskDao()
                TaskViewModel(dao)
            }
        }
    }
}