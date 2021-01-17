package co.canytech.films.kotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.canytech.films.MovieActivity
import co.canytech.films.R
import co.canytech.films.model.Category
import co.canytech.films.model.Movie
import co.canytech.films.util.CategoryTask
import co.canytech.films.util.ImageDownloaderTask
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.category_item.view.*
import kotlinx.android.synthetic.main.item.*
import kotlinx.android.synthetic.main.movie_item_similar.view.*


class MainActivity : AppCompatActivity() {

    private lateinit var mainAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val categories = arrayListOf<Category>()
        mainAdapter = MainAdapter(categories)
        recycler_view_main.adapter = mainAdapter
        recycler_view_main.layoutManager = LinearLayoutManager(this)

        val categoryTask = CategoryTask(this)
        categoryTask.setCategoryLoader {
            mainAdapter.categories.clear()
            mainAdapter.categories.addAll(it)
            mainAdapter.notifyDataSetChanged()
        }
        categoryTask.execute("https://tiagoaguiar.co/api/netflix/home")
    }

    private inner class MainAdapter(val categories: MutableList<Category>) : RecyclerView.Adapter<CategoryHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
            return CategoryHolder (
                layoutInflater.inflate(R.layout.category_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
            val category = categories[position]
            holder.bind(category)
        }

        override fun getItemCount(): Int = categories.size
    }

    private inner class MovieAdapter(val movies: List<Movie>) : RecyclerView.Adapter<MovieHolder>() {

        val onClick:((Int) -> Unit)? = { position ->
            if (movies[position].id <= 3) {
                val intent = Intent(this@MainActivity, MovieActivity::class.java)
                intent.putExtra("id", movies[position].id)
                startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
            return MovieHolder (
                    layoutInflater.inflate(R.layout.movie_item, parent, false), onClick
            )
        }

        override fun onBindViewHolder(holder: MovieHolder, position: Int) {
            val movie: Movie = movies[position]
            holder.bind(movie)
        }

        override fun getItemCount(): Int = movies.size
    }

    private inner class CategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(category: Category) {
            itemView.text_view_title.text = category.name
            itemView.recycler_view_movie.adapter = MovieAdapter(category.movies)
            itemView.recycler_view_movie.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.HORIZONTAL, false)
        }
    }

    private class MovieHolder(itemView: View, val onClick: ( (Int) -> Unit)?) : RecyclerView.ViewHolder(itemView ) {
        fun bind(movie: Movie) {
            ImageDownloaderTask(itemView.image_view_cover)
                    .execute(movie.coverUrl)
            itemView.image_view_cover.setOnClickListener{
                onClick?.invoke(adapterPosition)

            }
        }
    }
}