package fr.epf.min1.android_project

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epf.min1.android_project.model.Film
import java.io.InputStream
import java.net.URL

class FilmViewHolder(val view: View): RecyclerView.ViewHolder(view)

class FilmAdapter(val films: List<Film>,val context: Context): RecyclerView.Adapter<FilmViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.film_view, parent, false)
        return FilmViewHolder(view)
    }

    override fun getItemCount(): Int = films.size

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {

        val film : Film = films[position]
        val view = holder.itemView

        //set title
        val titleTextView = view.findViewById<TextView>(R.id.title_detail_film_textView)
        titleTextView.text = film.title

        //set Rating
        val ratingTextView = view.findViewById<TextView>(R.id.rating_detail_film_textView)
        ratingTextView.text = film.popularity.toString()

        //set image
        val posterImageView = view.findViewById<ImageView>(R.id.film_view_imageview)
        Glide.with(view).load("https://image.tmdb.org/t/p/original/" + film.poster_path).into(posterImageView)

        //set action listener
        val cardView = view.findViewById<CardView>(R.id.film_view_cardview)
        cardView.setOnClickListener {
            val intent = Intent(context, DetailsFilmActivity::class.java)
            intent.putExtra("film_id", film.id)
//            intent.putExtra("client", client)
            context.startActivity(intent)
        }
    }
}