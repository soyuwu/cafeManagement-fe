package com.se114.cafe.repository

import com.google.gson.JsonParser
import com.se114.cafe.network.api.CategoryApi
import com.se114.cafe.network.api.MenuApi
import com.se114.cafe.network.api.ProductApi
import com.se114.cafe.network.dto.CategoryRequest
import com.se114.cafe.network.dto.ProductRequest
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CatalogRepositoryTest {
    private val category = """{"id":3,"name":"Coffee","sortOrder":1,"isActive":true,"createdAt":"2026-11-22T10:00:00"}"""
    private val product = """{"id":7,"categoryId":3,"name":"Latte","isActive":true,"isAvailable":false,"createdAt":"2026-11-22T10:00:00","updatedAt":"2026-11-22T11:00:00"}"""
    private val calls = mutableListOf<String>()
    private val bodies = mutableListOf<String>()
    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        val request = chain.request()
        calls += "${request.method} ${request.url.encodedPath}"
        request.body?.let { body -> bodies += Buffer().apply { body.writeTo(this) }.readUtf8() }
        val item = if (request.url.encodedPath.endsWith("categories")) category else product
        val json = if (request.method == "GET") "[$item]" else item
        Response.Builder().request(request).protocol(Protocol.HTTP_1_1)
            .code(if (request.method == "POST") 201 else 200).message("OK")
            .body(json.toResponseBody("application/json".toMediaType())).build()
    }.build()
    private val retrofit = Retrofit.Builder().baseUrl("http://localhost/")
        .client(client).addConverterFactory(GsonConverterFactory.create()).build()

    @Test fun menu_readsCustomerEndpoints() = runBlocking {
        val repo = MenuRepository(retrofit.create(MenuApi::class.java))
        assertEquals("Coffee", repo.getCategories().single().name)
        assertEquals(3L, repo.getProducts().single().categoryId)
        assertEquals(listOf("GET /api/menu/categories", "GET /api/menu/products"), calls)
    }

    @Test fun categories_listAndCreate() = runBlocking {
        val repo = CategoryRepository(retrofit.create(CategoryApi::class.java))
        assertTrue(repo.getCategories().single().isActive)
        assertEquals(3L, repo.createCategory(CategoryRequest("Coffee", 1)).id)
        assertEquals(listOf("GET /api/categories", "POST /api/categories"), calls)
        assertEquals(JsonParser.parseString("""{"name":"Coffee","sortOrder":1,"isActive":true}"""),
            JsonParser.parseString(bodies.single()))
    }

    @Test fun products_listCreateReplaceAndChangeAvailability() = runBlocking {
        val repo = ProductRepository(retrofit.create(ProductApi::class.java))
        val request = ProductRequest(3, "Latte", "Milk coffee", "https://example.com/latte.jpg")
        assertFalse(repo.getProducts().single().isAvailable)
        assertEquals(7L, repo.createProduct(request).id)
        repo.updateProduct(7, request.copy(isActive = false))
        assertFalse(repo.updateAvailability(7, false).isAvailable)
        assertEquals(listOf("GET /api/products", "POST /api/products", "PUT /api/products/7",
            "PATCH /api/products/7/availability"), calls)
        val created = JsonParser.parseString(bodies[0]).asJsonObject
        assertEquals(3L, created["categoryId"].asLong)
        assertEquals("Milk coffee", created["description"].asString)
        assertTrue(created["isAvailable"].asBoolean)
        assertFalse(JsonParser.parseString(bodies[1]).asJsonObject["isActive"].asBoolean)
        assertEquals(JsonParser.parseString("""{"isAvailable":false}"""), JsonParser.parseString(bodies[2]))
    }
}
