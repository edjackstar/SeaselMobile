package com.example.seaselmobile.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.seaselmobile.R
import com.example.seaselmobile.model.api.CompositionRepresentationApiModel


class CompositionsAdapter(
    var compositions: List<CompositionRepresentationApiModel>
) : RecyclerView.Adapter<CompositionsAdapter.ViewHolder>() {
    var onItemClick: (Int) -> Unit = {}
    var onFeedbackClick: (Int) -> Unit = {}
    var onAddFavoriteClick: (Int) -> Unit = {}
    var onRemoveFavoriteClick: (Int) -> Unit = {}
    var favorites: List<Int> = listOf()

    override fun getItemCount() = compositions.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_composition, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = compositions[position]
        val context = holder.itemView.context
        holder.author.text = item.author
        holder.name.text = item.name
        holder.difficulty.text = item.difficulty.toString()
        holder.button.setOnClickListener { onItemClick(compositions[position].composition_id) }
        holder.rating.rating = item.avg_mark ?: 0f

        holder.popupMenu.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.popupMenu)
            if (favorites.contains(compositions[position].composition_id)) {
                popupMenu.menuInflater.inflate(R.menu.composition_menu_remove, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.remove_feedback -> onFeedbackClick(compositions[position].composition_id)
                        R.id.remove_favorite -> onRemoveFavoriteClick(compositions[position].composition_id)
                    }
                    true
                }
            } else {
                popupMenu.menuInflater.inflate(R.menu.composition_menu_add, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.add_feedback -> onFeedbackClick(compositions[position].composition_id)
                        R.id.add_favorite -> onAddFavoriteClick(compositions[position].composition_id)
                    }
                    true
                }
            }
            popupMenu.show()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var author: TextView = itemView.findViewById(R.id.itemCompositionAuthor)
        var name: TextView = itemView.findViewById(R.id.itemCompositionName)
        var difficulty: TextView = itemView.findViewById(R.id.itemCompositionDifficulty)
        var button: View = itemView.findViewById(R.id.itemCompositionBackgroundButton)
        var rating: RatingBar = itemView.findViewById(R.id.itemCompositionRating)
        var popupMenu: ImageView = itemView.findViewById(R.id.itemCompositionOptions)
    }

}