package fr.epf.min1.android_project.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ExperimentalGetImage
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.epf.min1.android_project.ListFilmActivity
import fr.epf.min1.android_project.QRCodeActivity
import fr.epf.min1.android_project.R

//pour savoir sur quelle page on se trouve
const val PAGE_MAIN = 0
const val PAGE_SEARCH = 1
const val PAGE_FAVORITES = 2
const val PAGE_QR_CODE = 3

val currentPage = PAGE_MAIN


class MyMainActivity : AppCompatActivity() {
    private lateinit var popularMovies: RecyclerView
    private lateinit var popularMoviesAdapter: MoviesAdapter

    private lateinit var topRatedMovies: RecyclerView
    private lateinit var topRatedMoviesAdapter: MoviesAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_activity_main)

        popularMovies = findViewById(R.id.popular_movies)
        topRatedMovies = findViewById(R.id.top_rated_movies)

        popularMoviesAdapter = MoviesAdapter(listOf()) { movie -> showMovieDetails(movie) }
        topRatedMoviesAdapter = MoviesAdapter(listOf()) { movie -> showMovieDetails(movie) }

        popularMovies.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        topRatedMovies.layoutManager =LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        popularMovies.adapter = popularMoviesAdapter
        topRatedMovies.adapter = topRatedMoviesAdapter

        MoviesRepository.getPopularMovies(
            onSuccess = ::onPopularMoviesFetched,
            onError = ::onError
        )


        MoviesRepository.getTopRatedMovies(
            onSuccess = ::onTopRatedMoviesFetched,
            onError = ::onError
        )

    }

    private fun onTopRatedMoviesFetched(movies: List<Movie>) {
        topRatedMoviesAdapter.updateMovies(movies)
    }


    private fun onPopularMoviesFetched(movies: List<Movie>) {
        popularMoviesAdapter.updateMovies(movies)
    }


    private fun onError() {
        Toast.makeText(this, getString(R.string.error_fetch_movies), Toast.LENGTH_SHORT).show()
    }


    //Menu dynamique
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu,menu)
        updateMenuItems(menu);
        return super.onCreateOptionsMenu(menu)
    }

    private fun updateMenuItems(menu: Menu) {
        val homeItem = menu.findItem(R.id.home)
        val searchItem = menu.findItem(R.id.search_movie)
        val favItem = menu.findItem(R.id.fav_movies)
        val qrCodeItem = menu.findItem(R.id.action_scan_qr_code)
        when (currentPage) {

            PAGE_MAIN -> {
                homeItem.isVisible = false
                searchItem.isVisible = true
                favItem.isVisible = true
                qrCodeItem.isVisible = true
            }

            PAGE_SEARCH -> {
                homeItem.isVisible = true
                searchItem.isVisible = false
                favItem.isVisible = true
                qrCodeItem.isVisible = true
            }

            PAGE_FAVORITES -> {
                homeItem.isVisible = true
                searchItem.isVisible = true
                favItem.isVisible = false
                qrCodeItem.isVisible = true
            }

            PAGE_QR_CODE -> {
                homeItem.isVisible = true
                searchItem.isVisible = true
                favItem.isVisible = true
                qrCodeItem.isVisible = false
            }
        }
    }
    @ExperimentalGetImage
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.search_movie -> {
                Toast.makeText(this,"Recherche",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ListFilmActivity::class.java)
                startActivity(intent)
            }
            R.id.fav_movies -> {
                Toast.makeText(this,"Favoris",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MoviesFavoriteActivity::class.java)
                startActivity(intent)
            }
            R.id.action_scan_qr_code -> {
                Toast.makeText(this,"Scanner un QRCode",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, QRCodeActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showMovieDetails(movie: Movie) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra(MOVIE_BACKDROP, movie.backdropPath)
        intent.putExtra(MOVIE_POSTER, movie.posterPath)
        intent.putExtra(MOVIE_TITLE, movie.title)
        intent.putExtra(MOVIE_RATING, movie.rating)
        intent.putExtra(MOVIE_RELEASE_DATE, movie.releaseDate)
        intent.putExtra(MOVIE_OVERVIEW, movie.overview)
        startActivity(intent)
    }

}