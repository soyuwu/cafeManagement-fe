package com.se114.cafe.repository

import com.se114.cafe.network.RetrofitClient
import com.se114.cafe.network.api.MenuApi

class MenuRepository(private val api: MenuApi = RetrofitClient.retrofit.create(MenuApi::class.java)) {
    suspend fun getCategories() = api.getCategories()

    suspend fun getProducts() = api.getProducts()
}
