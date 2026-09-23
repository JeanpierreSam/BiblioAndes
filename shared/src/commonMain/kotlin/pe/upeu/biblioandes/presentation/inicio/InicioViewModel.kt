package pe.upeu.biblioandes.presentation.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.usecase.ObtenerResumenInicioUseCase
import pe.upeu.biblioandes.presentation.prestamos.aUi

class InicioViewModel(
    private val obtenerResumen: ObtenerResumenInicioUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<InicioUiState>(InicioUiState.Cargando)
    val uiState: StateFlow<InicioUiState> = _uiState.asStateFlow()

    fun cargar() {
        _uiState.value = InicioUiState.Cargando
        viewModelScope.launch {
            obtenerResumen()
                .onSuccess { resumen ->
                    _uiState.value = InicioUiState.Contenido(
                        primerNombre = resumen.estudiante.nombre.substringBefore(' '),
                        destacado = resumen.proximoAVencer?.aUi()
                    )
                }
                .onFailure { error ->
                    _uiState.value = InicioUiState.Error(error.message ?: "No se pudo cargar el inicio")
                }
        }
    }
}
