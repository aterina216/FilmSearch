package com.example.filmsearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.filmsearch.databinding.FragmentDetailsBinding


class DetailsFragment : Fragment() {
    private var binding: FragmentDetailsBinding? = null
    private val bind get() = binding!!

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
        binding?.detailsPoster?.setImageResource(film.poster)
        binding?.detailsDescription?.text = film.description

    }
}