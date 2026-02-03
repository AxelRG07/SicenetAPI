package com.example.sicenetapi.network

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class SessionCookieJar : CookieJar {
    private val cookieStore = mutableListOf<Cookie>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore.clear()
        cookieStore.addAll(cookies)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore
    }
}