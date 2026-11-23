package com.example.lumiere

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lumiere.databinding.FragmentSearchBinding
import com.example.lumiere.ui.MovieAdapter
import com.example.lumiere.ui.SearchState
import com.example.lumiere.ui.SearchViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private val adapter = MovieAdapter { movie ->
        findNavController().navigate(
            R.id.detailFragment,
            bundleOf("movieId" to movie.id)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvResults.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvResults.adapter = adapter

        binding.etSearch.doAfterTextChanged {
            viewModel.onQueryChanged(it?.toString().orEmpty())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { render(it) }
            }
        }
    }

    private fun render(state: SearchState) {
        binding.progress.visibility =
            if (state is SearchState.Loading) View.VISIBLE else View.GONE

        val message = when (state) {
            is SearchState.Idle -> "Digite o nome de um filme"
            is SearchState.Empty -> "Nenhum filme encontrado"
            is SearchState.Error -> "Erro ao buscar. Verifique sua conexão."
            else -> null
        }
        binding.tvMessage.text = message
        binding.tvMessage.visibility = if (message != null) View.VISIBLE else View.GONE

        if (state is SearchState.Success) {
            adapter.submitList(state.movies)
        } else if (state !is SearchState.Loading) {
            adapter.submitList(emptyList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}