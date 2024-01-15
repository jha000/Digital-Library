package com.wbsl.digitallibraray.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wbsl.digitallibraray.Data.Book
import com.wbsl.digitallibraray.Activities.OpenBook
import com.wbsl.digitallibraray.R

class SearchAdapter(private val context: Context, private var books: List<Book>) :
    RecyclerView.Adapter<SearchAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.titleTextView.text = book.title

        holder.itemView.setOnClickListener {

            val intent = Intent(context, OpenBook::class.java)
            intent.putExtra("BOOK_TITLE", book.title)
            intent.putExtra("BOOK_MEDIA", book.media)
            intent.putExtra("BOOK_SUBJECT", book.subject)
            intent.putExtra("BOOK_YEAR", book.publication_year)
            intent.putExtra("BOOK_DESCRIPTION", book.description)
            intent.putExtra("BOOK_PAGES", book.total_pages.toString())
            intent.putExtra("BOOK_ID", book.id)
            intent.putExtra("BOOK_PUBLISHER", book.publisher)
            intent.putExtra("BOOK_AUTHOR", book.author)
            intent.putExtra("BOOK_LINK", book.link)
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return books.size
    }

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.title_search)

    }
}
