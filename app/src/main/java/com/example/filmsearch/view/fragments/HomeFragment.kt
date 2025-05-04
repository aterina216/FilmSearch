package com.example.filmsearch.view.fragments

import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
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
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var binding: FragmentHomeBinding? = null
    private val bind get() = binding!!
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private val viewModel by lazy {
      ViewModelProvider.NewInstanceFactory().create(HomeFragmentViewModel::class.java)
  }


    private var filmsDataBase = listOf<Film>()
        //Используем backing field
        set(value) {
            //Если придет такое же значение, то мы выходим из метода
            if (field == value) return
            //Если пришло другое значение, то кладем его в переменную
            field = value
            //Обновляем RV адаптер
            filmsAdapter.addItems(field)
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(com.example.filmsearch.view.fragments.ARG_PARAM1)
            param2 = it.getString(com.example.filmsearch.view.fragments.ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding?.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchSlide = Slide(Gravity.TOP).addTarget(R.id.search_view)
        val recyclerSlide = Slide(Gravity.BOTTOM).addTarget(R.id.main_recycler)
        val customTransition = TransitionSet().apply {
            duration = 500
            addTransition(recyclerSlide)
            addTransition(searchSlide)
            AnimationHelper.performFragmentCircularrevealAnimation(view, requireActivity(), 1)
        }


        val recv = binding?.mainRecycler
        apply_rv(recv)
        binding?.searchView?.setOnClickListener {
            binding?.searchView?.isIconified = false
        }
        binding?.searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText?.isEmpty() == true){
                    filmsAdapter.addItems(filmsDataBase)
                    return true
                }
                val result = filmsDataBase.filter {
                    it.title.toLowerCase(Locale.getDefault()).contains(newText!!.toLowerCase(Locale.getDefault()))
                }
                filmsAdapter.addItems(result)
                return true
            }
        })
        viewModel.filmsListLiveData.observe(viewLifecycleOwner, Observer<List<Film>> {
            filmsDataBase = it
        })

        binding?.mainRecycler?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Проверяем, если мы находимся внизу списка
                if (!recyclerView.canScrollVertically(1)) {
                    // Если да, то загружаем следующие фильмы
                    viewModel.loadFilms()
                }
            }
        })

    }

    fun apply_rv(recv: RecyclerView?){
        recv.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        (requireActivity() as MainActivity).launchDetailsFragment(film)
                    }
                })
            this?.adapter = filmsAdapter
            this?.layoutManager = LinearLayoutManager(requireContext())
            val decorator = TopSpacingItemDecoration(8)
            this!!.addItemDecoration(decorator)
        }
        filmsAdapter.addItems(filmsDataBase)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(com.example.filmsearch.view.fragments.ARG_PARAM1, param1)
                    putString(com.example.filmsearch.view.fragments.ARG_PARAM2, param2)
                }
            }
    }
}