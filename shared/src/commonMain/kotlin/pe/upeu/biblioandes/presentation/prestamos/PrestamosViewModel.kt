package pe.upeu.biblioandes.presentation.prestamos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.usecase.DevolverPrestamoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase

/** Los préstamos llegan ya ordenados y con su estado calculado por el dominio. */
class PrestamosViewModel(
    private val obtenerPrestamos: ObtenerPrestamosUseCase,
    private val devolverPrestamo: DevolverPrestamoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamosUiState())
    val uiState: StateFlow<PrestamosUiState> = _uiState.asStateFlow()

    private var prestamos: List<Prestamo> = emptyList()

    fun cargar() {
        _uiState.update { it.copy(fase = FasePrestamos.Cargando) }
        viewModelScope.launch {
            obtenerPrestamos()
                .onSuccess {
                    prestamos = it
                    aplicarFiltro()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(fase = FasePrestamos.Error(error.message ?: "No se pudieron cargar tus préstamos"))
                    }
                }
        }
    }

    fun onFiltroSeleccionado(filtro: FiltroEstado) {
        _uiState.update { it.copy(filtro = filtro) }
        val fase = _uiState.value.fase
        if (fase is FasePrestamos.Contenido || fase is FasePrestamos.Vacio) aplicarFiltro()
    }

    /** RN-04: regulariza el préstamo y vuelve a cargar la lista. */
    fun onDevolver(prestamoId: Int) {
        _uiState.update { it.copy(fase = FasePrestamos.Cargando) }
        viewModelScope.launch {
            devolverPrestamo(prestamoId)
            cargar()
        }
    }

    private fun aplicarFiltro() {
        val filtro = _uiState.value.filtro
        val visibles = prestamos.filter { filtro.admite(it.estado) }.map { it.aUi() }
        val fase = if (visibles.isEmpty()) {
            FasePrestamos.Vacio(
                if (filtro == FiltroEstado.TODOS) "Aún no has solicitado libros"
                else "No tienes préstamos en estado ${filtro.etiqueta}"
            )
        } else {
            FasePrestamos.Contenido(visibles)
        }
        _uiState.update { it.copy(fase = fase) }
    }
}
