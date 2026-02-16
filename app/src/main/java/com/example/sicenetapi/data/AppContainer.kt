package com.example.sicenetapi.data

import com.example.sicenetapi.data.NetworkSicenetRepository
import com.example.sicenetapi.data.SicenetRepository
import com.example.sicenetapi.network.SessionCookieJar
import com.example.sicenetapi.network.SicenetApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

interface AppContainer {
    val sicenetRepository: SicenetRepository
}

class DefaultAppContainer : AppContainer {

    private val BASE_URL = "https://sicenet.surguanajuato.tecnm.mx/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 1. Construimos el cliente OkHttp con las cookies
    private val okHttpClient = OkHttpClient.Builder()
        .cookieJar(SessionCookieJar())
        .addNetworkInterceptor(loggingInterceptor)
        .build()

    // 2. Construimos Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    // 3. Creamos el servicio de la API
    private val retrofitService: SicenetApi by lazy {
        retrofit.create(SicenetApi::class.java)
    }

    // 4. Construimos nuestro repositorio pasándole el servicio
    override val sicenetRepository: SicenetRepository by lazy {
        NetworkSicenetRepository(retrofitService)
    }
}