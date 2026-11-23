package com.example.lumiere

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.lumiere.databinding.FragmentDetailBinding
import com.example.lumiere.ui.DetailState
import com.example.lumiere.ui.DetailViewModel
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnRetry.setOnClickListener { viewModel.load() }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { render(it) }
            }
        }
    }

    private fun render(state: DetailState) {
        binding.progress.visibility =
            if (state is DetailState.Loading) View.VISIBLE else View.GONE
        binding.errorGroup.visibility =
            if (state is DetailState.Error) View.VISIBLE else View.GONE
        binding.content.visibility =
            if (state is DetailState.Success) View.VISIBLE else View.GONE

        if (state is DetailState.Success) {
            val m = state.movie
            binding.tvTitle.text = m.title

            val year = m.releaseDate?.take(4).orEmpty()
            val runtime = m.runtime?.takeIf { it > 0 }
                ?.let { "${it / 60}h ${"%02d".format(it % 60)}min" }
            binding.tvInfo.text = listOfNotNull(
                year.ifBlank { null }, runtime, "★ %.1f".format(m.voteAverage)
            ).joinToString("  •  ")

            binding.tvGenres.text = m.genres.joinToString(", ") { it.name }
            binding.tvOverview.text =
                m.overview?.ifBlank { null } ?: "Sinopse indisponível."
            binding.tvCast.text = m.credits?.cast?.take(8)
                ?.joinToString("\n") { cast ->
                    if (cast.character.isNullOrBlank()) cast.name
                    else "${cast.name} como ${cast.character}"
                }
                ?.ifBlank { null } ?: "Elenco indisponível."

            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500${m.posterPath}")
                .into(binding.ivPoster)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}