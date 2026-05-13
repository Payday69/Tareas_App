package pablo.moreno.tareasapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

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

        fun getInstance(
            context: Context
        ): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "tasks_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}