package pe.upeu.biblioandes.presentation.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.usecase.ObtenerCupoPrestamosUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerLibroUseCase
import pe.upeu.biblioandes.domain.usecase.SolicitarPrestamoUseCase

/**
 * Detalle de un libro. No decide si el préstamo procede: se lo pregunta a
 * [SolicitarPrestamoUseCase] y muestra el mensaje que el dominio devuelve.
 */
class DetalleLibroViewModel(
    private val libroId: Int,
    private val obtenerLibro: ObtenerLibroUseCase,
    private val solicitarPrestamo: SolicitarPrestamoUseCase,
    private val obtenerCupo: ObtenerCupoPrestamosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleLibroUiState())
    val uiState: StateFlow<DetalleLibroUiState> = _uiState.asStateFlow()

    init {
        cargar()
        actualizarCupo()
    }

    /** RN-01: si ya hay 3 préstamos activos, el botón de solicitar se deshabilita. */
    private fun actualizarCupo() {
        viewModelScope.launch {
            obtenerCupo().onSuccess { cupo -> _uiState.update { it.copy(limiteAlcanzado = cupo.limiteAlcanzado) } }
        }
    }

    fun cargar(mostrarCarga: Boolean = true) {
        if (mostrarCarga) _uiState.update { it.copy(fase = FaseDetalle.Cargando) }
        viewModelScope.launch {
            obtenerLibro(libroId)
                .onSuccess { libro -> _uiState.update { it.copy(fase = FaseDetalle.Contenido(libro)) } }
                .onFailure { _uiState.update { it.copy(fase = FaseDetalle.NoEncontrado) } }
        }
    }

    fun onSolicitarClick() = _uiState.update { it.copy(mostrarConfirmacion = true) }

    fun onCancelar() = _uiState.update { it.copy(mostrarConfirmacion = false) }

    fun onConfirmar() {
        val libro = (_uiState.value.fase as? FaseDetalle.Contenido)?.libro ?: return
        if (_uiState.value.solicitando) return
        _uiState.update { it.copy(mostrarConfirmacion = false, solicitando = true) }

        viewModelScope.launch {
            solicitarPrestamo(libro)
                .onSuccess { prestamo ->
                    _uiState.update {
                        it.copy(
                            solicitando = false,
                            mensaje = "Préstamo registrado. Devuélvelo hasta el ${prestamo.fechaLimite}"
                        )
                    }
                    cargar(mostrarCarga = false) // actualiza los ejemplares disponibles
                    actualizarCupo()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(solicitando = false, mensaje = error.message ?: "No se pudo registrar el préstamo")
                    }
                }
        }
    }

    fun onMensajeMostrado() = _uiState.update { it.copy(mensaje = null) }
}
