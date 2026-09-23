package pe.upeu.biblioandes.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.usecase.ObtenerEstudianteUseCase

sealed interface PerfilUiState {
    data object Cargando : PerfilUiState
    data class Contenido(val estudiante: Estudiante) : PerfilUiState
    data class Error(val mensaje: String) : PerfilUiState
}

class PerfilViewModel(
    private val obtenerEstudiante: ObtenerEstudianteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PerfilUiState>(PerfilUiState.Cargando)
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            obtenerEstudiante()
                .onSuccess { _uiState.value = PerfilUiState.Contenido(it) }
                .onFailure { _uiState.value = PerfilUiState.Error(it.message ?: "No se pudo cargar el perfil") }
        }
    }
}
