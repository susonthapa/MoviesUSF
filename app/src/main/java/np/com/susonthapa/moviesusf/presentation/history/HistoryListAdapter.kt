package np.com.susonthapa.moviesusf.presentation.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import np.com.susonthapa.moviesusf.databinding.HistoryGridItemLayoutBinding
import np.com.susonthapa.moviesusf.databinding.HistoryListItemLayoutBinding
import np.com.susonthapa.moviesusf.domain.Movies
import np.com.susonthapa.moviesusf.presentation.home.SearchResultItemCallback

/**
 * Created by suson on 8/2/20
 */

class HistoryListAdapter constructor(private val removeListener: (position: Int) -> Unit) : ListAdapter<Movies, HistoryListItemViewHolder>(SearchResultItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryListItemViewHolder {
        val binding =
            HistoryListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryListItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryListItemViewHolder, position: Int) {
        holder.bind(getItem(position), removeListener)
    }

}

class HistoryListItemViewHolder(private val binding: HistoryListItemLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Movies, removeListener: (position: Int) -> Unit) {
        binding.apply {
            movieTitle.text = item.title
            movieType.text = item.type
            movieImage.setImageURI(item.image)
            movieYear.text = item.year
            movieRemoveFromHistory.setOnClickListener {
                removeListener(adapterPosition)
            }
        }
    }

}
