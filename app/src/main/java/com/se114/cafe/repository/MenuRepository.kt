package com.se114.cafe.repository

import com.se114.cafe.network.RetrofitClient
import com.se114.cafe.network.api.MenuApi

import com.se114.cafe.network.api.MenuSnapshot
import com.se114.cafe.network.api.MenuAvailability
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

data class MenuRead<T>(val body: T, val notModified: Boolean)

private class ConditionalGet<T> {
    private val lock = Mutex()
    private var tag: String? = null
    private var body: T? = null

    suspend fun read(fetch: suspend (String?) -> Response<T>): MenuRead<T> = lock.withLock {
        val response = fetch(tag)
        if (response.code() == 304) {
            return@withLock MenuRead(body ?: throw IOException("Missing cached menu"), notModified = true)
        }
        if (!response.isSuccessful) throw HttpException(response)
        val result = response.body() ?: throw IOException("Empty menu response")
        body = result
        tag = response.headers()["ETag"]
        MenuRead(result, notModified = false)
    }
}

class MenuRepository(private val api: MenuApi = RetrofitClient.retrofit.create(MenuApi::class.java)) {
    private val snapshotCache = ConditionalGet<MenuSnapshot>()
    private val availabilityCache = ConditionalGet<List<MenuAvailability>>()

    suspend fun snapshot() = snapshotCache.read(api::snapshot)
    suspend fun availability() = availabilityCache.read(api::availability).body

    suspend fun getCategories() = api.getCategories()

    suspend fun getProducts() = api.getProducts()
}
