package pe.upeu.biblioandes.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.usecase.ObtenerCupoPrestamosUseCase

/** Cuenta los préstamos activos para el badge de la barra inferior (RN-01). */
class BarraInferiorViewModel(
    private val obtenerCupo: ObtenerCupoPrestamosUseCase
) : ViewModel() {

    private val _activos = MutableStateFlow(0)
    val activos: StateFlow<Int> = _activos.asStateFlow()

    fun actualizar() {
        viewModelScope.launch {
            obtenerCupo().onSuccess { _activos.value = it.activos }
        }
    }
}
