package fr.epf.min1.android_project.home

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class FavoriteMoviesManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("FavoriteMovies", Context.MODE_PRIVATE)
    private val favoriteMoviesKey = "favorite_movies"

   fun getFavoriteMovies(): Set<String> {
        return sharedPreferences.getStringSet(favoriteMoviesKey, emptySet()) ?: emptySet()
    }

    fun addFavoriteMovie(movieTitle: String) {
        val favoriteMovies = getFavoriteMovies().toMutableSet()
        favoriteMovies.add(movieTitle)
        sharedPreferences.edit().putStringSet(favoriteMoviesKey, favoriteMovies).apply()
    }

    fun removeFavoriteMovie(movieTitle: String) {
        val favoriteMovies = getFavoriteMovies().toMutableSet()
        favoriteMovies.remove(movieTitle)
        sharedPreferences.edit().putStringSet(favoriteMoviesKey, favoriteMovies).apply()
    }
    fun isMovieFavorite(movieTitle: String): Boolean {
        return getFavoriteMovies().contains(movieTitle)
    }

}