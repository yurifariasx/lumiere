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
import com.example.lumiere.databinding.FragmentProfileBinding
import com.example.lumiere.ui.ProfileViewModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stats.collect { stats ->
                    binding.tvWatched.text = stats.watched.toString()
                    binding.tvWant.text = stats.wantToWatch.toString()
                    binding.tvFavorites.text = stats.favorites.toString()
                    binding.tvAverage.text =
                        stats.averageRating?.let { "★ %.1f".format(it) } ?: "—"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}