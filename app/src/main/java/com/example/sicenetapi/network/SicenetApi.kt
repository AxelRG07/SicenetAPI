package com.example.sicenetapi.network

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SicenetApi {
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/accesoLogin\""
    )
    @POST("/ws/wsalumnos.asmx")
    suspend fun login(@Body requestBody: String): retrofit2.Response<String>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getAlumnoAcademicoWithLineamiento\""
    )
    @POST("/ws/wsalumnos.asmx")
    suspend fun getProfile(@Body requestBody: String): retrofit2.Response<String>

    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/getCargaAcademicaByAlumno\"")
    @POST("/ws/wsalumnos.asmx")
    suspend fun getCargaAcademica(@Body requestBody: String): retrofit2.Response<String>

    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/getAllKardexConPromedioByAlumno\"")
    @POST("/ws/wsalumnos.asmx")
    suspend fun getKardex(@Body requestBody: String): retrofit2.Response<String>

    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/getAllCalifFinalByAlumnos\"")
    @POST("/ws/wsalumnos.asmx")
    suspend fun getCalifFinal(@Body requestBody: String): retrofit2.Response<String>

    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/getCalifUnidadesByAlumno\"")
    @POST("/ws/wsalumnos.asmx")
    suspend fun getCalifUnidades(@Body requestBody: String): retrofit2.Response<String>
}
