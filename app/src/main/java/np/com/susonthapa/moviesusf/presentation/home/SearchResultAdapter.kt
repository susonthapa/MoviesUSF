package np.com.susonthapa.moviesusf.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import np.com.susonthapa.moviesusf.databinding.MoviesItemLayoutBinding
import np.com.susonthapa.moviesusf.domain.Movies

/**
 * Created by suson on 8/2/20
 */

class SearchResultAdapter constructor(private val clickListener: SearchResultClickListener) :
    ListAdapter<Movies, SearchResultViewHolder>(SearchResultItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = MoviesItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

}

class SearchResultItemCallback : DiffUtil.ItemCallback<Movies>() {

    override fun areItemsTheSame(oldItem: Movies, newItem: Movies): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movies, newItem: Movies): Boolean {
        return oldItem == newItem
    }

}

class SearchResultViewHolder(private val binding: MoviesItemLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Movies, clickListener: SearchResultClickListener) {
        binding.apply {
            root.transitionName = item.id
            root.setOnClickListener {
                // set the shared element transition name
                clickListener.onResultClick(adapterPosition, root)
            }

            movieTitle.text = item.title
            movieYear.text = item.year
            movieType.text = item.type
            movieImage.setImageURI(item.image)

            movieAddToHistory.setOnClickListener {
                clickListener.onAddToHistory(adapterPosition)
            }
        }
    }
}

interface SearchResultClickListener {
    fun onResultClick(position: Int, sharedView: View)
    fun onAddToHistory(position: Int)
}