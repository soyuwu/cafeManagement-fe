package com.se114.cafe.repository

import com.se114.cafe.network.ApiService

class productRepository (private val api: ApiService){
    suspend fun getHealth() = api.health()
}