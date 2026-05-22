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

    // @Before se ejecuta antes de CADA test
    // Así cada prueba arranca con la BD vacía y aislada
    @Before
    fun setUp() {

        val context = ApplicationProvider
            .getApplicationContext<Context>()

        // inMemoryDatabaseBuilder crea la
        // BD en RAM en lugar de en disco.
        // Es rápida y desaparece cuando
        // cierras la BD.
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDataBase::class.java
        )
            // En producción NUNCA hagas
            // esto, pero en tests sí
            // queremos correr en main
            // thread para simplificar.
            .allowMainThreadQueries()
            .build()

        dao = db.taskDao()
    }

    // @After se ejecuta después de CADA
    // test. Cerrar la BD libera recursos.
    @After
    fun tearDown() {
        db.close()
    }

    // Cada @Test es un caso de prueba
    // independiente. runTest crea un
    // contexto de coroutines pensado para
    // pruebas: ejecuta las suspend al
    // instante.

    @Test
    fun insertarTarea() = runTest {

        // Arrange (preparar)
        val tarea = TaskEntity(
            titulo = "Comprar pan"
        )

        // Act (ejecutar)
        dao.insert(tarea)

        // Assert (verificar)
        // .first() pide la PRIMER emisión
        // del Flow y termina.
        val tareas = dao
            .getAllTasks()
            .first()

        assertEquals(1, tareas.size)

        assertEquals(
            "Comprar pan",
            tareas[0].titulo
        )
    }

    @Test
    fun actualizarTarea() = runTest {

        // Insertamos algo primero
        dao.insert(
            TaskEntity(
                titulo = "Estudiar"
            )
        )

        val original = dao
            .getAllTasks()
            .first()
            .first()

        // Por defecto completado es false
        assertEquals(
            false,
            original.completado
        )

        // copy() crea un objeto nuevo
        // cambiando una propiedad.
        dao.update(
            original.copy(
                completado = true
            )
        )

        val actualizada = dao
            .getAllTasks()
            .first()
            .first()

        assertTrue(
            actualizada.completado
        )
    }

    @Test
    fun borrarTarea() = runTest {

        dao.insert(
            TaskEntity(
                titulo = "Algo"
            )
        )

        val tarea = dao
            .getAllTasks()
            .first()
            .first()

        dao.delete(tarea)

        val tareas = dao
            .getAllTasks()
            .first()

        assertTrue(
            tareas.isEmpty()
        )
    }
}
