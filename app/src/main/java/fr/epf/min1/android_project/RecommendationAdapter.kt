package fr.epf.min1.android_project

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epf.min1.android_project.model.Film

class RecommendationViewHolder(val view: View): RecyclerView.ViewHolder(view)


class RecommendationAdapter(val films: List<Film>, val context: Context): RecyclerView.Adapter<RecommendationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.recommendation_view, parent, false)
        return RecommendationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        val film : Film = films[position]
        val view = holder.itemView

        //set image
        val posterImageView = view.findViewById<ImageView>(R.id.poster_image_image_view)
        Glide.with(view).load("https://image.tmdb.org/t/p/original/" + film.poster_path).into(posterImageView)

        //set film name
        val movieNameTextView = view.findViewById<TextView>(R.id.movie_name_recommendation_view)
        movieNameTextView.text = film.title

        //on clickListener
        posterImageView.setOnClickListener {
            val intent = Intent(context, DetailsFilmActivity::class.java)
            intent.putExtra("film_id", film.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = films.size

}