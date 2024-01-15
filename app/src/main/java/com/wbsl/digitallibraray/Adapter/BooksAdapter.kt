package com.wbsl.digitallibraray.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wbsl.digitallibraray.Data.Book
import com.wbsl.digitallibraray.Activities.OpenBook
import com.wbsl.digitallibraray.R

class BookAdapter(private val context: Context, private val books: List<Book>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.titleTextView.text = book.title
        Picasso.get().load(book.media).into(holder.bookImage)
        holder.yearTextView.text = book.publication_year
        holder.authorTextView.text = book.author

        holder.itemView.setOnClickListener {
            // When clicked, open the OpenBookActivity and pass the data
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

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.bookTitle)
        val bookImage: ImageView = itemView.findViewById(R.id.mediaImage)
        val yearTextView: TextView = itemView.findViewById(R.id.bookYear)
        val authorTextView: TextView = itemView.findViewById(R.id.Author)
    }
}
