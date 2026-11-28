package com.example.lumiere

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.lumiere.databinding.DialogResetPasswordBinding
import com.example.lumiere.databinding.FragmentLoginBinding
import com.example.lumiere.ui.AuthState
import com.example.lumiere.ui.AuthViewModel
import com.example.lumiere.ui.ResetState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnForgot.setOnClickListener { showResetDialog() }
        binding.btnLogin.setOnClickListener {
            viewModel.login(
                binding.etEmail.text.toString().trim(),
                binding.etPassword.text.toString()
            )
        }
        binding.btnGoRegister.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.state.collect { render(it) } }
                launch { viewModel.resetState.collect { renderReset(it) } }
            }
        }
    }

    private fun showResetDialog() {
        val dialogBinding = DialogResetPasswordBinding.inflate(layoutInflater)
        dialogBinding.etResetEmail.setText(binding.etEmail.text)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Redefinir senha")
            .setView(dialogBinding.root)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Enviar link") { _, _ ->
                viewModel.sendPasswordReset(dialogBinding.etResetEmail.text.toString().trim())
            }
            .show()
    }

    private fun renderReset(state: ResetState) {
        val message = when (state) {
            is ResetState.Sent ->
                "Se o e-mail estiver cadastrado, você receberá um link para redefinir a senha."
            is ResetState.Error -> state.message
            else -> null
        }
        if (message != null) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            viewModel.consumeResetState()
        }
    }

    private fun render(state: AuthState) {
        val loading = state is AuthState.Loading
        binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !loading

        val error = (state as? AuthState.Error)?.message
        binding.tvError.text = error
        binding.tvError.visibility = if (error != null) View.VISIBLE else View.GONE

        if (state is AuthState.Success) {
            Toast.makeText(requireContext(), "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
            viewModel.consumeState()
            findNavController().popBackStack(R.id.profileFragment, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}