package com.wbsl.digitallibraray.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.squareup.picasso.Picasso
import com.wbsl.digitallibraray.R

class OpenBook : AppCompatActivity() {

    lateinit var pdfView: PDFView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_book)

        val back = findViewById<ImageView>(R.id.back)

        back.setOnClickListener(View.OnClickListener {
            super.onBackPressed()
        })

        // Retrieve data from intent
        val intent = intent
        val bookTitle = intent.getStringExtra("BOOK_TITLE")
        val bookMedia = intent.getStringExtra("BOOK_MEDIA")
        val bookAuthor = intent.getStringExtra("BOOK_AUTHOR")
        val bookDescription = intent.getStringExtra("BOOK_DESCRIPTION")
        val bookPages = intent.getStringExtra("BOOK_PAGES")
        val bookYear = intent.getStringExtra("BOOK_YEAR")
        val bookSubject = intent.getStringExtra("BOOK_SUBJECT")
        val link = intent.getStringExtra("BOOK_LINK")


        // Get references to views
        val getMedia = findViewById<ImageView>(R.id.getMedia)
        val getAuthor = findViewById<TextView>(R.id.getAuthor)
        val getTitle = findViewById<TextView>(R.id.getTitle)
        val getDescription = findViewById<TextView>(R.id.getDescription)
        val getPages = findViewById<TextView>(R.id.getPages)
        val getYear = findViewById<TextView>(R.id.getYear)
        val getSubject = findViewById<TextView>(R.id.subject)
        val open = findViewById<LinearLayout>(R.id.open)


        open.setOnClickListener {
            // Check if the link is not null or empty
            if (!link.isNullOrBlank()) {
                // Create an Intent with the ACTION_VIEW action
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))

                // Start the activity (open the link)
                startActivity(intent)
            }
        }

        // Set data to views
        getTitle.text = bookTitle
        getAuthor.text = bookAuthor
        getDescription.text = bookDescription
        getPages.text = bookPages
        getYear.text = bookYear
        getSubject.text = bookSubject

        // Load image using Picasso
        Picasso.get().load(bookMedia).into(getMedia)

    }
}