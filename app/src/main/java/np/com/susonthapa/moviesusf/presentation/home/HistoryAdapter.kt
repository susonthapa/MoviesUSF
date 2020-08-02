package np.com.susonthapa.moviesusf.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import np.com.susonthapa.moviesusf.databinding.HistoryItemLayoutBinding
import np.com.susonthapa.moviesusf.databinding.MoviesItemLayoutBinding
import np.com.susonthapa.moviesusf.domain.Movies

/**
 * Created by suson on 8/2/20
 */

class HistoryAdapter : ListAdapter<Movies, HistoryItemViewHolder>(SearchResultItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        val binding =
            HistoryItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class HistoryItemViewHolder(private val binding: HistoryItemLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Movies) {
        binding.apply {
            movieHistoryImage.setImageURI(item.image)
        }
    }

}
