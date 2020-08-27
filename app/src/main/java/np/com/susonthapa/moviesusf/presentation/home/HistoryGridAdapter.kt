package np.com.susonthapa.moviesusf.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import np.com.susonthapa.moviesusf.databinding.HistoryGridItemLayoutBinding
import np.com.susonthapa.moviesusf.databinding.MoviesItemLayoutBinding
import np.com.susonthapa.moviesusf.domain.Movies

/**
 * Created by suson on 8/2/20
 */

class HistoryAdapter : ListAdapter<Movies, HistoryGridItemViewHolder>(SearchResultItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryGridItemViewHolder {
        val binding =
            HistoryGridItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryGridItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryGridItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class HistoryGridItemViewHolder(private val binding: HistoryGridItemLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Movies) {
        binding.apply {
            movieHistoryImage.setImageURI(item.image)
        }
    }

}
