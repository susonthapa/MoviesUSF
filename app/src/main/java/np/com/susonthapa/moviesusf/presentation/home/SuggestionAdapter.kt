package np.com.susonthapa.moviesusf.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import np.com.susonthapa.moviesusf.databinding.SuggestionListItemBinding

/**
 * Created by suson on 8/28/20
 */

class SuggestionAdapter(private val clickListener: (String) -> Unit) : ListAdapter<String, SuggestionListViewHolder>(SuggestionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionListViewHolder {
        val binding = SuggestionListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SuggestionListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuggestionListViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

}


class SuggestionListViewHolder(private val binding: SuggestionListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: String, clickListener: (String) -> Unit) {
        binding.suggestionText.text = item
        binding.root.setOnClickListener {
            clickListener(item)
        }
    }

}

class SuggestionDiffCallback : DiffUtil.ItemCallback<String>() {

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

}