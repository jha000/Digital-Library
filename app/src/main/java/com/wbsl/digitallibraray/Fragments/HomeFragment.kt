package com.wbsl.digitallibrary.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smarteist.autoimageslider.SliderView
import com.wbsl.digitallibraray.Adapter.BookAdapter
import com.wbsl.digitallibraray.Adapter.CatalogueAdapter
import com.wbsl.digitallibraray.Adapter.SliderAdapter
import com.wbsl.digitallibraray.Data.Book
import com.wbsl.digitallibraray.Data.Catalogue
import com.wbsl.digitallibraray.Data.SliderData
import com.wbsl.digitallibraray.R
import com.wbsl.digitallibraray.Activities.SearchActivity
import com.wbsl.digitallibraray.Services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private var url1 = "https://firebasestorage.googleapis.com/v0/b/nodenote-63309.appspot.com/o/dig_lib%2Fslider%2Fpic1.png?alt=media&token=917143cc-9945-481a-ad30-580a58366d35"
    private var url2 = "https://firebasestorage.googleapis.com/v0/b/nodenote-63309.appspot.com/o/dig_lib%2Fslider%2Fpic2.png?alt=media&token=c14a820d-c61c-4dc5-9983-e5ad92933251"
    private var url3 = "https://firebasestorage.googleapis.com/v0/b/nodenote-63309.appspot.com/o/dig_lib%2Fslider%2Fpic3.png?alt=media&token=27f316d3-0db1-4353-a92a-bfc1eed8a034"
    private var url4 = "https://firebasestorage.googleapis.com/v0/b/nodenote-63309.appspot.com/o/dig_lib%2Fslider%2Fpic4.png?alt=media&token=af432cc0-2792-4424-b69e-c05f487dae99"
    private var url5 = "https://firebasestorage.googleapis.com/v0/b/nodenote-63309.appspot.com/o/dig_lib%2Fslider%2Fpic5.png?alt=media&token=23963117-6ef1-4060-a383-1141fa13b7fe"
    private var url6 = "https://firebasestorage.googleapis.com/v0/b/nodenote-63309.appspot.com/o/dig_lib%2Fslider%2Fpic6.png?alt=media&token=a861d6c7-5937-4a13-87e9-8fbbaf25cd6c"

    private var formattedText: String = "0"

    private lateinit var progressBar: LinearLayout
    private lateinit var progressBar2: LinearLayout
    private lateinit var show: LinearLayout

    lateinit var call: Call<List<Catalogue>>
    lateinit var booksCall: Call<List<Book>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val sliderDataArrayList: ArrayList<SliderData> = ArrayList()
        sliderDataArrayList.add(SliderData(url1))
        sliderDataArrayList.add(SliderData(url2))
        sliderDataArrayList.add(SliderData(url3))
        sliderDataArrayList.add(SliderData(url4))
        sliderDataArrayList.add(SliderData(url5))
        sliderDataArrayList.add(SliderData(url6))

        val sliderView: SliderView = view.findViewById(R.id.slider)
        val sliderAdapter = SliderAdapter(requireContext(), sliderDataArrayList)
        sliderView.setSliderAdapter(sliderAdapter)
        sliderView.isAutoCycle = true
        sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
        sliderView.scrollTimeInSec = 5
        sliderView.startAutoCycle()

        // Initialize RecyclerView
        val catalogueRecyclerView: RecyclerView = view.findViewById(R.id.catalogueRecyclerView)
        val booksRecyclerView: RecyclerView = view.findViewById(R.id.booksRecyclerView)

        progressBar = view.findViewById(R.id.progressBar)
        progressBar2 = view.findViewById(R.id.progressBar2)
        show = view.findViewById(R.id.show)

        val searchView: CardView = view.findViewById(R.id.searchView)

        searchView.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)
        }

        // Retrieve catalogues from the API
        retrieveCataloguesFromAPI()
        // Retrieve books from the API
        retrieveBooksFromAPI()

        // Create and set the adapter
        val adapter = CatalogueAdapter(emptyList()) { selectedCatalogue ->
            // Handle the selected catalogue item click
            progressBar.visibility = View.VISIBLE
            show.visibility = View.GONE
            formattedText = "${selectedCatalogue.catalogue_id}"

            // Update the books RecyclerView with loading indicator
            retrieveBooksFromAPI()
        }
        catalogueRecyclerView.adapter = adapter

        val booksAdapter = BookAdapter(requireContext(), emptyList())
        booksRecyclerView.adapter = booksAdapter

        // Set RecyclerView to horizontal orientation
        catalogueRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        booksRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        return view
    }

    override fun onResume() {
        super.onResume()

        formattedText ="0"
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
        call = apiService.getCatalogues()

        call.enqueue(object : Callback<List<Catalogue>> {
            override fun onResponse(call: Call<List<Catalogue>>, response: Response<List<Catalogue>>) {
                if (response.isSuccessful) {
                    val catalogues = response.body() ?: emptyList()
                    // Update the RecyclerView with the retrieved catalogues
                    updateRecyclerView(catalogues)
                }
            }

            override fun onFailure(call: Call<List<Catalogue>>, t: Throwable) {
                // Handle failure
            }
        })
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
                    progressBar.visibility = View.GONE
                    show.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun updateRecyclerView(catalogues: List<Catalogue>) {
        // Check if the fragment is in the correct state
        if (!isAdded || isDetached || view == null) {
            return
        }

        val catalogueRecyclerView: RecyclerView = requireView().findViewById(R.id.catalogueRecyclerView)

        // Create and set the adapter
        val adapter = CatalogueAdapter(catalogues) { selectedCatalogue ->
            // Handle the selected catalogue item click
            progressBar.visibility = View.VISIBLE
            show.visibility = View.GONE
            formattedText = "${selectedCatalogue.catalogue_id}"

            // Update the books RecyclerView with loading indicator
            retrieveBooksFromAPI()
        }
        catalogueRecyclerView.adapter = adapter
    }


    private fun updateBooksRecyclerView(books: List<Book>) {

        if (!isAdded || isDetached || view == null) {
            return
        }

        val booksRecyclerView: RecyclerView = requireView().findViewById(R.id.booksRecyclerView)

        // Filter books based on formattedText value
        val filteredBooks = if (formattedText == "0") {
            // If formattedText is "0", show all books
            books
        } else {
            // Otherwise, filter books by category_id matching formattedText
            books.filter { it.category_id == formattedText.toInt() }
        }

        val booksAdapter = BookAdapter(requireContext(), filteredBooks)
        booksRecyclerView.adapter = booksAdapter
    }
}
