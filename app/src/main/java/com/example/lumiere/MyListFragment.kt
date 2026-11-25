package com.example.lumiere

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lumiere.databinding.FragmentMyListBinding
import com.example.lumiere.ui.EntryAdapter
import com.example.lumiere.ui.MyListViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class MyListFragment : Fragment() {

    private var _binding: FragmentMyListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyListViewModel by viewModels()

    private val adapter = EntryAdapter { entry ->
        findNavController().navigate(
            R.id.detailFragment,
            bundleOf("movieId" to entry.movieId)
        )
    }

    private val emptyMessages = listOf(
        "Você ainda não marcou nenhum filme como visto.",
        "Sua lista de filmes para ver está vazia.",
        "Você ainda não favoritou nenhum filme."
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvEntries.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvEntries.adapter = adapter

        binding.tabs.getTabAt(viewModel.selectedTab.value)?.select()
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.selectTab(tab.position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.entries.collect { list ->
                    adapter.submitList(list)
                    binding.tvEmpty.text = emptyMessages[viewModel.selectedTab.value]
                    binding.tvEmpty.visibility =
                        if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}