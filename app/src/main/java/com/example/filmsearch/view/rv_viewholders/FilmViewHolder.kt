package com.example.filmsearch.view.rv_viewholders

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.filmsearch.databinding.FilmItemBinding
import com.example.filmsearch.domain.Film

class FilmViewHolder(private val bindingItem : FilmItemBinding) : RecyclerView.ViewHolder(bindingItem.root) {

    private val title = bindingItem.title
    private val poster = bindingItem.poster
    private val description = bindingItem.description
    val ratingDonut = bindingItem.ratingDonut

    fun bind(films: Film) {
        title.text = films.title
        poster.setImageResource(films.poster)
        Glide.with(itemView)
            //Загружаем сам ресурс
            .load(films.poster)
            //Центруем изображение
            .centerCrop()
            //Указываем ImageView, куда будем загружать изображение
            .into(poster)
        description.text = films.description
        ratingDonut.setProgress((films.rating * 10).toInt())
        val scaledRating = (films.rating / 10f) * 100f
    }
}