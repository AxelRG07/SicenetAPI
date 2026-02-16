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
}
