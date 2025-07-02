package com.example.filmsearch.view.fragments

import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filmsearch.utils.AnimationHelper
import com.example.filmsearch.view.rv_adapters.FilmListRecyclerAdapter
import com.example.filmsearch.view.MainActivity
import com.example.filmsearch.R
import com.example.filmsearch.data.MainRepository
import com.example.filmsearch.view.rv_adapters.TopSpacingItemDecoration
import com.example.filmsearch.databinding.FragmentHomeBinding
import com.example.filmsearch.domain.Film
import com.example.filmsearch.viewmodel.HomeFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/*class HomeFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(HomeFragmentViewModel::class.java)
    }
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private lateinit var binding: FragmentHomeBinding
    private var filmsDataBase = listOf<Film>()
        set(value) {
            if (field == value) return
            field = value
            filmsAdapter.addItems(field)
        }

    private var scrollListener: RecyclerView.OnScrollListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnimationHelper.performFragmentCircularrevealAnimation(view, requireActivity(), 1)

        initRecyckler()

        viewModel.filmsListLiveData.observe(viewLifecycleOwner, Observer<List<Film>> {
            filmsDataBase = it
            filmsAdapter.addItems(it)
        })

        viewModel.showProgressbar.observe(viewLifecycleOwner, Observer<Boolean> {
            binding.progressBar.isVisible = it
        })

        viewModel.hasMoreData.observe(viewLifecycleOwner, Observer { hasMore ->
            if (!hasMore) {
                // Если нет больше данных, отключаем пагинацию
                scrollListener?.let { binding.mainRecycler.removeOnScrollListener(it) }
            }
        })
    }

    private fun initRecyckler() {
        binding.mainRecycler.apply {
            filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                override fun click(film: Film) {
                    (requireActivity() as MainActivity).launchDetailsFragment(film)
                }
            })
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }

        // Создаем и устанавливаем слушатель прокрутки
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && dy > 0) {
                    // Если есть данные для загрузки, загружаем их
                    if (viewModel.hasMoreData.value == true) {
                        viewModel.loadFilms()
                    }
                }
            }
        }
        binding.mainRecycler.addOnScrollListener(scrollListener!!)
    }
}*/

class HomeFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(HomeFragmentViewModel::class.java)
    }
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private lateinit var binding: FragmentHomeBinding
    private var filmsDataBase = listOf<Film>()
        set(value) {
            if (field == value) return
            field = value
            filmsAdapter.addItems(field)
        }

    private var scrollListener: RecyclerView.OnScrollListener? = null
    private lateinit var scope: CoroutineScope

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnimationHelper.performFragmentCircularrevealAnimation(view, requireActivity(), 1)

        initRecyckler()

       scope = CoroutineScope(Dispatchers.IO).also {
           scope -> scope.launch {
               viewModel.filmsListData.collect{
                   withContext(Dispatchers.Main){
                       filmsAdapter.addItems(it)
                       filmsDataBase = it
                   }
               }
       }
           scope.launch {
               for(element in viewModel.showProgressbar){
                   launch(Dispatchers.Main) {
                       binding.progressBar.isVisible = element
                   }
               }
           }
       }


        // Подписка на наличие данных
           /* viewModel.hasMoreData.observe(viewLifecycleOwner, Observer { hasMore ->
            if (!hasMore) {
                // Если нет больше данных, отключаем пагинацию
                scrollListener?.let { binding.mainRecycler.removeOnScrollListener(it) }
            }
        })

        // Подписка на ошибки с явным указанием типа
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer<String> { error ->
            showErrorSnackbar(error)
        })*/
    }

    private fun initRecyckler() {
        binding.mainRecycler.apply {
            filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                override fun click(film: Film) {
                    (requireActivity() as MainActivity).launchDetailsFragment(film)
                }
            })
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }

        // Создаем и устанавливаем слушатель прокрутки
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && dy > 0) {
                    // Если есть данные для загрузки, загружаем их
                    if (viewModel.hasMoreData.value == true) {
                        viewModel.loadFilms()
                    }
                }
            }
        }
        binding.mainRecycler.addOnScrollListener(scrollListener!!)
    }

    // Функция для показа снэкбара с ошибкой
    private fun showErrorSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    override fun onStop() {
        super.onStop()
        scope.cancel()
    }
}



