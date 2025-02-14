package com.example.filmsearch

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filmsearch.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    val filmsDataBase = listOf(
        Film("Игра в кальмара", R.drawable.play, "Сотни игроков, испытывающих нехватку денег, принимают странное приглашение поучаствовать в детских играх. Внутри их ждет заманчивый приз со смертельно высокими ставками: игра на выживание, в которой на кону стоит колоссальный выигрыш в размере 45,6 миллиарда вон."),
        Film("Первозданная америка", R.drawable.america, "Вас ждет увлекательный рассказ о смелом и авантюрном исследовании зарождения американского Запада, жестоких столкновениях культов, религий, мужчин и женщин,борющихся за контроль над новым миром."),
        Film("Сегун", R.drawable.segun, "Когда в соседней японской рыбацкой деревушке находят таинственный европейский корабль, правитель Есии Торанага обнаруживает секреты, которые могут склонить чашу весов власти и уничтожить его врагов."),
        Film("Субстанция", R.drawable.substance, "Увядающая знаменитость принимает наркотик с черного рынка: вещество, размножающее клетки, которое временно создает более молодую и совершенную версию самой себя."),
        Film("Дюна", R.drawable.duna, "Пол Атрейдес прибывает на Арракис после того, как его отец принимает руководство опасной планетой. Однако после предательства наступает хаос, когда силы сталкиваются за контроль над меланжем, драгоценным ресурсом."),
        Film("Паразиты", R.drawable.cover, "Жадность и классовая дискриминация угрожают недавно сложившимся симбиотическим отношениям между богатой семьей Пак и обездоленным кланом Ким."),
        Film("Джокер", R.drawable.jocer, "Артур Флек, тусовочный клоун и неудавшийся стендап-комик, ведет бедную жизнь со своей больной матерью. Однако, когда общество избегает его и клеймит как урода, он решает принять жизнь хаоса в Готэм-Сити."),
        Film("Побег из Шоушенка", R.drawable.escape, "Банкир, осужденный за убийство, на протяжении четверти века завязывает дружбу с закоренелым осужденным, сохраняя при этом свою невиновность и пытаясь сохранять надежду с помощью простого сострадания."),
        Film("Властелин колец: Братство кольца", R.drawable.lord, "Кроткий Хоббит из Удела и восемь его спутников отправляются в путешествие, чтобы уничтожить могущественное Кольцо Единого и спасти Средиземье от Темного Лорда Саурона.")

    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(binding.root)
        initNavigation()
        val recv = binding.mainRecycler
        apply_rv(recv)

    }
    fun apply_rv(recv: RecyclerView?){
        recv.apply {
            filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener{
                override fun click(film: Film) {
                    val bundle = Bundle()
                    //Первым параметром указывается ключ, по которому потом будем искать, вторым сам
                    //передаваемый объект
                    bundle.putParcelable("film", film)
                    val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            })
            this?.adapter = filmsAdapter
            this?.layoutManager = LinearLayoutManager(this@MainActivity)
            val decorator = TopSpacingItemDecoration(8)
            this!!.addItemDecoration(decorator)
        }
        filmsAdapter.addItems(filmsDataBase)
    }


    fun initNavigation() {
        var topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
        var bottom_navigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottom_navigation.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.favorites -> {
                    Toast.makeText(this, "Избранное", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.watch_later -> {
                    Toast.makeText(this, "Посмотреть похже", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.selections -> {
                    Toast.makeText(this, "Подборки", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
    }
}