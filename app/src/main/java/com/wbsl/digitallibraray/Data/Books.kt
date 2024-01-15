package com.wbsl.digitallibraray.Data

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val description: String,
    val subject: String,
    val publisher: String,
    val total_pages: Int,
    val media: String,
    val link: String,
    val publication_year: String,
    val category_id: Int
)
