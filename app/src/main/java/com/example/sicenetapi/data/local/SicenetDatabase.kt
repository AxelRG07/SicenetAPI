package com.example.sicenetapi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AlumnoEntity::class, CargaAcademicaEntity::class, KardexEntity::class, CalifFinalEntity::class, CalifUnidadesEntity::class],
    version = 8,
    exportSchema = false
)
abstract class SicenetDatabase : RoomDatabase() {

    abstract fun alumnoDao(): AlumnoDao
    abstract fun cargaAcademicaDao(): CargaAcademicaDao
    abstract fun kardexDao(): KardexDao
    abstract fun califFinalDao(): CalifFinalDao
    abstract fun califUnidadesDao(): CalifUnidadesDao


    companion object {
        @Volatile
        private var INSTANCE: SicenetDatabase? = null


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