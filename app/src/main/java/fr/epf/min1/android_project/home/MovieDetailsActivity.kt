package fr.epf.min1.android_project.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import fr.epf.min1.android_project.R


const val MOVIE_BACKDROP = "extra_movie_backdrop"
const val MOVIE_POSTER = "extra_movie_poster"
const val MOVIE_TITLE = "extra_movie_title"
const val MOVIE_RATING = "extra_movie_rating"
const val MOVIE_RELEASE_DATE = "extra_movie_release_date"
const val MOVIE_OVERVIEW = "extra_movie_overview"


class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var backdrop: ImageView
    private lateinit var poster: ImageView
    private lateinit var title: TextView
    private lateinit var rating: RatingBar
    private lateinit var releaseDate: TextView
    private lateinit var overview: TextView

    private var isFavorite: Boolean = false
    private lateinit var favoriteMoviesManager: FavoriteMoviesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        backdrop = findViewById(R.id.movie_backdrop)
        poster = findViewById(R.id.movie_poster)
        title = findViewById(R.id.movie_title)
        rating = findViewById(R.id.movie_rating)
        releaseDate = findViewById(R.id.movie_release_date)
        overview = findViewById(R.id.movie_overview)

        val extras = intent.extras

        if (extras != null) {
            populateDetails(extras)
        } else {
            finish()
        }
        favoriteMoviesManager = FavoriteMoviesManager(this)
        isFavorite = favoriteMoviesManager.isMovieFavorite(title.text.toString())
        updateStarIcon()

    }
    private fun populateDetails(extras: Bundle) {
        extras.getString(MOVIE_BACKDROP)?.let { backdropPath ->
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w1280$backdropPath")
                .transform(CenterCrop())
                .into(backdrop)
        }

        extras.getString(MOVIE_POSTER)?.let { posterPath ->
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w342$posterPath")
                .transform(CenterCrop())
                .into(poster)
        }

        title.text = extras.getString(MOVIE_TITLE, "")
        rating.rating = extras.getFloat(MOVIE_RATING, 0f) / 2
        releaseDate.text = extras.getString(MOVIE_RELEASE_DATE, "")
        overview.text = extras.getString(MOVIE_OVERVIEW, "")
    }
    private fun updateStarIcon() {
        val starButton = findViewById<ImageView>(R.id.star_button)
        starButton.setImageResource(if (isFavorite) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24)
    }

    fun onStarButtonClick(view: View) {
        val movieTitle = title.text.toString()
        if (!isFavorite) {
            favoriteMoviesManager.addFavoriteMovie(movieTitle)
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
            //launchFavoriteActivity(movieTitle, true)
        } else {
            //removeFavorite(movieTitle)
            favoriteMoviesManager.removeFavoriteMovie(movieTitle)
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
        }
        isFavorite = !isFavorite
        updateStarIcon()
    }

    private fun launchFavoriteActivity(movieTitle: String, isFavorite: Boolean) {
        val intent = Intent(this, MoviesFavoriteActivity::class.java)
        intent.putExtra("movie_title", movieTitle)
        intent.putExtra("is_favorite", isFavorite)
        startActivity(intent)
    }

    private fun removeFavorite(movieTitle: String) {
        val intent = Intent(this, MoviesFavoriteActivity::class.java)
        intent.putExtra("movie_title", movieTitle)
        intent.putExtra("remove_favorite", true)
        intent.putExtra("is_favorite", false)
        startActivity(intent)
    }

}
