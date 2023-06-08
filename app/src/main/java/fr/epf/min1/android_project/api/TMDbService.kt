package fr.epf.min1.android_project

import fr.epf.min1.android_project.model.MovieDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface TMDbService {
    @GET("search/movie")
    suspend fun getFilms(@Query("api_key") api_key:String,@Query("query") query: String?, @Query("page") page: Int): GetFilmsResult

    @GET("movie/{movie_id}")
    suspend fun getMovieDetailsById(@Path("movie_id") id: Int,@Query("api_key") api_key:String): MovieDetail

    @GET("movie/{movie_id}/recommendations")
    suspend fun getFilmsRecommendations(@Path("movie_id") movie_id:Int, @Query("api_key") api_key:String): GetFilmsResult
}

data class GetFilmsResult(
    val page: Int,
    val results: List<Film>,
    val total_results: Int,
    val total_pages: Int
)

data class Film(
    val poster_path: String?,
    val adult: Boolean,
    val overview: String,
    val release_date: String,
    val genre_ids: List<Int>,
    val id: Int,
    val original_title: String,
    val original_language: String,
    val title: String,
    val backdrop_path: String?,
    val popularity: Float,
    val vote_count: Int,
    val video:Boolean,
    val vote_average: Float
)

data class MovieDetailResult(
    val adult: Boolean, //details
    val backdrop_path: String,
    val budget: Int, //details
    val belongs_to_collection: Collection,
    val genres: List<Genres>,
    val id: Int,
    val homepage: String,
    val imdb_id: String,
    val name: String,
    val original_language: String, //details
    val original_title: String, //details
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val production_companies: List<ProductionCompanies>, //details
    val production_countries: List<ProductionCountries>,
    val release_date: String, //details
    val revenue: Long,
    val runtime: Int,
    val spoken_languages: List<Language>, //details
    val status: String,
    val tagline: String, //details
    val title: String,
    val video: Boolean,
    val vote_average: Float, //rating
    val vote_count: Int //with average
)

data class Language(
    val english_name: String,
    val iso_639_1: String,
    val name: String
)

data class ProductionCountries(
    val iso_3166_1: String,
    val name: String
)

data class ProductionCompanies(
    val id: Int,
    val logo_path: String,
    val name: String,
    val origin_country: String
)

data class Collection(
    val id: Int,
    val name: String,
    val poster_path: String,
    val backdrop_path: String
)

data class Genres(
    val id: Int,
    val name: String
)