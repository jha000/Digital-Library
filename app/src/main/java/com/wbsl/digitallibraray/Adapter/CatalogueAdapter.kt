package com.wbsl.digitallibraray.Adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wbsl.digitallibraray.Data.Catalogue
import com.wbsl.digitallibraray.R

class CatalogueAdapter(
    private val catalogues: List<Catalogue>,
    private val onItemClickListener: (Catalogue) -> Unit
) :
    RecyclerView.Adapter<CatalogueAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val catalogueName: TextView = view.findViewById(R.id.catalogueNameTextView)
        val indicatorView: View = view.findViewById(R.id.indicatorView)
    }

    private var selectedItemPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_catalogue, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val catalogue = catalogues[position]
        holder.catalogueName.text = catalogue.catalogue_name

        holder.itemView.setOnClickListener {
            onItemClickListener(catalogue)

            selectedItemPosition = position
            notifyDataSetChanged()
        }

        holder.indicatorView.visibility =
            if (position == selectedItemPosition) View.VISIBLE else View.INVISIBLE

        if (holder.indicatorView.visibility == View.VISIBLE) {
            holder.catalogueName.alpha = 1.0f
            holder.catalogueName.setTypeface(null, Typeface.BOLD)
            holder.catalogueName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        }
        else {
            holder.catalogueName.alpha = 0.5f
            holder.catalogueName.setTypeface(null, Typeface.NORMAL)
            holder.catalogueName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        }

    }

    override fun getItemCount(): Int {
        return catalogues.size
    }
}

