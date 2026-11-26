package com.example.lumiere.ui

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface AuthState {
    data object Idle : AuthState
    data object Loading : AuthState
    data object Success : AuthState
    data class Error(val message: String) : AuthState
}

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun login(email: String, password: String) {
        validate(email, password)?.let {
            _state.value = AuthState.Error(it)
            return
        }
        _state.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            _state.value = if (task.isSuccessful) AuthState.Success
            else AuthState.Error(messageFor(task.exception))
        }
    }

    fun register(email: String, password: String, confirm: String) {
        val error = validate(email, password)
            ?: if (password != confirm) "As senhas não conferem." else null
        if (error != null) {
            _state.value = AuthState.Error(error)
            return
        }
        _state.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            _state.value = if (task.isSuccessful) AuthState.Success
            else AuthState.Error(messageFor(task.exception))
        }
    }

    fun consumeState() {
        _state.value = AuthState.Idle
    }

    private fun validate(email: String, password: String): String? = when {
        email.isBlank() -> "Informe o e-mail."
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "E-mail inválido."
        password.isBlank() -> "Informe a senha."
        password.length < 6 -> "A senha deve ter pelo menos 6 caracteres."
        else -> null
    }

    private fun messageFor(e: Exception?): String = when (e) {
        is FirebaseAuthWeakPasswordException -> "Senha muito fraca. Use pelo menos 6 caracteres."
        is FirebaseAuthUserCollisionException -> "Já existe uma conta com este e-mail."
        is FirebaseAuthInvalidUserException,
        is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha incorretos."
        is FirebaseNetworkException -> "Sem conexão. Verifique sua internet."
        else -> "Não foi possível concluir. Tente novamente."
    }
}