package com.wbsl.digitallibraray.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wbsl.digitallibraray.Adapter.CategoryAdapter
import com.wbsl.digitallibraray.Data.Category
import com.wbsl.digitallibraray.R
import com.wbsl.digitallibraray.Activities.SearchActivity
import com.wbsl.digitallibraray.Services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CatalogueFragment : Fragment() {

    lateinit var call: Call<List<Category>>
    private lateinit var progressBar2: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_catalogue, container, false)

        val searchView: CardView = view.findViewById(R.id.searchView)

        searchView.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)
        }

        val catalogueRecyclerView: RecyclerView = view.findViewById(R.id.catalogueRecyclerView)

        progressBar2 = view.findViewById(R.id.progressBar2)

        // Retrieve catalogues from the API
        retrieveCataloguesFromAPI()

        // Create and set the adapter
        val adapter = CategoryAdapter(requireContext(),emptyList()) { selectedCatalogue ->

        }
        catalogueRecyclerView.adapter = adapter

        catalogueRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        return view
    }

    private fun retrieveCataloguesFromAPI() {
        // Retrofit setup
        val retrofit = Retrofit.Builder()
            .baseUrl("https://nodenote.cyclic.cloud/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create the API service
        val apiService = retrofit.create(ApiService::class.java)

        // Make the API call
        call = apiService.getCategory()

        call.enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    val catalogues = response.body() ?: emptyList()
                    // Update the RecyclerView with the retrieved catalogues
                    updateRecyclerView(catalogues)
                    progressBar2.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun updateRecyclerView(catalogues: List<Category>) {
        // Check if the fragment is in the correct state
        if (!isAdded || isDetached || view == null) {
            return
        }

        val catalogueRecyclerView: RecyclerView = requireView().findViewById(R.id.catalogueRecyclerView)

        // Create and set the adapter
        val adapter = CategoryAdapter(requireContext(),catalogues) { selectedCatalogue ->
        }
        catalogueRecyclerView.adapter = adapter
    }

}