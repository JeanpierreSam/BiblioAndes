package pe.upeu.biblioandes.presentation.catalogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.usecase.FiltrarCatalogoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerCatalogoUseCase

/**
 * Guarda el catálogo completo y publica solo los libros que pasan los filtros.
 * No filtra por su cuenta: delega en [FiltrarCatalogoUseCase].
 */
class CatalogoViewModel(
    private val obtenerCatalogo: ObtenerCatalogoUseCase,
    private val filtrarCatalogo: FiltrarCatalogoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogoUiState())
    val uiState: StateFlow<CatalogoUiState> = _uiState.asStateFlow()

    private var todosLosLibros: List<Libro> = emptyList()

    fun cargar() {
        _uiState.update { it.copy(fase = FaseCatalogo.Cargando) }
        viewModelScope.launch {
            obtenerCatalogo()
                .onSuccess { catalogo ->
                    todosLosLibros = catalogo.libros
                    _uiState.update { it.copy(categorias = catalogo.categorias) }
                    aplicarFiltros()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(fase = FaseCatalogo.Error(error.message ?: "No se pudo cargar el catálogo"))
                    }
                }
        }
    }

    fun onCategoriaSeleccionada(categoria: String?) {
        _uiState.update { it.copy(categoriaSeleccionada = categoria) }
        aplicarFiltros()
    }

    fun onConsultaChange(consulta: String) {
        _uiState.update { it.copy(consulta = consulta) }
        aplicarFiltros()
    }

    fun onSoloDisponiblesChange(activo: Boolean) {
        _uiState.update { it.copy(soloDisponibles = activo) }
        aplicarFiltros()
    }

    /** Recalcula la lista visible; no hace nada mientras carga o si hubo error. */
    private fun aplicarFiltros() {
        val estado = _uiState.value
        if (estado.fase is FaseCatalogo.Cargando && todosLosLibros.isEmpty()) return
        if (estado.fase is FaseCatalogo.Error) return
        val visibles = filtrarCatalogo(
            todosLosLibros, estado.categoriaSeleccionada, estado.consulta, estado.soloDisponibles
        )
        _uiState.update {
            it.copy(fase = if (visibles.isEmpty()) FaseCatalogo.Vacio else FaseCatalogo.Contenido(visibles))
        }
    }
}
