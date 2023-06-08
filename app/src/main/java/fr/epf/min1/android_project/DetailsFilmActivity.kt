package fr.epf.min1.android_project

import android.database.Cursor
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import fr.epf.min1.android_project.api.ApiTMDb
import fr.epf.min1.android_project.api.AppConstants
import fr.epf.min1.android_project.databinding.ActivityDetailsFilmBinding
import fr.epf.min1.android_project.model.Collection
import fr.epf.min1.android_project.model.Film
import fr.epf.min1.android_project.model.Genre
import fr.epf.min1.android_project.model.MovieDetail
import fr.epf.min1.android_project.model.ProductionCompany
import fr.epf.min1.android_project.model.ProductionCountry
import fr.epf.min1.android_project.model.Language
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import java.time.LocalDate


class DetailsFilmActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailsFilmBinding
    private lateinit var menu: Menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_film)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val extras = intent.extras
        val id = extras?.getInt("film_id") ?: 0

        binding = ActivityDetailsFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (id != 0){
            synchroDetails(id)
            synchroRecommendations(id)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_film_menu,menu)
        if (menu != null) {
            this.menu = menu
        }
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_delete_from_favorite -> {
                val db = DBHelper(this, null)
                var movieId:Int = findViewById<TextView>(R.id.id_table_textView).text.toString().toInt()
                movieId = 10
                if (isInDB(movieId, db)){
                    menu.findItem(item.itemId).icon = ContextCompat.getDrawable(this, R.drawable.baseline_star_border_24)

                    db.deleteFavoriteByMovieId(movieId)

                    Toast.makeText(this," Movie deleted from favorites", Toast.LENGTH_SHORT).show()
                }else{
                    menu.findItem(item.itemId).icon = ContextCompat.getDrawable(this, R.drawable.baseline_star_24)

                    val title = findViewById<TextView>(R.id.title_table_textView).text.toString()
                    val rating = findViewById<TextView>(R.id.popularity_table_textView).text.toString().toFloat()
                    val posterPath = findViewById<TextView>(R.id.poster_path_table_textView).text.toString()

                    db.addFilm(movieId, "title", 1.1, "posterPath")

                    Toast.makeText(this," Movie added to favorites", Toast.LENGTH_SHORT).show()
                }



            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isInDB(id:Int,db:DBHelper) : Boolean{
        val res: Cursor? = db.findFavoriteByMovieId(id)
        if (res != null) {
            if (res.moveToFirst()){
                return true
            }
        }
        return false
    }

    private fun synchroDetails(id:Int) {

        val retrofit: Retrofit = ApiTMDb.getInstance()

        val service = retrofit.create(TMDbService::class.java)
        runBlocking {

            val filmRes = service.getMovieDetailsById(id,AppConstants.TMDB_KEY_V3)

//            Mapping
//            val genres:List<Genre> = filmRes.genres.map {
//                Genre(it.id,it.name)
//            }
//
//            val productionCompanes:List<ProductionCompany> = filmRes.production_companies.map {
//                ProductionCompany(it.id,it.logo_path,it.name,it.origin_country)
//            }
//
//            val productionCountries:List<ProductionCountry> = filmRes.production_countries.map {
//                ProductionCountry(it.iso_3166_1,it.name)
//            }
//
//            val languages:List<Language> = filmRes.spoken_languages.map {
//                Language(it.english_name, it.iso_639_1,it.name)
//            }

//            val film = MovieDetail(
//                    filmRes.adult,
//                    filmRes.backdrop_path,
//                    filmRes.budget,
//                    Collection(filmRes.belongs_to_collection.id,filmRes.belongs_to_collection.name,filmRes.belongs_to_collection.poster_path,filmRes.belongs_to_collection.backdrop_path),
//                    genres,
//                    filmRes.id,
//                    filmRes.homepage,
//                    filmRes.imdb_id,
//                    filmRes.name,
//                    filmRes.original_language,
//                    filmRes.original_title,
//                    filmRes.overview,
//                    filmRes.popularity,
//                    filmRes.poster_path,
//                    productionCompanes,
//                    productionCountries,
//                    LocalDate.parse(filmRes.release_date),
//                    filmRes.revenue,
//                    filmRes.runtime,
//                    languages,
//                    filmRes.status,
//                    filmRes.tagline,
//                    filmRes.title,
//                    filmRes.video,
//                    filmRes.vote_average,
//                    filmRes.vote_count)


            //TODO create all widgets in layout/activity_details_film.xml
            //TODO create recycler view for genre, countries, companies, languages (horizontal)
            val titleTextView = findViewById<TextView>(R.id.movie_title_details_textView)
            titleTextView.text = filmRes.title

            val posterImageView = findViewById<ImageView>(R.id.poster_details_imageView)
            Glide.with(this@DetailsFilmActivity).load("https://image.tmdb.org/t/p/original/" + filmRes.poster_path).into(posterImageView)

            val titleTableTextView = findViewById<TextView>(R.id.title_table_textView)
            titleTableTextView.text = filmRes.title

            val originalTitleTableTextView = findViewById<TextView>(R.id.original_title_table_textView)
            originalTitleTableTextView.text = filmRes.original_title

            val overviewTableTextView = findViewById<TextView>(R.id.overview_table_textView)
            overviewTableTextView.text = filmRes.overview

            val idTableTextView = findViewById<TextView>(R.id.id_table_textView)
            idTableTextView.text = filmRes.id.toString()

            val posterPathTableTextView = findViewById<TextView>(R.id.poster_path_table_textView)
            posterPathTableTextView.text = filmRes.poster_path

            val languagesTableTextView = findViewById<TextView>(R.id.languages_table_textView)
            languagesTableTextView.text = filmRes.spoken_languages.joinToString(",\n")

            val originalLanguageTableTextView = findViewById<TextView>(R.id.original_language_table_textView)
            originalLanguageTableTextView.text = filmRes.original_language

            val runtimeTableTextView = findViewById<TextView>(R.id.runtime_table_textView)
            runtimeTableTextView.text = filmRes.runtime.toString()

            val isAdultImageView = findViewById<ImageView>(R.id.adult_table_imageView)
            if (filmRes.adult)
                isAdultImageView.setImageResource(R.drawable.baseline_check_box_24)
            else
                isAdultImageView.setImageResource(R.drawable.baseline_check_box_outline_blank_24)

            val releaseDateTableTextView = findViewById<TextView>(R.id.release_date_table_textView)
            releaseDateTableTextView.text = filmRes.release_date

            val statusTableTextView = findViewById<TextView>(R.id.status_table_textView)
            statusTableTextView.text = filmRes.status

            val taglineTableTextView = findViewById<TextView>(R.id.tagline_table_textView)
            taglineTableTextView.text = filmRes.tagline

            val popularityTextView = findViewById<TextView>(R.id.popularity_table_textView)
            popularityTextView.text = filmRes.popularity.toString()

            val budgetTextView = findViewById<TextView>(R.id.budget_table_textView)
            budgetTextView.text = "$"+filmRes.budget.toString()
        }
    }

    private fun synchroRecommendations(id:Int) {

        val retrofit: Retrofit = ApiTMDb.getInstance()

        val service = retrofit.create(TMDbService::class.java)
        runBlocking {
            val filmRes = service.getFilmsRecommendations(id,AppConstants.TMDB_KEY_V3)
            //film view
            val films = filmRes.results.map {
                Film(it.poster_path,
                    it.adult,
                    it.overview,
                    it.release_date,
                    it.genre_ids,
                    it.id,
                    it.original_title,
                    it.original_language,
                    it.title,
                    it.backdrop_path,
                    it.popularity,
                    it.vote_count,
                    it.video,
                    it.vote_average)
            }

            val adapter = RecommendationAdapter(films,this@DetailsFilmActivity)

            binding.apply {
                carouselRecyclerView.adapter = adapter
                carouselRecyclerView.set3DItem(true)
                carouselRecyclerView.setAlpha(true)
                carouselRecyclerView.setInfinite(true)
            }
        }
    }
}