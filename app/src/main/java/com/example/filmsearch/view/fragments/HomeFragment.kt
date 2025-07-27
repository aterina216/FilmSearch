package com.example.filmsearch.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filmsearch.databinding.FragmentHomeBinding
import com.example.filmsearch.domain.Film
import com.example.filmsearch.utils.AnimationHelper
import com.example.filmsearch.utils.AutoDisposable
import com.example.filmsearch.utils.addTo
import com.example.filmsearch.view.MainActivity
import com.example.filmsearch.view.rv_adapters.FilmListRecyclerAdapter
import com.example.filmsearch.view.rv_adapters.TopSpacingItemDecoration
import com.example.filmsearch.viewmodel.HomeFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import java.util.Locale
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.concurrent.TimeUnit

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

    private val autoDisposable = AutoDisposable()

    private var isLoading = false // Переменная для управления загрузкой данных

    // Текущая страница и общая информация о страницах
    private var currentPage = 1
    private var totalPages = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        autoDisposable.bindTo(lifecycle)
        retainInstance = true
    }

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

        viewModel.filmsListData
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { list ->
                filmsAdapter.addItems(list)
                filmsDataBase = list
            }.addTo(autoDisposable)

        viewModel.showProgressbar
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.progressBar.isVisible = it
            }
            .addTo(autoDisposable)
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

        Observable.create(ObservableOnSubscribe<String> { subscriber ->
            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    subscriber.onNext(newText)
                    return false
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    subscriber.onNext(query)
                    return false
                }
            })
        })
            .debounce(800, TimeUnit.MILLISECONDS) // Задержка перед запросом
            .filter { it.isNotBlank() }
            .flatMap { query ->
                viewModel.getSearchResult(query.toLowerCase(Locale.getDefault()).trim())
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    filmsAdapter.addItems(it) // Добавляем новые элементы
                },
                onError = {
                    showErrorSnackbar("Что-то пошло не так")
                }
            )
            .addTo(autoDisposable)
    }


    override fun onStop() {
        super.onStop()
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



