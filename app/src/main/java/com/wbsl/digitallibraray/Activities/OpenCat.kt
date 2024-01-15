package com.wbsl.digitallibraray.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wbsl.digitallibraray.Adapter.BookAdapter
import com.wbsl.digitallibraray.Data.Book
import com.wbsl.digitallibraray.R
import com.wbsl.digitallibraray.Services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OpenCat : AppCompatActivity() {

    private var formattedText: String = "0"
    lateinit var booksCall: Call<List<Book>>

    private lateinit var progressBar2: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_cat)

        val intent = intent
        val catId = intent.getStringExtra("CAT_ID")
        val catName = intent.getStringExtra("CAT_NAME")

        val back = findViewById<ImageView>(R.id.back)

        back.setOnClickListener(View.OnClickListener {
            super.onBackPressed()
        })

        val name = findViewById<TextView>(R.id.cat_name)
        val booksRecyclerView: RecyclerView = findViewById(R.id.booksRecyclerView)
        progressBar2 = findViewById(R.id.progressBar2)

        name.text = catName
        formattedText = catId.toString()

        retrieveBooksFromAPI()


        val booksAdapter = BookAdapter(applicationContext, emptyList())
        booksRecyclerView.adapter = booksAdapter

        booksRecyclerView.layoutManager =
            GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)


    }

    private fun retrieveBooksFromAPI() {
        // Retrofit setup
        val retrofit = Retrofit.Builder()
            .baseUrl("https://nodenote.cyclic.cloud/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create the API service
        val apiService = retrofit.create(ApiService::class.java)

        // Make the API call for books
        booksCall = apiService.getBooks()

        booksCall.enqueue(object : Callback<List<Book>> {
            override fun onResponse(call: Call<List<Book>>, response: Response<List<Book>>) {
                if (response.isSuccessful) {
                    val books = response.body() ?: emptyList()
                    // Update the RecyclerView with the retrieved books
                    updateBooksRecyclerView(books)
                    progressBar2.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun updateBooksRecyclerView(books: List<Book>) {

        val booksRecyclerView: RecyclerView = findViewById(R.id.booksRecyclerView)

        // Filter books based on formattedText value
        val filteredBooks = if (formattedText == "0") {
            // If formattedText is "0", show all books
            books
        } else {
            // Otherwise, filter books by category_id matching formattedText
            books.filter { it.category_id == formattedText.toInt() }
        }

        val booksAdapter = BookAdapter(applicationContext, filteredBooks)
        booksRecyclerView.adapter = booksAdapter
    }

}