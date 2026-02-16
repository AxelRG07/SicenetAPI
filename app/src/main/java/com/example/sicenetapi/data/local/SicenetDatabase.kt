package com.example.sicenetapi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Si agregas más tablas en el futuro (Kardex, Calificaciones),
// las añades al arreglo de entities y subes la versión.
@Database(entities = [AlumnoEntity::class], version = 1, exportSchema = false)
abstract class SicenetDatabase : RoomDatabase() {

    // Define qué DAOs están disponibles
    abstract fun alumnoDao(): AlumnoDao

    companion object {
        @Volatile
        private var INSTANCE: SicenetDatabase? = null

        // Patrón Singleton para asegurarnos de que solo exista UNA conexión
        // a la base de datos en toda la aplicación.
        fun getDatabase(context: Context): SicenetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SicenetDatabase::class.java,
                    "sicenet_offline_db"
                )
                    // Opcional para desarrollo: destruye y recrea la DB si cambias la versión
                    .fallbackToDestructiveMigration(false)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}