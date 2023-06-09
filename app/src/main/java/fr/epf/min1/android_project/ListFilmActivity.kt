package fr.epf.min1.android_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.ExperimentalGetImage
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.epf.min1.android_project.api.ApiTMDb
import fr.epf.min1.android_project.home.MoviesFavoriteActivity
import fr.epf.min1.android_project.home.MyMainActivity
import fr.epf.min1.android_project.home.PAGE_FAVORITES
import fr.epf.min1.android_project.home.PAGE_MAIN
import fr.epf.min1.android_project.home.PAGE_QR_CODE
import fr.epf.min1.android_project.home.PAGE_SEARCH
import fr.epf.min1.android_project.model.Film
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit

val currentPage = PAGE_SEARCH
class ListFilmActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    private var lastPage:Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_film)

        // recycler view
        recyclerView = findViewById<RecyclerView>(R.id.list_film_recyclerview)
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        //search edit text
        val queryEdittext = findViewById<EditText>(R.id.search_film_list_film_edittext)

        //search button
        val searchButton = findViewById<Button>(R.id.search_films_list_film_button)
        searchButton.setOnClickListener {

            Log.d("EPF", "Query : ${queryEdittext.text}")

            synchro(queryEdittext.text.toString(),1)
        }

        //page edit text
        val pageEdittext = findViewById<EditText>(R.id.page_list_film_edittext)
        //TODO add change listener to update list after change
        pageEdittext.isEnabled = false


        //first page
        val firstPage = findViewById<ImageButton>(R.id.first_page_list_film_imageButton)
        firstPage.setOnClickListener {
            pageEdittext.setText("1")
            synchro(queryEdittext.text.toString(),1)
        }

        // last page
        val lastPage = findViewById<ImageButton>(R.id.last_page_list_film_imageButton)
        lastPage.setOnClickListener {
            pageEdittext.setText(this.lastPage.toString())
            synchro(queryEdittext.text.toString(),this.lastPage)
        }

        //previous page
        val previousButton = findViewById<ImageButton>(R.id.previous_page_list_film_imageButton)
        previousButton.setOnClickListener {
            val page: Int = pageEdittext.text.toString().toInt()
            pageEdittext.setText((page - 1).toString())
            synchro(queryEdittext.text.toString(),page - 1)
        }

        //next page
        val nextButton = findViewById<ImageButton>(R.id.next_page_list_film_imageButton)
        nextButton.setOnClickListener {
            val page: Int = pageEdittext.text.toString().toInt()
            pageEdittext.setText((page + 1).toString())
            synchro(queryEdittext.text.toString(),page + 1)

        }

        //no results

        val linearLayout = findViewById<LinearLayout>(R.id.layout_pagination_list_film_linear_layout)
        linearLayout.visibility = View.GONE
    }

    //MENU
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
            R.id.home -> {
                Toast.makeText(this,"Accueil",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MyMainActivity::class.java)
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


    inner class MinMaxFilter() : InputFilter {
        private var intMin: Int = 0
        private var intMax: Int = 0

        // Initialized
        constructor(minValue: Int, maxValue: Int) : this() {
            this.intMin = minValue
            this.intMax = maxValue
        }

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dStart: Int, dEnd: Int): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(intMin, intMax, input)) {
                    return null
                }else{
                    Toast.makeText(this@ListFilmActivity, "page between 1 and $intMax", Toast.LENGTH_SHORT).show()
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }
        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a) c in a..b else c in b..a
        }
    }

    private fun synchro(query: String?, page: Int) {

        val retrofit:Retrofit = ApiTMDb.getInstance()

        val service = retrofit.create(TMDbService::class.java)
        runBlocking {
            val filmRes = service.getFilms("8a001113b1bd015692aa418a6bf9a18b",query,page)

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
            val layout = findViewById<LinearLayout>(R.id.layout_pagination_list_film_linear_layout)

            if (films.size == 0) {
                Toast.makeText(this@ListFilmActivity, "No results found!", Toast.LENGTH_SHORT).show()
                layout.visibility = View.GONE
            }
            else
            {
                layout.visibility = View.VISIBLE
            }

            //pagination
            val pageTextView = findViewById<TextView>(R.id.page_list_film_TextView)
            pageTextView.text = String.format("%s-%s of %s", ((filmRes.page - 1) * 20 + 1).toString(),((filmRes.page - 1) * 20 + (films.size)).toString(),filmRes.total_results)

            lastPage = filmRes.total_pages

            val previousImgBtn = findViewById<ImageButton>(R.id.previous_page_list_film_imageButton)
            val firstPageImgBtn = findViewById<ImageButton>(R.id.first_page_list_film_imageButton)
            if (filmRes.page == 1){
                previousImgBtn.visibility = View.GONE
                firstPageImgBtn.visibility = View.GONE
            }else{
                previousImgBtn.visibility = View.VISIBLE
                firstPageImgBtn.visibility = View.VISIBLE
            }

            val nextImgBtn = findViewById<ImageButton>(R.id.next_page_list_film_imageButton)
            val lastPageImgBtn = findViewById<ImageButton>(R.id.last_page_list_film_imageButton)

            if (filmRes.page == filmRes.total_pages){
                nextImgBtn.visibility = View.GONE
                lastPageImgBtn.visibility = View.GONE
            }else{
                nextImgBtn.visibility = View.VISIBLE
                lastPageImgBtn.visibility = View.VISIBLE
            }

            // Assigning filters
            val pageEdittext = findViewById<EditText>(R.id.page_list_film_edittext)
            pageEdittext.filters = arrayOf<InputFilter>(MinMaxFilter(1, filmRes.total_pages))



            recyclerView.adapter = FilmAdapter(films, this@ListFilmActivity)
        }

    }
}