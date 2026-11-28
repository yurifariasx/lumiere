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
import com.example.lumiere.databinding.FragmentProfileBinding
import com.example.lumiere.ui.ProfileViewModel
import com.example.lumiere.ui.SessionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    private val sessionViewModel: SessionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sair da conta")
                .setMessage("Deseja realmente sair?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Sair") { _, _ -> sessionViewModel.signOut() }
                .show()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stats.collect { stats ->
                        binding.tvWatched.text = stats.watched.toString()
                        binding.tvWant.text = stats.wantToWatch.toString()
                        binding.tvFavorites.text = stats.favorites.toString()
                        binding.tvAverage.text =
                            stats.averageRating?.let { "★ %.1f".format(it) } ?: "—"
                    }
                }
                launch { sessionViewModel.user.collect { renderUser(it) } }
            }
        }
    }

    private fun renderUser(user: FirebaseUser?) {
        val loggedIn = user != null
        binding.groupLoggedOut.visibility = if (loggedIn) View.GONE else View.VISIBLE
        binding.groupLoggedIn.visibility = if (loggedIn) View.VISIBLE else View.GONE
        binding.tvEmail.text = user?.email.orEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}