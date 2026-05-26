package pablo.moreno.tareasapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var db: AppDataBase
    private lateinit var dao: TaskDao

    @Before
    fun setUp() {
        val context = ApplicationProvider
            .getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDataBase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = db.taskDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertarTarea() = runTest {
        val tarea = TaskEntity(
            title = "Comprar pan"
        )
        dao.insert(tarea)
        val tareas = dao.getAllTasks().first()
        assertEquals(1, tareas.size)
        assertEquals("Comprar pan", tareas[0].title)
    }

    @Test
    fun actualizarTarea() = runTest {
        dao.insert(TaskEntity(title = "Estudiar"))
        val original = dao.getAllTasks().first().first()
        assertEquals(false, original.isCompleted)
        dao.update(original.copy(isCompleted = true))
        val actualizada = dao.getAllTasks().first().first()
        assertTrue(actualizada.isCompleted)
    }

    @Test
    fun borrarTarea() = runTest {
        dao.insert(TaskEntity(title = "Algo"))
        val tarea = dao.getAllTasks().first().first()
        dao.delete(tarea)
        val tareas = dao.getAllTasks().first()
        assertTrue(tareas.isEmpty())
    }
}