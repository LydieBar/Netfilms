package fr.epf.min1.android_project.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.camera.core.ExperimentalGetImage
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.gson.Gson
import fr.epf.min1.android_project.QRCodeActivity
import fr.epf.min1.android_project.R

class MoviesFavoriteActivity : AppCompatActivity() {
    private lateinit var linearLayout: LinearLayout
    private lateinit var favoriteMoviesManager: FavoriteMoviesManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies_favorite)

        linearLayout = findViewById(R.id.linearLayout)
        favoriteMoviesManager = FavoriteMoviesManager(this)

        displayFavoriteMovies()
    }

    private fun addTextView(text: String) {
        val textView = TextView(this)
        textView.text = text
        textView.textSize = 18f
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        linearLayout.addView(textView)
    }

    private fun removeTextView(text: String) {
        for (i in 0 until linearLayout.childCount) {
            val child = linearLayout.getChildAt(i)
            if (child is TextView && child.text == text) {
                linearLayout.removeView(child)
                break
            }
        }
    }



   private fun displayFavoriteMovies() {
        val favoriteMovies = favoriteMoviesManager.getFavoriteMovies()
        for (movieTitle in favoriteMovies) {
            val movieLayout = createMovieLayout(movieTitle)
            linearLayout.addView(movieLayout)
        }
    }

    private fun createMovieLayout(movieTitle: String): View {
        val layoutInflater = LayoutInflater.from(this)
        val movieLayout = layoutInflater.inflate(R.layout.item_favorite_movie, linearLayout, false)
        val movieTitleTextView = movieLayout.findViewById<TextView>(R.id.movieTitleTextView)

        movieTitleTextView.text = movieTitle


        /*Glide.with(this)
            .load("https://image.tmdb.org/t/p/original${movieTitle.posterPath}")
            .placeholder(R.drawable.baseline_sync_24) // Placeholder image while loading
            .into(moviePosterImageView)*/

        return movieLayout
    }



}