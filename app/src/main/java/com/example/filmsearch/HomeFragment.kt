package com.example.filmsearch

import android.animation.ObjectAnimator
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionSet
import android.view.Gravity

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


import com.example.filmsearch.databinding.FragmentHomeBinding
import java.util.Locale


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var binding: FragmentHomeBinding? = null
    private val bind get() = binding!!
    private lateinit var filmsAdapter: FilmListRecyclerAdapter


    val filmsDataBase = listOf(
        Film(
            "Игра в кальмара",
            R.drawable.play,
            "Сотни игроков, испытывающих нехватку денег, принимают странное приглашение поучаствовать в детских играх. Внутри их ждет заманчивый приз со смертельно высокими ставками: игра на выживание, в которой на кону стоит колоссальный выигрыш в размере 45,6 миллиарда вон.",
            6.5f
        ),
        Film(
            "Первозданная америка",
            R.drawable.america,
            "Вас ждет увлекательный рассказ о смелом и авантюрном исследовании зарождения американского Запада, жестоких столкновениях культов, религий, мужчин и женщин,борющихся за контроль над новым миром.",
            4.8f
        ),
        Film(
            "Сегун",
            R.drawable.segun,
            "Когда в соседней японской рыбацкой деревушке находят таинственный европейский корабль, правитель Есии Торанага обнаруживает секреты, которые могут склонить чашу весов власти и уничтожить его врагов.",
            7.9f
        ),
        Film(
            "Субстанция",
            R.drawable.substance,
            "Увядающая знаменитость принимает наркотик с черного рынка: вещество, размножающее клетки, которое временно создает более молодую и совершенную версию самой себя.",
            7.1f
        ),
        Film(
            "Дюна",
            R.drawable.duna,
            "Пол Атрейдес прибывает на Арракис после того, как его отец принимает руководство опасной планетой. Однако после предательства наступает хаос, когда силы сталкиваются за контроль над меланжем, драгоценным ресурсом.",
            6.9f
        ),
        Film(
            "Паразиты",
            R.drawable.cover,
            "Жадность и классовая дискриминация угрожают недавно сложившимся симбиотическим отношениям между богатой семьей Пак и обездоленным кланом Ким.",
            7.3f
        ),
        Film(
            "Джокер",
            R.drawable.jocer,
            "Артур Флек, тусовочный клоун и неудавшийся стендап-комик, ведет бедную жизнь со своей больной матерью. Однако, когда общество избегает его и клеймит как урода, он решает принять жизнь хаоса в Готэм-Сити.",
            7.6f
        ),
        Film(
            "Побег из Шоушенка",
            R.drawable.escape,
            "Банкир, осужденный за убийство, на протяжении четверти века завязывает дружбу с закоренелым осужденным, сохраняя при этом свою невиновность и пытаясь сохранять надежду с помощью простого сострадания.",
            8.0f
        ),
        Film(
            "Властелин колец: Братство кольца",
            R.drawable.lord,
            "Кроткий Хоббит из Удела и восемь его спутников отправляются в путешествие, чтобы уничтожить могущественное Кольцо Единого и спасти Средиземье от Темного Лорда Саурона.",
            8.9f
        )
    )
    private var targetRating: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
        binding?.searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
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
    }


    fun apply_rv(recv: RecyclerView?){
        recv.apply {
            filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener{
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
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}