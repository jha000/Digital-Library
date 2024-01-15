package com.wbsl.digitallibraray.Activities

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wbsl.digitallibraray.Adapter.SearchAdapter
import com.wbsl.digitallibraray.Data.Book
import com.wbsl.digitallibraray.R
import com.wbsl.digitallibraray.Services.ApiService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val books = mutableListOf<Book>()
    private lateinit var searchAdapter: SearchAdapter
    private var titleQuery: String = ""

    private lateinit var showAnim: LinearLayout
    private lateinit var showAnim1: LinearLayout

    private var searchJob: Job? = null

    // Retrofit instance
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://nodenote.cyclic.cloud/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = ContextCompat.getColor(this, R.color.orange)

        setContentView(R.layout.activity_search)

        // Initialize RecyclerView
        val searchRecyclerView: RecyclerView = findViewById(R.id.searchRecyclerView)
        val searchView: SearchView = findViewById(R.id.searchView)

        showAnim= findViewById(R.id.showAnim)
        showAnim1= findViewById(R.id.showAnim1)

        searchView.requestFocus()

        searchAdapter = SearchAdapter(this, books)
        searchRecyclerView.adapter = searchAdapter
        searchRecyclerView.layoutManager = LinearLayoutManager(this)

        // Set up a listener for the search view
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search query changes
                titleQuery = newText.orEmpty()
                performSearch()
                return true
            }
        })
    }

    private fun performSearch() {
        // Cancel the previous job if it exists
        searchJob?.cancel()

        // Start a new coroutine for the API call after a short delay
        searchJob = lifecycleScope.launch {
            delay(300) // Delay for 300 milliseconds to avoid rapid API calls while typing

            try {
                val response = apiService.getBooks(titleQuery)
                if (response.isSuccessful) {
                    // Update the books list with the retrieved data
                    books.clear()
                    books.addAll(response.body() ?: emptyList())
                    searchAdapter.notifyDataSetChanged()

                    // Show or hide the showAnim view based on the result
                    if (books.isEmpty()) {
                        showAnim.visibility = View.VISIBLE
                        showAnim1.visibility = View.GONE
                    } else {
                        showAnim.visibility = View.GONE
                        showAnim1.visibility = View.GONE
                    }
                } else {
                    // Handle API error
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

}
