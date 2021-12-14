package com.example.seaselmobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.seaselmobile.model.api.CompositionApiModel
import com.example.seaselmobile.model.api.CompositionRepresentationApiModel

class MusicListAdapter(
    private val values: MutableList<CompositionRepresentationApiModel>
) : RecyclerView.Adapter<MusicListAdapter.ViewHolder>() {
    var onItemClickListener: (Int) -> Unit = {}

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_music_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.authorNameText.text = item.author
        holder.titleText.text = item.name
        holder.difficultyText.text = item.difficulty.toString()
        holder.button.setOnClickListener { onItemClickListener(position) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var authorNameText: TextView = itemView.findViewById(R.id.itemMusicListAuthor)
        var titleText: TextView = itemView.findViewById(R.id.itemMusicListTitle)
        var difficultyText: TextView = itemView.findViewById(R.id.itemMusicListDifficulty)
        var button: View = itemView.findViewById(R.id.itemBackgroundButton)
    }

    companion object {
        const val REPETITION_ID = "REPETITION_ID"
        const val REPETITION_AUTHOR = "REPETITION_AUTHOR"
        const val REPETITION_NAME = "REPETITION_NAME"
    }

}