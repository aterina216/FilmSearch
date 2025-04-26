package com.example.filmsearch.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.filmsearch.R
import com.example.filmsearch.databinding.FragmentDetailsBinding
import com.example.filmsearch.domain.Film


class DetailsFragment : Fragment() {
    private var binding: FragmentDetailsBinding? = null
    private val bind get() = binding!!
    private lateinit var film: Film

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val view = binding?.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val film = arguments?.get("film") as Film
        binding?.detailsToolbar?.title = film.title
      //  binding?.detailsPoster?.setImageResource(film.poster)
        binding?.detailsDescription?.text = film.description
        binding?.detailsFabFavorites?.setOnClickListener {
                if (!film.isInFavorites) {
                    binding?.detailsFabFavorites?.setImageResource(R.drawable.favorite_base)
                    film.isInFavorites = true
                    }
                else binding?.detailsFabFavorites?.setImageResource(R.drawable.favorite)
                film.isInFavorites = false

        }
        binding?.detailsFab?.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra( Intent.EXTRA_TEXT,
                "Check out this film: ${film.title} \n\n ${film.description}")
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share To:"))
        }
    }
    }
