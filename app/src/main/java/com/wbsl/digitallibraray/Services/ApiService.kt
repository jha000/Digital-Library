package com.wbsl.digitallibraray.Services

import com.wbsl.digitallibraray.Data.Book
import com.wbsl.digitallibraray.Data.Catalogue
import com.wbsl.digitallibraray.Data.Category
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("catalogue")
    fun getCatalogues(): Call<List<Catalogue>>

    @GET("category")
    fun getCategory(): Call<List<Category>>

    @GET("books")
    fun getBooks(): Call<List<Book>>

    @GET("books/search")
    suspend fun getBooks(@Query("title") title: String): Response<List<Book>>

}
