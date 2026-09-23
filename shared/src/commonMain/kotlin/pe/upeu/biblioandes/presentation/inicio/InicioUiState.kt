package pe.upeu.biblioandes.presentation.inicio

import pe.upeu.biblioandes.presentation.prestamos.PrestamoUi

sealed interface InicioUiState {
    data object Cargando : InicioUiState

    /** [destacado] es null cuando no hay préstamos pendientes (estado vacío). */
    data class Contenido(val primerNombre: String, val destacado: PrestamoUi?) : InicioUiState

    data class Error(val mensaje: String) : InicioUiState
}
