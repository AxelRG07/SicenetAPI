package com.example.sicenetapi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AlumnoEntity::class, CargaAcademicaEntity::class],
    version = 2,
    exportSchema = false
)
abstract class SicenetDatabase : RoomDatabase() {

    abstract fun alumnoDao(): AlumnoDao
    abstract fun cargaAcademicaDao(): CargaAcademicaDao

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
                    .fallbackToDestructiveMigration(false)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}