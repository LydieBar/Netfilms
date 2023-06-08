package fr.epf.min1.android_project.home

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String = "b8bdaa5dec63d722fcb81039bd4fac09",
        @Query("page") page: Int
    ): Call<GetMoviesResponse>


    @GET("movie/top_rated")
    fun getTopRatedMovies(
        @Query("api_key") apiKey: String = "b8bdaa5dec63d722fcb81039bd4fac09",
        @Query("page") page: Int
    ): Call<GetMoviesResponse>
}