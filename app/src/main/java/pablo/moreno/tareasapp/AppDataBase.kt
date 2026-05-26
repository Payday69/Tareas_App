package pablo.moreno.tareasapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null

        private val TAREAS_INICIALES = listOf(
            TaskEntity(
                title = "Pantalla de selección de gráficas para publicar",
                isCompleted = true
            ),
            TaskEntity(
                title = "Pantalla de carga de archivos",
                isCompleted = true
            ),
            TaskEntity(
                title = "Bloquear capturas de pantalla y grabación",
                isCompleted = true
            ),
            TaskEntity(
                title = "Mover botón de Generate Charts hacia abajo",
                isCompleted = false
            ),
            TaskEntity(
                title = "Corregir inconsistencias en Generate Charts después de cargar gráficas",
                isCompleted = false
            ),
            TaskEntity(
                title = "Mostrar mensaje de confirmación como Toast en Generate Charts",
                isCompleted = false
            ),
            TaskEntity(
                title = "Reducir espaciado excesivo en Login",
                isCompleted = false
            ),
            TaskEntity(
                title = "Ajustar padding del logo en Login",
                isCompleted = false
            )
        )

        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "tasks_db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).taskDao()
                                TAREAS_INICIALES.forEach { tarea ->
                                    dao.insert(tarea)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}