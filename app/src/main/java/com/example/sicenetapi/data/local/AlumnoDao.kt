package com.example.sicenetapi.data.local
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlumnoDao {

    // OnConflictStrategy.REPLACE es magia pura: si el alumno ya existe,
    // actualiza sus datos con los nuevos. Si no existe, lo inserta. (Upsert)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerfil(alumno: AlumnoEntity)

    // Usamos Flow (Flujo reactivo). Cada vez que la base de datos cambie,
    // Flow avisará automáticamente a tu UI para que se redibuje.
    @Query("SELECT * FROM perfil_alumno WHERE matricula = :matricula")
    fun getPerfil(matricula: String): Flow<AlumnoEntity?>

    // Opcional: Para saber si ya tenemos un alumno logueado
    @Query("SELECT * FROM perfil_alumno LIMIT 1")
    fun getPerfilActual(): Flow<AlumnoEntity?>

    @Query("DELETE FROM perfil_alumno")
    suspend fun borrarSesion()
}