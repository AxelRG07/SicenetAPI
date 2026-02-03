package com.example.sicenetapi.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
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

object RetrofitClient {
    private const val BASE_URL = "https://sicenet.surguanajuato.tecnm.mx/"

    val api: SicenetApi by lazy {
        val client = OkHttpClient.Builder()
            .cookieJar(SessionCookieJar())
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(SicenetApi::class.java)
    }
}