package com.example.sicenetapi.network

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class SessionCookieJar : CookieJar {
    private val cookieStore = HashMap<String, Cookie>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        for (cookie in cookies) {
            if (!cookie.name.contains("ANONYMOUS", ignoreCase = true)) {
                cookieStore[cookie.name] = cookie
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore.values.toList()
    }

    fun clearSession() {
        cookieStore.clear()
    }
}