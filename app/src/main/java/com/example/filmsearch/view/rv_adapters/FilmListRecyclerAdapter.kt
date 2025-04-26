package com.example.filmsearch.view.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filmsearch.view.rv_viewholders.FilmViewHolder
import com.example.filmsearch.databinding.FilmItemBinding
import com.example.filmsearch.domain.Film

class FilmListRecyclerAdapter(private val clickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var binding: FilmItemBinding
    private val items = mutableListOf<Film>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        binding = FilmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder){
            is FilmViewHolder -> {
                holder.bind(items[position])
                val scaledRating = (items[position].rating / 10f) * 100f
                binding.itemContainer.setOnClickListener{
                    clickListener.click(items[position])
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
    fun addItems(list: List<Film>){
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
    interface OnItemClickListener{
        fun click(film: Film)
    }
    fun updateRating(position: Int, newRating: Float) {
      //  items[position].rating = newRating
        notifyItemChanged(position) // Обновление только одной позиции
    }
}