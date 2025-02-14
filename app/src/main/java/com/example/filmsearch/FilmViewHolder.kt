package com.example.filmsearch

import androidx.recyclerview.widget.RecyclerView
import com.example.filmsearch.databinding.FilmItemBinding

class FilmViewHolder(private val bindingItem : FilmItemBinding) : RecyclerView.ViewHolder(bindingItem.root) {

    private val title = bindingItem.title
    private val poster = bindingItem.poster
    private val description = bindingItem.description


    fun bind(films: Film) {
        title.text = films.title
        poster.setImageResource(films.poster)
        description.text = films.description
    }
}