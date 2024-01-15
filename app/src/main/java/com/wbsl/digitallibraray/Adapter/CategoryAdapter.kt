package com.wbsl.digitallibraray.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wbsl.digitallibraray.Activities.OpenBook
import com.wbsl.digitallibraray.Activities.OpenCat
import com.wbsl.digitallibraray.Data.Catalogue
import com.wbsl.digitallibraray.Data.Category
import com.wbsl.digitallibraray.R

class CategoryAdapter(
    private val context: Context,
    private val catalogues: List<Category>,
    private val onItemClickListener: (Category) -> Unit
) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val catalogueName: TextView = view.findViewById(R.id.catalogueName)
        val image_cat: ImageView = view.findViewById(R.id.image_cat)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val catalogue = catalogues[position]
        holder.catalogueName.text = catalogue.catalogue_name
        Picasso.get().load(catalogue.media_name).into(holder.image_cat)

        holder.itemView.setOnClickListener {
            onItemClickListener(catalogue)

            val intent = Intent(context, OpenCat::class.java)
            intent.putExtra("CAT_ID", catalogue.catalogue_id.toString())
            intent.putExtra("CAT_NAME", catalogue.catalogue_name)
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return catalogues.size
    }
}

