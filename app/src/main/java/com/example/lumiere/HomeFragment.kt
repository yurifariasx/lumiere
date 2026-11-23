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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lumiere.databinding.FragmentHomeBinding
import com.example.lumiere.ui.HomeState
import com.example.lumiere.ui.HomeViewModel
import com.example.lumiere.ui.MovieAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val adapter = MovieAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvMovies.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvMovies.adapter = adapter
        binding.btnRetry.setOnClickListener { viewModel.load() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progress.visibility =
                        if (state is HomeState.Loading) View.VISIBLE else View.GONE
                    binding.errorGroup.visibility =
                        if (state is HomeState.Error) View.VISIBLE else View.GONE
                    if (state is HomeState.Success) adapter.submitList(state.movies)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}