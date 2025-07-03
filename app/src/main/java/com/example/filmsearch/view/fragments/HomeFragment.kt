package com.example.filmsearch.view.fragments

import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.cancelChildren
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

    private var isLoading = false // Переменная для управления загрузкой данных

    // Текущая страница и общая информация о страницах
    private var currentPage = 1
    private var totalPages = 1

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

        initSearchView()

        initRecyckler()

        initPagination()

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

    private fun initSearchView() {
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        //Подключаем слушателя изменений введенного текста в поиска
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //Этот метод отрабатывает при нажатии кнопки "поиск" на софт клавиатуре
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            //Этот метод отрабатывает на каждое изменения текста
            override fun onQueryTextChange(newText: String): Boolean {
                //Если ввод пуст то вставляем в адаптер всю БД
                if (newText.isEmpty()) {
                    filmsAdapter.addItems(filmsDataBase)
                    return true
                }
                //Фильтруем список на поискк подходящих сочетаний
                val result = filmsDataBase.filter {
                    //Чтобы все работало правильно, нужно и запроси и имя фильма приводить к нижнему регистру
                    it.title.toLowerCase(Locale.getDefault())
                        .contains(newText.toLowerCase(Locale.getDefault()))
                }
                //Добавляем в адаптер
                filmsAdapter.addItems(result)
                return true
            }
        })
    }

    override fun onStop() {
        super.onStop()
        scope.cancel()
    }

    private fun initPagination() {
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                Log.d("HomeFragment", "onScrolled: totalItemCount = $totalItemCount, visibleItemCount = $visibleItemCount, firstVisibleItemPosition = $firstVisibleItemPosition, currentPage = $currentPage, totalPages = $totalPages")

                // Если прокрутили до конца списка и есть еще данные для загрузки
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && dy > 0) {
                    Log.d("HomeFragment", "Достигнут конец списка, загружаем следующую страницу")
                    if (!isLoading && currentPage <= totalPages) {
                        isLoading = true
                        Log.d("HomeFragment", "Загружаем страницу $currentPage")
                        viewModel.loadFilms()  // Загружаем следующие данные
                    }
                }
            }
        }
        binding.mainRecycler.addOnScrollListener(scrollListener!!)
    }
}



