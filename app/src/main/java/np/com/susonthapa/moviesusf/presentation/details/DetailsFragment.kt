package np.com.susonthapa.moviesusf.presentation.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import np.com.susonthapa.core.ui.common.BaseFragment
import np.com.susonthapa.moviesusf.R
import np.com.susonthapa.moviesusf.databinding.FragmentDetailsBinding

class DetailsFragment : BaseFragment() {

    private val binding: FragmentDetailsBinding
        get() = _binding!! as FragmentDetailsBinding

    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transform = MaterialContainerTransform()
        sharedElementEnterTransition = transform
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        binding.root.transitionName = args.sharedElement

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            detailsDraweeView.setImageURI(args.movie.image)
            detailsTitleText.text = args.movie.title
            detailsDescriptionText.text = "This is a ${args.movie.type} Movie released in ${args.movie.year}"
        }
    }

}