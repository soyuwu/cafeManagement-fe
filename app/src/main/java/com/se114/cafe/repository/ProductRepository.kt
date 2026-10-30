package com.se114.cafe.repository

import com.se114.cafe.network.api.ProductApi

class ProductRepository (private val api: ProductApi){
    suspend fun getHealth() = api.health()
}